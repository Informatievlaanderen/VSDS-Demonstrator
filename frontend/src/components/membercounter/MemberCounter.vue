<template>
  <h1 class="header5 centered">Aantal members: {{ memberCounter }}</h1>
</template>

<script>
import "leaflet/dist/leaflet.css"
import Stomp from "webstomp-client";

export default {
  name: "ConnectionState",
  data() {
    return {
      memberCounter: 0,
      stompClient: null
    };
  },

  mounted() {
    this.connect()
  },
  methods: {
    //websocket
    connect() {
      this.stompClient = new Stomp.client('ws://localhost:8084/update', {debug: false});
      this.stompClient.connect(
          {},
          () => {
            this.stompClient.subscribe("/broker/membercounter", (memberCounter) => {
              this.memberCounter = JSON.parse(memberCounter.body)
            });
          },
          error => {
            console.log(error);
            this.connect()
          }
      );
    },
    disconnect() {
      if (this.stompClient) {
        this.stompClient.disconnect();
      }
    }
  }

};
</script>

<style>

.centered {
  text-align: center
}
</style>