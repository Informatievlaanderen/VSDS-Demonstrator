export class Client {
    constructor(public id: string, public response: any, public bounds: {
        _southWest: LatLng,
        _northEast: LatLng
    }, public zoom: number) { }
}

class LatLng {
    constructor(public lat: number, public lng: number) {}
}