import { Oberservation } from "./observation";

export class OberservationPoint {
    id: string;
    geometry: {lat: number, lng: number}
    wkt: string
    lane: string
    observations: Oberservation[] = []

    constructor(id: string, wkt: string, lane: string) {
        this.id = id
        this.wkt = wkt
        this.lane = lane
        let coords = wkt.split('(')[1].split(')')[0].split(' ');

        this.geometry = { lat: Number.parseFloat(coords[1]), lng: Number.parseFloat(coords[0])};
    }

    eventType() {
        return "observation-point"
    }
}