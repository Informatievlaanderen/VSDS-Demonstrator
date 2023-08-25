export const visualiseObservation = (observations)  => {

    if (observations.length === 0 ) {
        return "No data available yet"
    }
    
    const reducedData = observations.filter(observation => observation.measurementType === 'aantal').map(observation => {
        let speed = observations.filter(o => o.measurementType === 'snelheid')[0]
        return { vehicleType: observation.vehicleType, speed: speed.value, count: observation.value }
    }).sort(function(a, b) {
        return a.vehicleType - b.vehicleType;
    });

    console.log(reducedData)
    

    let output = 
    `
    <span>Findings from <b>${observations[0].startTime}</b> until <b>${observations[0].endTime}</b>
    <table>
        <tr>
            <th>Vehicle Type</th>
            <th>Speed</th>
            <th>Count</th>
        </tr>
    `

    reducedData.forEach(observation => {
        console.log(observation)
        output += 
        `
        <tr>
            <td>${getTypeImage(observation.vehicleType)}</tl>
            <td>${observation.speed}</tl>
            <td>${observation.count}</tl>
        </tr>
        `
    });

    output += '</table>'

    return output


}

const getTypeImage = (type) => {
    switch(type) {
        case 1:
            return `<img width="30" height="30" src="https://img.icons8.com/external-justicon-flat-justicon/64/external-motorcycle-transportation-justicon-flat-justicon.png" alt="external-motorcycle-transportation-justicon-flat-justicon"/>`
        case 2:
            return `<img width="30" height="30" src="https://img.icons8.com/external-justicon-flat-justicon/64/external-car-transportation-justicon-flat-justicon.png" alt="external-car-transportation-justicon-flat-justicon"/>`
        case 3:
            return `<img width="30" height="30" src="https://img.icons8.com/external-justicon-flat-justicon/64/external-van-transportation-justicon-flat-justicon-1.png" alt="external-van-transportation-justicon-flat-justicon-1"/>`
        case 4:
            return `<img width="30" height="30" src="https://img.icons8.com/external-justicon-flat-justicon/64/external-pickup-truck-transportation-justicon-flat-justicon-1.png" alt="external-pickup-truck-transportation-justicon-flat-justicon-1"/>`
        case 5:
            console.log(type)
            return `<img width="30" height="30" src="https://img.icons8.com/external-justicon-flat-justicon/64/external-truck-transportation-justicon-flat-justicon.png" alt="external-truck-transportation-justicon-flat-justicon"/>`

    }

}