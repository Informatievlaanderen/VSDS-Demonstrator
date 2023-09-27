<script setup>
import LeafletMap from './components/map/LeafletMap.vue'
import {ref} from "vue";
import KnowledgeGraph from "@/components/graph/KnowledgeGraph.vue";
const selectedMember = ref(null);
</script>

<template>

  <main>

    <div style="display: flex; justify-content: space-between">
      <div>
        <KnowledgeGraph :member-id="selectedMember"></KnowledgeGraph>
      </div>
      <div>
        <LeafletMap @marker-clicked="id => selectedMember = id"></LeafletMap>
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