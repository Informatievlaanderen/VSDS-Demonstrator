<template>
  <div class="member-counter-container">
    <h2 class="header header2 member-counter">{{ memberCounter }}</h2>
    <span class="body body-xxlarge-regular">members</span>
  </div>
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
      this.stompClient = new Stomp.client(`${import.meta.env.VITE_WS_BASE_URL}/update`, {debug: false});
      this.stompClient.connect(
          {},
          () => {
            this.stompClient.subscribe("/broker/membercounter", (memberCounter) => {
              this.memberCounter = JSON.parse(memberCounter.body)
            });
          },
          error => {
            console.error(error);
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

<style scoped>
.member-counter-container {
  display: flex;
  gap: 8px;
  align-items: center;
}
.member-counter {
  color: #fff;
  background-color: #d2373c;
  border-radius: 6px;
  padding: 4px 16px;
  width: fit-content;
  display: inline;
}
</style>