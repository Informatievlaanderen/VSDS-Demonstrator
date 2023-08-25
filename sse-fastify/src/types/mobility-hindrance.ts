import { Store } from "n3";
import proj4 from "proj4";

const reproject =  require("reproject")
const terraformerWkt =  require("@terraformer/wkt")

export class MobilityHindrance {
    id: string;
    zones: GeoJSON.FeatureCollection
    description: string
    periode: { startTime: string, endTime: string}
    maintainer: string
    type: string = this.eventType()

    constructor(id: string, zones: GeoJSON.FeatureCollection, description: string, periode: { startTime: string, endTime: string}, maintainer: string) {
        this.id = id
        this.zones = zones
        this.description = description
        this.periode = periode
        this.maintainer = maintainer
    }

    eventType() {
        return "mobility-hindrance"
    }

    static getZones(store: Store): GeoJSON.FeatureCollection {
        const lambert = "+proj=lcc +lat_0=90 +lon_0=4.36748666666667 +lat_1=51.1666672333333 +lat_2=49.8333339 +x_0=150000.013 +y_0=5400088.438 +ellps=intl +towgs84=-106.8686,52.2978,-103.7239,-0.3366,0.457,-1.8422,-1.2747 +units=m +no_defs +type=crs";

        let collection: GeoJSON.FeatureCollection = {
            type: "FeatureCollection",
            features: []
        }

        let zoneIds = store.getObjects(null, "https://data.vlaanderen.be/ns/mobiliteit#zone", null)
        for (let zoneId in zoneIds) {
            let geometry = store.getObjects(zoneId, "http://www.w3.org/ns/locn#geometry", null)[0]
            let wkt = store.getObjects(geometry, "http://www.opengis.net/ont/geosparql#asWKT", null)[0].value
            let feature: GeoJSON.Feature = {
                type: 'Feature',
                geometry: reproject.reproject(terraformerWkt.wktToGeoJSON(wkt.split('>')[1]), lambert, proj4.WGS84),
                properties: {}
            }
            collection.features.push(feature)
        }
        return collection
    }

    static getPeriod(store: Store): { startTime: string, endTime: string} {
        let periodeId = store.getObjects(null, "https://data.vlaanderen.be/ns/mobiliteit#periode", null)[0].id
        let startTime = store.getObjects(periodeId, "http://data.europa.eu/m8g/startTime", null)[0].value
        let endTime = store.getObjects(periodeId, "http://data.europa.eu/m8g/endTime", null)[0].value

        return {startTime, endTime}
    }
}