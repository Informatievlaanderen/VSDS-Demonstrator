<template>
  <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"
    integrity="sha256-p4NxAoJBhIIN+hmNHrzRCf9tD/miZyoHS5obTRR9BMY=" crossorigin="" />

  <div style="height:600px; width:100%" id="map"></div>
</template>

<script>

import "leaflet/dist/leaflet.css";
import leaflet from "leaflet"

import { wktToGeoJSON } from "@terraformer/wkt"
import proj4 from "proj4";
import { reproject } from "reproject"

import { Store, Parser } from "n3"

// We store the reference to the SSE client out here
// so we can access it from other methods
let sseClient;

class StoredEvent {
  constructor(message, event) {
    this.message = message;
    this.event = event;
  }
}

export default {
  name: "ConnectionState",
  data() {
    return {
      data: [],
      members: [],
      zoom: 8,
      map: {}
    };
  },

  mounted() {
    this.map = leaflet.map("map", {zoomAnimation: false}).setView([50.7747, 4.4852], 8)
    leaflet.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
      attribution: '© OpenStreetMap'
    }).addTo(this.map);

    console.log(this.map)

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

  
    sseClient.on('mobility-hindrances', this.handleMobHind)
    sseClient.on('observation-points', this.handleObservationPoints)

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
      this.data.push(`You've been banned! Reason: ${banMessage.reason}`);
    },
    handleMobHind(dataEvent) {
      this.data.push(new StoredEvent(dataEvent.data, "MobilityHindrance"));

      const parser = new Parser();
      const store = new Store();
      parser.parse(dataEvent.data,
        (error, quad, prefixes) => {
          if (quad)
            store.add(quad)
          else
            console.log("# That's all, folks!", prefixes);
            console.log(store)
            console.log(store.getObjects(null, null, null))
        });

      if (dataEvent.data["@type"] == "MobilityHindrance") {
        var lambert = "+proj=lcc +lat_0=90 +lon_0=4.36748666666667 +lat_1=51.1666672333333 +lat_2=49.8333339 +x_0=150000.013 +y_0=5400088.438 +ellps=intl +towgs84=-106.8686,52.2978,-103.7239,-0.3366,0.457,-1.8422,-1.2747 +units=m +no_defs +type=crs";

        dataEvent.data.zone.forEach(zone => {
          var geometry = reproject(wktToGeoJSON(zone.geometry.wkt.split('>')[1]), lambert, proj4.WGS84);

          var geojsonFeature = {
            "type": "Feature",
            geometry
          };

          leaflet.geoJson(geojsonFeature)
            .addTo(this.map)
            .bindPopup(dataEvent.data["@id"])
        }
      )}

    },
    handleObservationPoints(dataEvent) {
      this.data.push(new StoredEvent(dataEvent.data, "ObservationPoint"));

      const store = new Store();

      console.log(dataEvent.data)

      function getWktFromString(str){
        const matches = str.split('"');
        return matches[1] ? matches[1].split('>')[1] : str;
      }

      store.addQuads(new Parser().parse(dataEvent.data));

      var geometry = wktToGeoJSON(getWktFromString(store.getObjects(null, "http://www.opengis.net/ont/geosparql#asWKT", null)[0].id));

      var geojsonFeature = {
        "type": "Feature",
        geometry
      };

      leaflet.geoJson(geojsonFeature)
        .addTo(this.map)
        .bindPopup(store.getObjects(null, "http://www.w3.org/2000/01/rdf-schema#label", null)[0].id)
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
<style scoped></style>
