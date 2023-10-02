<script setup>
import LeafletMap from './components/map/LeafletMap.vue'
import GlobalHeader from "@/components/headers/GlobalHeader.vue";
</script>

<template>
  <GlobalHeader/>
  <main class="container">
    <h1 class="header1">VSDS Demonstrator</h1>
    <div class="body-large-regular">
      <p>
        De Vlaamse Smart Data Space helpt bij het <b>duurzame</b> delen van snel en traag veranderende data en hun
        contextinformatie. Hiervoor wordt data gepubliceerd als <b>Linked Data Event Streams</b>.
      </p>
      <p>
        Deze technische standaard houdt de <b>historiek</b> bij, maakt de data zelfbeschrijvend via semantische
        standaarden en interoperabel via het <b>linked data</b> principe en houdt de eindgebruiker altijd
        <b>up-to-date</b> met de bron.
      </p>
    </div>

    <hr class="divider content-separator">

    <div>
      <LeafletMap></LeafletMap>
    </div>

  </main>
</template>


<style scoped>
#app {
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}

.content-separator {
  margin: 24px 0;
}

.header1 {
  margin-bottom: 24px;
}

</style>