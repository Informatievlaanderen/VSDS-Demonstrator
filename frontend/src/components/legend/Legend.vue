<script setup>
import mapsMarker from "../../assets/svgs/legend/maps.marker.svg"
import selectedMapsMarker from "../../assets/svgs/legend/maps.marker-1.svg"
import infoFilled from "../../assets/svgs/legend/info-filled.svg"
import alertTriangleFilled from "../../assets/svgs/legend/maps.marker-1.svg"
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
    <LegendRow icon-src="src/assets/svgs/legend/info-filled.svg" text="Geplande activiteiten" />
    <LegendRow icon-src="src/assets/svgs/legend/alert-triangle-filled.svg" text="Matige hinder" />
    <LegendRow icon-src="src/assets/svgs/legend/alert-triangle-filled-1.svg" text="Zware hinder" />
    <LegendRow icon-src="src/assets/svgs/legend/polygon_wegenwerken.svg" text="Polygon wegenwerken" />
    <LegendRow icon-src="src/assets/svgs/legend/Wegsegment.svg" text="Wegsegment" />
    <LegendRow icon-src="src/assets/svgs/legend/car.svg" text="Lichte voertuigen" />
    <LegendRow icon-src="src/assets/svgs/legend/shipping-truck.svg" text="Zware voertuigen" />
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
}
</style>