import L from "leaflet";

export function useMarkers(memberGeometries, onMarkerClicked, onPopupClosed) {
    let markers = []

    function onEachFeature(feature, layer) {
        if (feature.properties && feature.properties.popupContent) {
            let popup = L.popup().setContent(feature.properties.popupContent)
            popup.on("remove", () => onPopupClosed())
            layer.bindPopup(popup)
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
        let geoJson = L.geoJson(geoJsonFeature, {onEachFeature: onEachFeature})
        geoJson.setStyle({color: '#808080'});
        markers.push(geoJson)
    })
    return markers;
}