<script setup>
import LeafletMap from './components/map/LeafletMap.vue'
</script>

<template>

  <main>
    <h1>VSDS Demonstrator</h1>
    <p>
      De Vlaamse Smart Data Space helpt bij het <b>duurzame</b> delen van snel en traag veranderende data en hun
      contextinformatie. Hiervoor wordt data gepubliceerd als <b>Linked Data Event Streams</b>.
    </p>
    <p>
      Deze technische standaard houdt de <b>historiek</b> bij, maakt de data zelfbeschrijvend via semantische
      standaarden en interoperabel via het <b>linked data</b> principe en houdt de eindgebruiker altijd
      <b>up-to-date</b> met de bron.
    </p>

    <div>
      <LeafletMap></LeafletMap>
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