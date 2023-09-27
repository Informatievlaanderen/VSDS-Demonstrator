import L from "leaflet";

export function useMarkers(memberGeometries, onMarkerClicked) {
    let markers = []

    function onEachFeature(feature, layer) {
        if (feature.properties && feature.properties.popupContent) {
            layer.bindPopup(feature.properties.popupContent, {remove: () => console.log("closed")});
        }
        //bind click
        layer.on({
            click: (event) => onMarkerClicked(event.sourceTarget._popup._content)
        });
    }

    memberGeometries.forEach(feature => {
        let geoJsonFeature = {
            "type": "Feature",
            "geometry": feature.geojsonGeometry,
            "properties": {
                "popupContent": feature.memberId
            }
        }
        let marker = L.geoJson(geoJsonFeature, {onEachFeature: onEachFeature})
        markers.push(marker)
    })
    return markers;
}