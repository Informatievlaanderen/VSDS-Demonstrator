<script setup>
const props = defineProps(["iconSrc", "text"])
</script>

<template>
  <div class="icon-holder">
    <img :src="iconSrc" alt="Legende icon">
  </div>
  <span>{{text}}</span>
</template>

<style scoped>
.icon-holder {
  display: flex;
  justify-content: center;
}
</style>