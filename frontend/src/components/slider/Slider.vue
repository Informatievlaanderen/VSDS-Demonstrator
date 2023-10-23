<script setup>
import next from "../../assets/svgs/video/next.svg"
import back from "../../assets/svgs/video/back.svg"
import play from "../../assets/svgs/video/play.svg"
import pause from "../../assets/svgs/video/pause.svg"
import {computed, ref, watch} from "vue";

const emit = defineEmits(['timestampChanged', 'realtimeToggled'])
const now = new Date();
const isRealtime = ref(true)

watch(isRealtime, (newVal, oldVal) => {
  if(newVal !== oldVal) {
    emit("realtimeToggled", newVal)
  }
})

const maxSeconds = 7 * 24 * 60 * 60;
const step = 5 * 60;

const sliderValue = ref(maxSeconds)
const isPlaying = ref(false);
let intervalId = null;

const sliderBg = computed({
  get() {
    return {background: `linear-gradient(to right, #5990de ${sliderValue.value / maxSeconds * 100}%,  #CFD5DD ${sliderValue.value / maxSeconds * 100}%)`}
  }
})

const getTime = () => now.getTime() - (maxSeconds - sliderValue.value) * 1000

function onChange() {
  emit('timestampChanged', getTime(), "PT10M")
  isRealtime.value = false
}

function onForwardClick(amount) {
  let updatedValue = +sliderValue.value + amount * 60;
  if (updatedValue < 0) {
    updatedValue = 0
  } else if (updatedValue > maxSeconds) {
    updatedValue = maxSeconds;
  }
  sliderValue.value = updatedValue;
  emit('timestampChanged', getTime(), "PT5M")
  isRealtime.value = false
}

function onShortcutClick(amount) {
  sliderValue.value = maxSeconds - amount * 60 * 60;
  emit('timestampChanged', now.getTime() - amount * 60 * 60 * 1000, "PT10M")
  isRealtime.value = false
}

function onRealTime() {
  if(!isRealtime.value) {
    sliderValue.value = maxSeconds;
    onPauseClick()
    emit('timestampChanged', now.getTime(), "PT10M")
    isRealtime.value = true
  } else {
    isRealtime.value = false;
  }
}

function onPlayClick() {
  isPlaying.value = true;
  isRealtime.value = false
  intervalId = setInterval(() => {
    if (sliderValue.value < maxSeconds) {
      sliderValue.value = +sliderValue.value + 60 * 60
    } else {
      sliderValue.value = 0;
    }
    emit("timestampChanged", getTime(), "PT1H")
  }, 500)
}

function onPauseClick() {
  isPlaying.value = false;
  clearInterval(intervalId);
}

</script>

<template>
  <div class="slider-container">
    <button class="icon-btn slider-btn" v-if="!isPlaying" @click="onPlayClick">
      <img :src="play" alt="Play"/>
    </button>
    <button class="icon-btn slider-btn" v-else @click="onPauseClick">
      <img :src="pause" alt="Pause"/>
    </button>
    <button class="icon-btn slider-btn" @click="onForwardClick(-5)" :disabled="sliderValue <= 0">
      <img :src="back" alt="Back"/>
    </button>
    <input class="slider" type="range" :style="sliderBg" min="0" :max="maxSeconds" v-model="sliderValue" :step="step"
           @input="onChange">
    <button class="icon-btn slider-btn" @click="onForwardClick(5)" :disabled="sliderValue >= maxSeconds">
      <img :src="next" alt="Next"/>
    </button>
  </div>
  <div class="shortcuts-flex-box">
    <button class="secondary-btn shadow-medium" :class="{'active': sliderValue === maxSeconds - 7 * 24 * 60 * 60}" @click="onShortcutClick(24 * 7)">-7 dagen</button>
    <button class="secondary-btn shadow-medium" :class="{'active': sliderValue === maxSeconds - 48 * 60 * 60}" @click="onShortcutClick(48)">-48 uur</button>
    <button class="secondary-btn shadow-medium" :class="{'active': sliderValue === maxSeconds - 24 * 60 * 60}" @click="onShortcutClick(24)">-24 uur</button>
    <button class="secondary-btn shadow-medium" :class="{'active': sliderValue === maxSeconds - 12 * 60 * 60}" @click="onShortcutClick(12)">-12 uur</button>
    <button class="secondary-btn shadow-medium" :class="{'active': sliderValue === maxSeconds - 60 * 60}" @click="onShortcutClick(1)">-1 uur</button>
    <button class="secondary-btn shadow-medium" :class="{'active': isRealtime}" @click="onRealTime()">Realtime</button>
  </div>
</template>

<style scoped>
.slider-btn {
  cursor: pointer;
}

.shortcuts-flex-box {
  width: 100%;
  padding: 24px 0;
  display: flex;
  justify-content: center;
  gap: 24px;
}

.slider-container {
  display: flex;
  height: 50px;
  align-items: center;
  justify-content: center;
  gap: 24px;
  background-color: #F7F9FC;
}

.slider {
  -webkit-appearance: none;
  appearance: none;
  width: 485px;
  height: 4px;
  outline: none;
}

.slider::-webkit-slider-thumb {
  -webkit-appearance: none;
  appearance: none;
  border: 0;
  border-radius: 3px;
  width: 15px;
  height: 24px;
  background: url("../../assets/svgs/range-handle.svg");
  cursor: pointer;
}

.slider::-moz-range-thumb {
  border: 0;
  border-radius: 3px;
  width: 15px;
  height: 24px;
  background: url("../../assets/svgs/range-handle.svg");
  cursor: pointer;
}

</style>