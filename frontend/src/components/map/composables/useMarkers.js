import L from "leaflet";

export function useMarkers(memberGeometries, onMarkerClicked, onPopupClosed) {
    let icon = L.icon({
        iconUrl: 'src/assets/svgs/legend/maps.marker.svg',
        iconAnchor: [10, 28],
        popupAnchor: [0, -25]

    });
    let selectedIcon = L.icon({
        iconUrl: 'src/assets/svgs/legend/maps.marker-1.svg',
        iconAnchor: [14, 40],
        popupAnchor: [0, -25]
    })
    let markers = []

    function onEachFeature(feature, layer) {
        if (feature.properties && feature.properties.popupContent) {
            let popup = L.popup().setContent(feature.properties.popupContent)
            popup.on("remove", () => {
                if(layer.defaultOptions.icon) {
                    layer.setIcon(icon)
                }
                onPopupClosed();
            })
            layer.bindPopup(popup)
        }
        //bind click
        layer.on({
            click: (event) => {
                if(layer.defaultOptions.icon) {
                    layer.setIcon(selectedIcon)
                }
                onMarkerClicked(event.sourceTarget._popup._content);
            }
        });
    }

    function pointToLayer(feature, latLng) {
        return L.marker(latLng, {icon})
    }

    memberGeometries.forEach(feature => {
        let geoJsonFeature = {
            "type": "Feature",
            "geometry": feature.geojsonGeometry,
            "properties": {
                "popupContent": feature.memberId
            }
        }
        let geoJson = L.geoJson(geoJsonFeature, {onEachFeature, pointToLayer: pointToLayer})
        geoJson.setStyle({color: '#808080'});
        markers.push(geoJson)
    })
    return markers;
}