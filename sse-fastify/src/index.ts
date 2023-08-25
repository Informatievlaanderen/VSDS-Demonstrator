import { OberservationPoint } from './types/observation-point'
import { Oberservation } from './types/observation'
import { Client } from './types/client'
import { Store, Parser } from "n3"
import { Engine } from 'quadstore-comunica';
import { MobilityHindrance } from './types/mobility-hindrance'
import { intersects  } from '@terraformer/spatial'
import { addObservationToPoint, getObservationPoints, saveObservationPoint} from "./db/observations";
import { getMobilityHindrancesForBBox, saveMobilityHindrance} from "./db/mobility-hindrances"
import { FastifySSEPlugin } from "fastify-sse-v2";

const fastify = require('fastify');

const app = fastify();

app.register(FastifySSEPlugin);

const visibleZoom = 13;

let clients: any[] = []

app.register(require('@fastify/postgres'), {
    connectionString: 'postgres://docker:docker@localhost:25432/gis'
})

app.get('/ping', (_req: any, reply: { send: (arg0: string) => void; }) => {
    reply.send("we're alive");
})

async function eventHandlers(req: any, reply: any) {
    const clientId = req.id;
    const newClient = new Client(clientId, reply, JSON.parse(req.query.bounds), req.query.zoom)

    clients.push(newClient);

    if (newClient.zoom >= visibleZoom) {
        await getObservationPoints(boundingBox(newClient), app)
            .then((observations: OberservationPoint[]) => {
                observations.forEach((oberservationPoint: OberservationPoint) => {
                    newClient.response.sse({data: JSON.stringify(oberservationPoint), event: oberservationPoint.eventType()});
                })
            })

        await getMobilityHindrancesForBBox(boundingBox(newClient), app)
            .then((mobilityHindrances: MobilityHindrance[]) => {
                mobilityHindrances.forEach((mobilityHindrance: MobilityHindrance) => {
                    newClient.response.sse({data: JSON.stringify(mobilityHindrance), event: mobilityHindrance.eventType()});
                })
            })
    } else {
        newClient.response.sse({event: 'alive'})
    }

    newClient.response.raw.on('close', () => {
        clients.unshift(newClient)
        console.log(`${clientId} Connection closed`)
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
    }

    if (eventType == 'observation') {
        handleObservation(req.body)
    }

    if (eventType == 'mobility-hindrance') {
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
        let op = new OberservationPoint(id, wkt, lane);

        saveObservationPoint(op, app)
        sendObservationPointUpdate(op)
    })
}

const sendObservationPointUpdate = (observationPoint: OberservationPoint) => {
    clients.filter(client => client.zoom >= visibleZoom)
        .filter(client => pointInBounds(client, observationPoint.geometry))
        .forEach(client => {
            client.response.sse({data: JSON.stringify(observationPoint), event: observationPoint.eventType()});
        })
}

const handleObservation = async(body: string) => {
    const parser = new Parser();
    const store = new Store();
    store.addQuads(parser.parse(body));

    let point = store.getObjects(null, "https://data.vlaanderen.be/ns/verkeersmetingen#verkeersmeetpunt", null)[0].id
    let observations: Oberservation[] = []

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

            observations.push(observation)
        })

    let observationPoint = await addObservationToPoint(observations, app)
    sendObservationPointUpdate(observationPoint)

}

const handleMobilityHindrances = (body: string) => {
    const parser = new Parser();
    const store = new Store();
    store.addQuads(parser.parse(body));

    let id = store.getObjects(null, "http://purl.org/dc/terms/isVersionOf", null)[0].value
    let description = store.getObjects(null, "http://purl.org/dc/terms/description", null)[0].value
    let maintainerSubj = store.getObjects(null,"https://data.vlaanderen.be/ns/mobiliteit#beheerder", null)[0].id
    let maintainer = store.getObjects(maintainerSubj, "http://www.w3.org/2004/02/skos/core#prefLabel", null)[0].value

    let mobilityHindrance = new MobilityHindrance(id, MobilityHindrance.getZones(store), description, MobilityHindrance.getPeriod(store), maintainer)

    saveMobilityHindrance(mobilityHindrance, app)

    clients.filter(client => client.zoom >= visibleZoom)
        .filter(client => mobilityHindranceInBounds(client, mobilityHindrance))
        .forEach(client => {
            client.response.sse({data: JSON.stringify(mobilityHindrance), event: mobilityHindrance.eventType()});
        })
}

const mobilityHindranceInBounds = (client: Client, mobilityHindrance: MobilityHindrance) => {
    const bb = boundingBox(client)
    let ok = false

    mobilityHindrance.zones.features.forEach(zone => {
        if (intersects(zone, bb)) {
            ok = true
        }
    })

    return ok
}

const pointInBounds = (client: Client, geometry: {lat: number, lng: number}) => (
    client.bounds._southWest.lat < geometry.lat && geometry.lat < client.bounds._northEast.lat &&
    client.bounds._southWest.lng < geometry.lng && geometry.lng < client.bounds._northEast.lng
);

const boundingBox = (client: Client) : GeoJSON.GeoJSON => ({
    type: "Polygon",
    coordinates: [
        [ 
            [client.bounds._northEast.lng, client.bounds._northEast.lat] , [client.bounds._northEast.lng, client.bounds._southWest.lat] , 
            [client.bounds._southWest.lng, client.bounds._southWest.lat] , [client.bounds._southWest.lng, client.bounds._northEast.lat] ,
            [client.bounds._northEast.lng, client.bounds._northEast.lat]
        ]
    ]
  })
