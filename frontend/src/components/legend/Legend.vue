<script setup>
import mapsMarker from "../../assets/svgs/legend/maps.marker.svg"
import selectedMapsMarker from "../../assets/svgs/legend/maps.marker-1.svg"
import clusterSmall from "../../assets/svgs/legend/maps.marker.cluster-small.svg"
import clusterMedium from "../../assets/svgs/legend/maps.marker.cluster-medium.svg"
import clusterLarge from "../../assets/svgs/legend/maps.marker.cluster-large.svg"
import infoFilled from "../../assets/svgs/legend/info-filled.svg"
import alertTriangleFilled from "../../assets/svgs/legend/alert-triangle-filled.svg"
import alertTriangleFilled1 from "../../assets/svgs/legend/alert-triangle-filled-1.svg"
import polygonWerken from "../../assets/svgs/legend/polygon_wegenwerken.svg"
import wegsegment from "../../assets/svgs/legend/Wegsegment.svg"
import car from "../../assets/svgs/legend/car.svg"
import shippingTruck from "../../assets/svgs/legend/shipping-truck.svg"
import LegendRow from "@/components/legend/LegendRow.vue";

const emit = defineEmits(['onCloseClick'])
</script>

<template>
  <button class="close-button" @click="emit('onCloseClick')">
    <img src="../../assets/svgs/Close_Button.svg" alt="x">
  </button>
  <h2 class="header header2">Legende</h2>
  <div class="legend-grid body body-medium-regular">
    <LegendRow :icon-src="mapsMarker" text="Meetpunt" />
    <LegendRow :icon-src="selectedMapsMarker" text="Meetpunt geselecteerd" />
    <LegendRow :icon-src="clusterSmall" text="0 - 20 meetpunten" />
    <LegendRow :icon-src="clusterMedium" text="21 - 60 meetpunten" />
    <LegendRow :icon-src="clusterLarge" text="> 60 meetpunten" />
    <LegendRow :icon-src="infoFilled" text="Geplande activiteiten" />
    <LegendRow :icon-src="alertTriangleFilled" text="Matige hinder" />
    <LegendRow :icon-src="alertTriangleFilled1" text="Zware hinder" />
    <LegendRow :icon-src="polygonWerken" text="Polygon wegenwerken" />
    <LegendRow :icon-src="wegsegment" text="Wegsegment" />
    <LegendRow :icon-src="car" text="Lichte voertuigen" />
    <LegendRow :icon-src="shippingTruck" text="Zware voertuigen" />
  </div>
  <button class="primary-btn" @click="emit('onCloseClick')">Sluiten</button>
</template>

<style scoped>
.legend-grid {
  width: 100%;
  display: grid;
  grid-template-columns: 81px auto;
  gap: 24px 36px;
  margin-bottom: 24px;
  align-items: center;
}
</style>