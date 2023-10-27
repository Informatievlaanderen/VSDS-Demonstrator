<template>
  <div class="member-counter-container">
    <h2 class="header header2 member-counter">{{ memberCounter }}</h2>
    <span class="body body-xxlarge-regular">datapunten</span>
  </div>
</template>

<script>
import "leaflet/dist/leaflet.css"

export default {
  name: "ConnectionState",
  props: ["stompClient"],
  data() {
    return {
      memberCounter: 0,
      subscription: null
    };
  },
  watch: {
    'stompClient.connected': {
      handler(newConnectedValue) {
        if(newConnectedValue) {
          this.subscribe();
        }
      },
      immediate: true
    }
  },
  unmounted() {
    this.unsubscribe();
  },
  methods: {
    subscribe() {
      this.stompClient.subscribe("/broker/membercounter", (memberCounter) => {
        this.memberCounter = JSON.parse(memberCounter.body)
      });
    },
    unsubscribe() {
      this.subscription?.unsubscribe();
    },
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