<template>
  <div class="checkbox-tile" :class="{ 'checked': checked }" @click="checkClicked()">
    <div class="checkmark-container">
      <img v-if="checked" class="checkmark-checked" src="../../assets/svgs/checkboxtile/check-checked.svg" alt="checkbox checked"/>
      <span v-else class="checkmark"/>
    </div>
    <h5 class="header header5">{{ label }}</h5>
  </div>
</template>

<script>
export default {
  props: {
    id: String,
    label: String,
    checked: Boolean
  },
  emits: ["onChecked"],
  methods: {
    checkClicked() {
      this.$emit('onChecked', !this.checked)
    }
  }
}
</script>

<style scoped>
.checkbox-tile {
  display: flex;
  align-items: center;
  border-radius: 3px;
  border: 2px #8695a8 solid;
  width: 100%;
  height: 58px;
  margin-bottom: 24px;
  cursor: pointer;
}

.checkbox-tile:hover, .checkbox-tile.checked {
  border: #0055CC solid 2px;
}

.checkbox-tile input {
  position: absolute;
  opacity: 0;
  cursor: pointer;
  height: 0;
  width: 0;
}

.checkmark-container {
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #8695a8;
  height: 100%;
  width: 28px;
  margin-right: 12px;
}

.checkbox-tile:hover .checkmark-container, .checkbox-tile.checked .checkmark-container {
  background-color: #0055CC;
}

.checkmark {
  border: #8695a8 solid 1px;
  width: 18px;
  height: 18px;
  border-radius: 3px;
  margin-left: -1px;
  background-color: #fff;
}

.checkmark-checked {
  border: #fff solid 1px;
  width: 18px;
  height: 18px;
  border-radius: 3px;
  margin-left: -1px;
}

</style>