import mapsMarkerAlt from "../../../assets/svgs/legend/maps.marker-alt.svg"
import carIcon from "../../../assets/svgs/legend/car.svg"
import bikeIcon from "../../../assets/svgs/legend/bike.svg"
import alertIcon from "../../../assets/svgs/legend/alert-triangle-filled.svg"

function getBikeString(number) {
    return number === "1" ? "fiets" : "fietsen";
}

export function usePopup(collection, properties) {
    switch (collection) {
        case "gipod":
            return `<div class="popup-grid body body-xxsmall-regular">
<img class="popup-grid-icon popup-gipod-icon" src="${alertIcon}">
<b class="popup-gipod-start-label">Vanaf</b><span class="popup-grid-end">${properties.starttime}</span>
<b class="popup-gipod-end-label">Tot</b><span class="popup-grid-end">${properties.endtime}</span>
</div>`
        case "verkeersmeting":
            return `<div class="popup-verkeersmeting">
<img class="popup-verkeersmeting-map-marker" src="${mapsMarkerAlt}">
<span class="popup-verkeersmeting-fullname body body-xxsmall-regular">${properties.fullname}</span>
<div class="popup-verkeersmeting-counting-row">
<img class="popup-verkeersmeting-car-icon" src="${carIcon}">
<span class="popup-verkeersmeting-counting-result body">${properties.countObservationResult ?? " - "}</span>
</div>
</div>`
        case "bluebikes":
            return `<div class="popup-grid body body-xxsmall-regular">
<img class="popup-grid-icon popup-bluebike-icon" src="${bikeIcon}">
<span class="popup-title body-small-regular">${properties.fullname}</span>
<b>${properties.capacity}</b><span class="">staanplaatsen</span>
<b>${properties.available}</b><span>beschikbare ${getBikeString(properties.available)}</span>
<b>${properties.used}</b><span>${getBikeString(properties.used)} in gebruik</span>
</div>`

    }
}