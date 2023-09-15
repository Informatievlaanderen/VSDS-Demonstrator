<script setup>

const props = defineProps({time})
const now = new Date();

const maxSeconds = 7 * 24 * 60 * 60;
const step = 5 * 60;

function onChange(event) {
  props.time.value = now.getTime() - (maxSeconds - event.target.value) * 1000;
  console.log(new Date(time.value).toISOString())
}
</script>

<template>
  <div>
    <input type="range" min="0" :max="maxSeconds" :value="maxSeconds" :step="step" @input="onChange">
  </div>
  <div>
    <span>{{ new Date(time) }}</span>
  </div>
</template>