import './stored-event'
import { OberservationPoint } from './observation-point'
import { Oberservation } from './observation'
import { Client } from './client'
import { Store, Parser, NamedNode } from "n3"
import { Engine } from 'quadstore-comunica';
import { timeStamp } from 'console';

const fs = require('fs');
const fastify = require('fastify');
const fastifySse = require('fastify-sse');
const app = fastify();

/*import proj4 from "proj4";
import { reproject } from "reproject"
import { wktToGeoJSON } from "@terraformer/wkt"*/
app.register(fastifySse);


const visibleZoom = 14;

let observationPoints: OberservationPoint[] = []
let clients: any[] = []
let messages: any[] = []



app.get('/ping', (_req: any, reply: { send: (arg0: string) => void; }) => {
    reply.send(JSON.stringify(observationPoints));
})

function eventHandlers(req: any, reply: any) {
    const clientId = req.id;
    const newClient = new Client(clientId, reply, JSON.parse(req.query.bounds), req.query.zoom)

    clients.push(newClient);

    if (newClient.zoom >= visibleZoom) {
        Array.from(observationPoints.values()).filter(measuringPoint => pointInBounds(newClient, measuringPoint))
            .forEach((measuringPoint: OberservationPoint) => {
                newClient.response.sse({ data: measuringPoint }, {
                    event: measuringPoint.eventType()
                });
            })
    }

    req.raw.on('close', () => {
        console.log(`${clientId} Connection closed`)
        clients = clients.filter((client: { id: any; }) => client.id !== clientId);
    })
}

app.get('/sse', eventHandlers);

app.listen({ port: 3000 }, function () {
    console.log("App is running");
})

app.addContentTypeParser('*', { parseAs: 'string'}, function(_req: any, body: any, done: (arg0: null, arg1: any) => void) {
    done(null, body)
})

app.post('/data/:eventType', async (req: { params: { eventType: any; }; body: string; } , reply: { send: () => void; }) => {
    const { eventType } = req.params;

    if (eventType == 'observation-point') {
        handleObservationPoint(req.body)
        console.log("OP", observationPoints.length)
    }

    if (eventType == 'observation') {
        handleObservation(req.body)
    }

    if (eventType == 'mobility-hindrances') {
        handleMobilityHindrances(req.body)
    }
    

    reply.send()
});

const handleObservationPoint = async(body: string) => {
    const parser = new Parser();
    const store = new Store();
    const engine = new Engine(store as any);
    store.addQuads(parser.parse(body));

    const wktQuery = await engine.queryBindings(`
        PREFIX sp: <http://def.isotc211.org/iso19156/2011/SamplingPoint#>
        PREFIX geosparql: <http://www.opengis.net/ont/geosparql#>

        SELECT ?wkt 
        WHERE {
            ?s sp:SF_SamplingPoint.shape [
                geosparql:asWKT ?wkt 
            ]
        } 
    `);

    wktQuery.on('data', (bindings) => {
        let id = store.getObjects(null, "http://purl.org/dc/terms/isVersionOf", null)[0].id
        let lane = store.getObjects(null, "https://data.vlaanderen.be/ns/verkeersmetingen#rijstrook", null).filter(quad => quad.termType == 'Literal')[0].id.replace(/['"]+/g, '')
        let wkt = JSON.parse(bindings.toString()).wkt
        let ov = new OberservationPoint(id, wkt, lane);

        if (observationPoints.filter(o => o.id == ov.id).length === 0 ) {
            observationPoints.push(ov)

            clients.filter(client => client.zoom >= visibleZoom)
            .filter(client => pointInBounds(client, ov))
            .forEach(client => {
                client.response.sse({ data: ov }, {
                    event: ov.eventType()
                });
            })
        }
    });
}

const handleObservation = async(body: string) => {
    const parser = new Parser();
    const store = new Store();
    store.addQuads(parser.parse(body));

    let point = store.getObjects(null, "https://data.vlaanderen.be/ns/verkeersmetingen#verkeersmeetpunt", null)[0].id

    let optObservationPoint = observationPoints.filter(observationPoint => observationPoint.id == point);
    if (optObservationPoint.length != 0) {
        let observationPoint = optObservationPoint[0]
        observationPoint.observations = []

        var beginTime = store.getObjects(store.getObjects(null, "http://www.w3.org/2006/time#hasBeginning", null)[0].id, "http://www.w3.org/2006/time#inXSDDateTimeStamp", null)[0].value
        var endTime = store.getObjects(store.getObjects(null, "http://www.w3.org/2006/time#hasEnd", null)[0].id, "http://www.w3.org/2006/time#inXSDDateTimeStamp", null)[0].value

        store.getSubjects("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "https://data.vlaanderen.be/ns/verkeersmetingen#Verkeersmeting", null)
            .forEach(subject => {
                var valueTypeId = store.getObjects(subject.id, "https://data.vlaanderen.be/ns/verkeersmetingen#geobserveerdKenmerk", null)[0].id

                var observation = new Oberservation()
                observation.value = Number.parseInt(store.getObjects(subject.id, "http://def.isotc211.org/iso19156/2011/Observation#OM_Observation.result", null)[0].value)
                observation.measureType = store.getObjects(valueTypeId, "https://data.vlaanderen.be/ns/verkeersmetingen#type", null)[0].value
                observation.vehicleTypeId = Number.parseInt(store.getObjects(valueTypeId, "https://data.vlaanderen.be/ns/verkeersmetingen#voertuigType", null)[0].value)
                observation.observationPoint = point
                observation.startTime = beginTime
                observation.endTime = endTime
                observationPoint.observations.push(observation)
            })
    }
    
}

const pointInBounds = (client: Client, point: OberservationPoint) => (
    client.bounds._southWest.lat < point.geometry.lat && point.geometry.lat < client.bounds._northEast.lat &&
    client.bounds._southWest.lng < point.geometry.lng && point.geometry.lng < client.bounds._northEast.lng
);

const handleMobilityHindrances = async (body: string) => {
    const parser = new Parser();
    const store = new Store();
    const engine = new Engine(store as any);
    store.addQuads(parser.parse(body));

    var lambert = "+proj=lcc +lat_0=90 +lon_0=4.36748666666667 +lat_1=51.1666672333333 +lat_2=49.8333339 +x_0=150000.013 +y_0=5400088.438 +ellps=intl +towgs84=-106.8686,52.2978,-103.7239,-0.3366,0.457,-1.8422,-1.2747 +units=m +no_defs +type=crs";

    const wktQuery = await engine.queryBindings(`
        PREFIX mob: <https://data.vlaanderen.be/ns/mobiliteit#>
        PREFIX locn: <http://www.w3.org/ns/locn#>
        PREFIX geosparql: <http://www.opengis.net/ont/geosparql#>

        SELECT ?wkt 
        WHERE {
            ?s1 mob:Zone [
                locn:geometry [
                    geosparql:asWKT ?wkt 
                ]
            ]
        } 
    `);

    wktQuery.on('data', (bindings) => {
        console.log(bindings)
    });

    /*
        body.zone.forEach(zone => {
          var geometry = reproject(wktToGeoJSON(zone.geometry.wkt.split('>')[1]), lambert, proj4.WGS84);

          var geojsonFeature = {
            "type": "Feature",
            geometry
          };
          
          leaflet.geoJson(geojsonFeature, {
            pointToLayer: function(feature, LatLng) {
              return leaflet.marker(LatLng, {icon: mobilityHindranceIcon});
            }
          })
            .addTo(this.map)
            .bindPopup(dataEvent.data["@id"])
        }
        */
       
      
}
