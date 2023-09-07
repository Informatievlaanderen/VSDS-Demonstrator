export const visualiseMobilityHindrance = (mobilityHindrance)  => {
    return `
    <div class="card">
      
      <div class="container">
        <p>
            From: ${mobilityHindrance.periode.startTime}<br/>
            Until: ${mobilityHindrance.periode.endTime}
        </p>
        <p>${mobilityHindrance.description}</p>
      </div>
    </div>
    `
}