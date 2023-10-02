<script setup>
import LeafletMap from './components/map/LeafletMap.vue'
import GlobalHeader from "@/components/headers/GlobalHeader.vue";
import MemberCounter from './components/membercounter/MemberCounter.vue'
import LineChart from "@/components/linechart/LineChart.vue";
</script>

<template>
  <GlobalHeader/>
  <main class="container">
    <div class="content-header">
      <h1 class="header header1">VSDS Demonstrator</h1>
      <MemberCounter/>
    </div>
    <div class="body body-large-regular">
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
    <div>
      <LineChart></LineChart>
    </div>
    <hr class="divider content-separator">
    <div>
      <LeafletMap></LeafletMap>
    </div>

  </main>
</template>


<style scoped>
.content-separator {
  margin: 24px 0;
}

.content-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 24px;
  align-items: center;
}

.content-header > h1 {
  margin: 0;
}

</style>