<script setup lang="ts">
import {ref} from "vue";
import CheckboxTile from "@/components/checkbox/CheckboxTile.vue";

const showLayers = ref(false);
const showLegend = ref(false);

function onMapButtonClicked(button : "layers" | "legend") {
  showLayers.value = button === "layers"
  showLegend.value = button === "legend"
}
</script>

<template>
  <div class="map-buttons">
    <button class="map-button shadow-medium" :class="{active : showLegend}" @click="onMapButtonClicked('legend')">
      <img src="../../assets/svgs/location-map.svg" alt="Legende icoon">
      <span>Legende</span>
    </button>
    <button class="map-button shadow-medium" :class="{active: showLayers}" @click="onMapButtonClicked('layers')">
      <img src="../../assets/svgs/content-layers-show.svg" alt="Lagen icoon">
      <span>Lagen</span>
    </button>
  </div>

  <Teleport to="body">
    <div v-if="showLayers" class="modal">
      <button class="close-button" @click="showLayers=false">
        <img src="../../assets/svgs/Close_Button.svg" alt="x">
      </button>
      <h2 class="header header2 margin-zero">Lagen</h2>
      <p class="body body-medium-regular margin-vert-24">
        Als decentrale oplossing maakt de Vlaamse Smart Data Space gebruik van een gezamenlijk <b>ecosysteem</b> met duidelijke spelregels, waardoor we data slim kunnen delen, publiceren en hergebruiken.
      </p>
      <CheckboxTile id="gipod" label="GIPOD" :checked="true"></CheckboxTile>
      <CheckboxTile id="traffic-counting" label="Verkeersmetingen Verkeerscentrum LDES"></CheckboxTile>
      <button class="primary-btn" @click="showLayers = false">Sluiten</button>
    </div>
  </Teleport>

  <Teleport to="body">
    <div v-if="showLegend" class="modal">
      <button class="close-button" @click="showLegend = false">
        <img src="../../assets/svgs/Close_Button.svg" alt="x">
      </button>
      <h2 class="header header2">Legende</h2>
      <button class="primary-btn" @click="showLegend = false">Sluiten</button>
    </div>
  </Teleport>
</template>

<style scoped>
.map-buttons {
  position: absolute;
  display: flex;
  flex-direction: column;
  align-items: end;
  gap: 10px;
  z-index: 10;
  width: fit-content;
  margin-right: 12px;
  right: 50%;
  bottom: 0;
  margin-bottom: 30px;
}

.map-button {
  width: 74px;
  height: 60px;
  border: none;
  background-color: #fff;
  border-radius: 3px;

  display: inline-flex;
  flex-direction: column;
  gap: 4px;
  align-items: center;
  justify-content: center;
}

.map-button.active {
  border: 1px solid #0055CC;
}

.map-button:hover {
  cursor: pointer;
}

.map-button > img {
  width: 24px;
  height: 24px;
}

.map-button > span {
  color: #0055CC;
  font-family: Flanders Art Sans, sans-serif;
  font-size: 12px;
  font-weight: 500;
  line-height: 16px;
  letter-spacing: 0;
}
.modal {
  display: flex;
  flex-direction: column;
  position: fixed;
  z-index: 999;
  top: 0;
  bottom: 0;
  right: 0;
  width: 600px;
  background-color: #fff;
  padding: 32px;
  border-left: 1px solid #cfd5dd;
}

.margin-zero {
  margin: 0;
}

.margin-vert-24 {
  margin: 24px 0;
}

.close-button {
  background: none;
  border: none;
  width: 35px;
  height: 35px;
  position: fixed;
  right: 8px;
  top: 0;
}

</style>