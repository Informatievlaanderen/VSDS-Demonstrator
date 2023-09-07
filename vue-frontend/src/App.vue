<script setup>
import KnowledgeGraph from './components/graph/KnowledgeGraph.vue'
import LeafletMap from './components/map/LeafletMap.vue'
</script>

<template>

  <main>

    <div>
<!--      <div style="float:left; width: auto">-->
<!--            <KnowledgeGraph/>-->
<!--      </div>-->
      <div>
        <LeafletMap ></LeafletMap>
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