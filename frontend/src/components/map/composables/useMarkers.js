import L from "leaflet";
import marker from "../../../assets/svgs/legend/maps.marker.svg"
import altMarker from "../../../assets/svgs/legend/maps.marker-alt.svg"
import selectedMarker from "../../../assets/svgs/legend/maps.marker-1.svg"
import altSelectedMarker from "../../../assets/svgs/legend/maps.marker-alt-1.svg"
import {usePopup} from "@/components/map/composables/usePopup";

export function useMarkers(memberGeometries, onMarkerClicked) {
    const getIcon = (collection) => L.icon({
        iconUrl: collection === "bluebikes" ? marker : altMarker,
        iconAnchor: [10, 28],
        popupAnchor: [0, -25],

    });
    const getSelectedIcon = (collection) => L.icon({
        iconUrl: collection === "bluebikes" ? selectedMarker : altSelectedMarker,
        iconAnchor: [14, 40],
        popupAnchor: [0, -25]
    })
    let markers = []

    function onEachFeature({properties}, layer) {
        if (properties?.popupProperties) {
            let content = usePopup(properties.collection, properties.popupProperties)
            let popup = L.popup().setContent(content)
            popup.on("remove", () => {
                if (layer.defaultOptions.icon) {
                    layer.setIcon(getIcon(properties.collection))
                }
            })
            layer.bindPopup(popup)
        }
        //bind click
        layer.on({
            click: () => {
                if (layer.defaultOptions.icon) {
                    layer.setIcon(getSelectedIcon(properties.collection))
                }
                onMarkerClicked(properties);
            }
        });
    }

    function pointToLayer({properties}, latLng) {
        return L.marker(latLng, {icon: getIcon(properties.collection)})
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
        if (feature.collection === "gipod") {
            if (Date.now() < Date.parse(feature.properties.endtime)) {
                markers.push(geoJson);
            }
        } else if (feature.collection === "verkeersmeting") {
            if (!feature.isVersionOf.includes("snelheid")) {
                markers.push(geoJson);
            }
        } else {
            markers.push(geoJson)
        }
    })
    return markers;
}