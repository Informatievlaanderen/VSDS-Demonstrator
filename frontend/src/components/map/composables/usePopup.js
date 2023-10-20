import mapsMarker from "../../../assets/svgs/legend/maps.marker.svg"
import carIcon from "../../../assets/svgs/legend/car.svg"
import bikeIcon from "../../../assets/svgs/legend/bike.svg"
import alertIcon from "../../../assets/svgs/legend/alert-triangle-filled.svg"
export function usePopup(collection, properties) {
    switch (collection) {
        case "gipod":
            return `<div class="popup-grid body body-xxsmall-regular">
<img class="popup-grid-icon popup-gipod-icon" src="${alertIcon}">
<b class="popup-gipod-start-label">Vanaf</b><span class="popup-gipod-start-date">${properties.startTime}</span>
<b class="popup-gipod-end-label">Tot</b><span class="popup-gipod-end-date">${properties.endTime}</span>
</div>`
        case "verkeersmeting":
            return `<div class="popup-verkeersmeting">
<img class="popup-verkeersmeting-map-marker" src="${mapsMarker}">
<span class="popup-verkeersmeting-fullname body body-xxsmall-regular">${properties.fullName}</span>
<div class="popup-verkeersmeting-counting-row">
<img class="popup-verkeersmeting-car-icon" src="${carIcon}">
<span class="popup-verkeersmeting-counting-result body">${properties.countObservationResult ?? " - "}</span>
</div>
</div>`
        case "bluebikes":
            return `<div class="popup-grid body body-xxsmall-regular">
<img class="popup-grid-icon popup-bluebike-icon" src="${bikeIcon}">
<span class="popup-title body-small-regular">${properties.fullName}</span>
<b>${properties.capacity}</b><span class="">staanplaatsen</span>
<b>${properties.available}</b><span>beschikbare fietsen</span>
<b>${properties.used}</b><span>fietsen in gebruik</span>
</div>`

    }
}