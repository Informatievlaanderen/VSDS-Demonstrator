<script setup>
import LeafletMap from './components/map/LeafletMap.vue'
import Slider from "@/components/slider/Slider.vue";
import {ref} from "vue";

const time = ref(new Date().getTime())
</script>

<template>

  <main>

    <div>
      <!--      <div style="float:left; width: auto">-->
      <!--            <KnowledgeGraph/>-->
      <!--      </div>-->
      <div>
        <LeafletMap></LeafletMap>
      </div>

      <div class="slider-container">
        <Slider :time="time"></Slider>
      </div>
    </div>

  </main>
</template>


<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>