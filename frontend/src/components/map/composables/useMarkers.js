import L from "leaflet";
import marker from "../../../assets/svgs/legend/maps.marker.svg"
import altMarker from "../../../assets/svgs/legend/maps.marker-alt.svg"
import selectedMarker from "../../../assets/svgs/legend/maps.marker-1.svg"
import altSelectedMarker from "../../../assets/svgs/legend/maps.marker-alt-1.svg"
import {usePopup} from "@/components/map/composables/usePopup";

export function useMarkers(memberGeometries, collection, onMarkerClicked, onPopupClosed) {
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
            click: () => {
                if(layer.defaultOptions.icon) {
                    layer.setIcon(selectedIcon)
                }
                onMarkerClicked(feature.properties.memberId);
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
                "popupProperties": feature.properties,
                "isVersionOf": feature.isVersionOf
            }
        }
        let geoJson = L.geoJson(geoJsonFeature, {onEachFeature: onEachFeature, pointToLayer: pointToLayer})
        geoJson.setStyle({color: '#A813F7'});
        markers.push(geoJson)
    })
    return markers;
}