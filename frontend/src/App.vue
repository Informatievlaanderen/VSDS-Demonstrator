<script setup>
import LeafletMap from './components/map/LeafletMap.vue'
import GlobalHeader from "@/components/headers/GlobalHeader.vue";
import MemberCounter from './components/membercounter/MemberCounter.vue'
import LineChart from "@/components/linechart/LineChart.vue";
import {onMounted, onUnmounted, ref} from "vue";
import Stomp from "webstomp-client";

const stompClient = ref(null);

onMounted(() => connect())
onUnmounted(() => disconnect())

function connect() {
  stompClient.value = new Stomp.client("ws://localhost:8084/update", {debug: false})
  stompClient.value.connect(
      {},
      () => console.debug("Websocket connection readiness:", stompClient.value.connected),
      error => {
        console.error(error);
        connect();
      }
  )
}

function disconnect() {
  stompClient.value?.disconnect();
}

</script>

<template>
  <GlobalHeader/>
  <main class="container">
    <div class="content-header">
      <h1 class="header header1">Vlaamse Smart Data Space Demonstrator</h1>
      <MemberCounter :stomp-client="stompClient"/>
    </div>
    <div class="explanation-chart-container">
      <div class="body body-large-regular">
        <p>
          De Vlaamse Smart Data Space helpt bij het <b>duurzaam</b> delen van snel en traag veranderende data en hun
          contextinformatie. Hiervoor wordt data gepubliceerd als <b>Linked Data Event Streams</b>.
        </p>
        <p>
          Deze technische standaard houdt de <b>historiek</b> bij, maakt de data zelfbeschrijvend via semantische
          standaarden en interoperabel via het <b>linked data</b> principe en houdt de eindgebruiker altijd
          <b>up-to-date</b> met de bron.
        </p>
      </div>
      <div class="line-chart body body-xxsmall-regular">
        <LineChart :stomp-client="stompClient"></LineChart>
      </div>
    </div>
    <hr class="divider content-separator">
    <div>
      <LeafletMap :stomp-client="stompClient"></LeafletMap>
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

.explanation-chart-container {
  display: flex;
  gap: 32px;
}

.line-chart {
  width: 40%;
  height: 250px;
  min-width: 40%;
  min-height: 250px;
}

</style>