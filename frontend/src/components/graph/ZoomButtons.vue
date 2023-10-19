<script setup>

</script>

<template>
<div id="knowledge-graph-zoom-buttons" class="zoom-buttons shadow-medium" style="opacity: 0">
  <button id="knowledge-graph-zoom-in-btn" class="zoom-btn zoom-in-btn">+</button>
  <button id="knowledge-graph-zoom-out-btn" class="zoom-btn zoom-out-btn">-</button>
</div>
</template>

<style scoped>
.zoom-buttons {
  display: inline-flex;
  flex-direction: column;

  width: fit-content;
  align-self: end;

  position: absolute;
  z-index: 99;
  right: 12px;
  top: 12px;
}

.zoom-btn {
  border: none;
  background-color: #ffffff;

  width: 35px;
  height: 35px;

  color: #0055CC;
  font-size: 22px;
  text-align: center;
  line-height: 35px;
  font-weight: bold;
  font-family: "Lucida Console", "Monaco", monospace;

  cursor: pointer;
}

.zoom-in-btn {
  border-radius: 3px 3px 0 0;
}

.zoom-out-btn {
  border-radius: 0 0 3px 3px;
}
</style>