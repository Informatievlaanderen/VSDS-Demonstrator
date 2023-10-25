import L from "leaflet";
import marker from "../../../assets/svgs/legend/maps.marker.svg"
import altMarker from "../../../assets/svgs/legend/maps.marker-alt.svg"
import selectedMarker from "../../../assets/svgs/legend/maps.marker-1.svg"
import altSelectedMarker from "../../../assets/svgs/legend/maps.marker-alt-1.svg"
import {usePopup} from "@/components/map/composables/usePopup";

export function useMarkers(memberGeometries, collection, onMarkerClicked) {
    let icon = L.icon({
        iconUrl: collection === "bluebikes" ? marker : altMarker,
        iconAnchor: [10, 28],
        popupAnchor: [0, -25],

    });
    let selectedIcon = L.icon({
        iconUrl: collection === "bluebikes" ? selectedMarker : altSelectedMarker,
        iconAnchor: [14, 40],
        popupAnchor: [0, -25]
    })
    let markers = []

    function onEachFeature(feature, layer) {
        if (feature.properties?.popupProperties) {
            let content = usePopup(collection, feature.properties.popupProperties)
            let popup = L.popup().setContent(content)
            layer.bindPopup(popup)
        }
        //bind click
        layer.on({
            click: () => {
                if(layer.defaultOptions.icon) {
                    layer.setIcon(selectedIcon)
                }
                onMarkerClicked(feature.properties);
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
                "memberId": feature.memberId,
                "collection": feature.collection,
                "popupProperties": feature.properties,
                "isVersionOf": feature.isVersionOf
            }
        }
        let geoJson = L.geoJson(geoJsonFeature, {onEachFeature: onEachFeature, pointToLayer: pointToLayer})
        geoJson.setStyle({color: '#A813F7'});
        // 2023-11-24T22:59:00Z
        // console.log(Date.parse(feature.properties.endtime))
        if(collection === "gipod") {
            if(Date.now() < Date.parse(feature.properties.endtime)) {
                markers.push(geoJson);
            }
        } else {
            markers.push(geoJson)
        }
    })
    return markers;
}