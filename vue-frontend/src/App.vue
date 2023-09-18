<script setup>
import KnowledgeGraph from './components/graph/KnowledgeGraph.vue'
import LeafletMap from './components/map/LeafletMap.vue'
import Slider from "@/components/slider/Slider.vue";
import {ref} from "vue";

let time = ref(new Date().getTime())
</script>

<template>

  <main>

    <div>
<!--      <div style="float:left; width: auto">-->
<!--            <KnowledgeGraph/>-->
<!--      </div>-->
      <div>
        <LeafletMap :time="time"></LeafletMap>
      </div>

      <div class="slider-container">
        <Slider @timestamp-changed="timestamp => time = timestamp"></Slider>
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