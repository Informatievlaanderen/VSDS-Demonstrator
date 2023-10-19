import mapsMarker from "../../../assets/svgs/legend/maps.marker.svg"
import carIcon from "../../../assets/svgs/legend/car.svg"
import bikeIcon from "../../../assets/svgs/legend/bike.svg"
import alertIcon from "../../../assets/svgs/legend/alert-triangle-filled.svg"
export function usePopup(collection, properties) {
    switch (collection) {
        case "gipod":
            return `<div class="popup-gipod-grid body body-xxsmall-regular">
<img class="popup-gipod-icon" src="${alertIcon}">
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
            return `<div class="popup-verkeersmeting">
<img class="popup-verkeersmeting-map-marker" src="${bikeIcon}">
<span class="popup-verkeersmeting-fullname body body-xxsmall-regular">${"Station: " + properties.fullname}</span>
<span class="popup-verkeersmeting-counting-result body">${"Capacity: " + properties.capacity}</span>
<span class="popup-verkeersmeting-counting-result body">${"Availability: " + properties.available}</span>
<span class="popup-verkeersmeting-counting-result body">${"Usage: " + properties.used}</span>
</div>`

    }
}