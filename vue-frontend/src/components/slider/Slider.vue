<script setup>

import {ref} from "vue";

const emit = defineEmits(['timestampChanged'])
const now = new Date();

const maxSeconds = 7 * 24 * 60 * 60;
const step = 5 * 60;

const sliderValue = ref(maxSeconds)
const isPlaying = ref(false);
let intervalId = null;

const getTime = () => now.getTime() - (maxSeconds - sliderValue.value) * 1000

function onChange() {
  emit('timestampChanged', getTime(), "PT10M")
}

function onShortcutClick(amount) {
  sliderValue.value = maxSeconds - amount * 60 * 60;
  emit('timestampChanged', now.getTime() - amount * 60 * 60 * 1000, "PT10M")
}

function onPlayClick() {
  isPlaying.value = true;
  intervalId = setInterval(() => {
    if (sliderValue.value < maxSeconds) {
      sliderValue.value = +sliderValue.value + 60 * 60
      emit("timestampChanged", getTime(), "PT1H")
    } else {
      sliderValue.value = 0;
    }
  }, 500)
}

function onPauseClick() {
  isPlaying.value = false;
  clearInterval(intervalId);
}

</script>

<template>
  <div>
    <span>{{ new Date(getTime()) }}</span>
  </div>
  <div>
  </div>
  <div>
    <button @click="onPlayClick">Play</button>
    <button v-if="!isPlaying && intervalId != null" @click="onPlayClick">Resume</button>
    <button v-else @click="onPauseClick" :disabled="intervalId == null">Pause</button>

    <input type="range" min="0" :max="maxSeconds" v-model="sliderValue" :step="step" @input="onChange">
  </div>
  <div style="display: flex; justify-content: center">
    <ul class="shortcut-list">
      <li class="shortcut-item">
        <button @click="onShortcutClick(24 * 7)">7 dagen geleden</button>
      </li>
      <li class="shortcut-item">
        <button @click="onShortcutClick(48)">2 dagen geleden</button>
      </li>
      <li class="shortcut-item">
        <button @click="onShortcutClick(24)">24 uur geleden</button>

      </li>
      <li class="shortcut-item">
        <button @click="onShortcutClick(12)">12 uur geleden</button>
      </li>
      <li class="shortcut-item">
        <button @click="onShortcutClick(1)">1 uur geleden</button>
      </li>
    </ul>
  </div>

</template>

<style>
.shortcut-list {
  list-style-type: none;

  .shortcut-item {
    float: left;
  }
}
</style>