<template>
  <h1>placeholder</h1>

  {{ messages }}

</template>

<script>

// We store the reference to the SSE client out here
// so we can access it from other methods
let sseClient;

export default {
  name: "ConnectionState",
  data() {
    return {
      messages: [],
      members: []
    };
  },
  mounted() {
    
    sseClient = this.$sse.create({
      url: 'http://localhost:3000/sse',
      format: 'json',
      withCredentials: false,
      polyfill: true,
    });

    // Catch any errors (ie. lost connections, etc.)
    sseClient.on('error', (e) => {
      console.error('lost connection or failed to parse!', e);

      // If this error is due to an unexpected disconnection, EventSource will
      // automatically attempt to reconnect indefinitely. You will _not_ need to
      // re-add your handlers.
    });

    // Handle 'user' messages
    sseClient.on('user', this.handleUser);

    // Handle messages without a specific event
    sseClient.on('message', this.handleMessage);

    

    // Handle once for a ban message
    sseClient.once('ban', this.handleBan);

    sseClient.connect()
      .then(() => {
        console.log('We\'re connected!');

        // Unsubscribes from event-less messages after 7 seconds
        /*
        setTimeout(() => {
          sseClient.off('message', this.handleMessage);
          console.log('Stopped listening to event-less messages!');
        }, 7000);

        // Unsubscribes from chat messages after 14 seconds
        setTimeout(() => {
          sse.off('user', this.handleChat);
          console.log('Stopped listening to chat messages!');
        }, 14000);
        */
      })
      .catch((err) => {
        // When this error is caught, it means the initial connection to the
        // events server failed.  No automatic attempts to reconnect will be made.
        console.error('Failed to connect to server', err);
      });
  },
  methods: {
    handleBan(banMessage) {
      // Note that we can access properties of message, since our parser is set to JSON
      // and the hypothetical object has a `reason` property.
      this.messages.push(`You've been banned! Reason: ${banMessage.reason}`);
    },
    handleUser(user) {
      // Note that we can access properties of message, since our parser is set to JSON
      // and the hypothetical object has these properties.
      this.messages.push(`user received : ${user}`);
    },
    handleMessage(message, lastEventId) {
      console.warn('Received a message w/o an event!', message, lastEventId);
    },
  },
  beforeUnmount() {
    // Make sure to close the connection with the events server
    // when the component is destroyed, or we'll have ghost connections!
    sseClient.disconnect();

    // Alternatively, we could have added the `sse: { cleanup: true }` option to our component,
    // and the SSEManager would have automatically disconnected during beforeDestroy.
  },
};

</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>

</style>
