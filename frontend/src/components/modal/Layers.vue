<script>
import {ref} from "vue";

export default {
  setup() {
    const isOpen = ref(false)
    return {
      isOpen
    }
  }
}
</script>

<template>
  <button @click="isOpen = true">Lagen</button>
  <Teleport to="body">
    <div v-if="isOpen" class="modal">
      <h1>Lagen</h1>
      <button @click="isOpen = false">Close</button>
    </div>
  </Teleport>
</template>

<style scoped>
body {
  position: relative;
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
}
</style>