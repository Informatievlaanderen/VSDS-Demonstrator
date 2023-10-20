import mapsMarker from "../../../assets/svgs/legend/maps.marker.svg"
import carIcon from "../../../assets/svgs/legend/car.svg"
import bikeIcon from "../../../assets/svgs/legend/bike.svg"
export function usePopup(collection, properties) {
    console.log(collection)
    switch (collection) {
        case "gipod":
            return `<p>${properties.startTime}</p>`
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

/**
 * box-shadow: 0px 2px 12px 0px #6A768659;
 *
 * box-shadow: 0px 0px 1px 0px #CFD5DD;
 */