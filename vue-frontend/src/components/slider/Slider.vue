<script setup>

import {ref} from "vue";

const emit = defineEmits(['timestampChanged'])
const now = new Date();

const maxSeconds = 7 * 24 * 60 * 60;
const step = 5 * 60;

const sliderValue = ref(maxSeconds)

const getTime = () => now.getTime() - (maxSeconds - sliderValue.value) * 1000

function onChange() {
  emit('timestampChanged', getTime())
}

function onShortcutClick(amount) {
  sliderValue.value = maxSeconds - amount * 60 * 60;
  emit('timestampChanged', now.getTime() - amount * 60 * 60 * 1000)
}

</script>

<template>
  <div>
    <input type="range" min="0" :max="maxSeconds" v-model="sliderValue" :step="step" @input="onChange">
  </div>
  <div>
    <button @click="onShortcutClick(1)">1 uur geleden</button>
    <button @click="onShortcutClick(12)">12 uur geleden</button>
    <button @click="onShortcutClick(24)">24 uur geleden</button>
    <button @click="onShortcutClick(48)">2 dagen geleden</button>
    <button @click="onShortcutClick(24 * 7)">7 dagen geleden</button>
  </div>
  <div>
    <span>{{ new Date(getTime()) }}</span>
  </div>
</template>