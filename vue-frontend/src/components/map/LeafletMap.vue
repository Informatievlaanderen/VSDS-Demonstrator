<script setup>
import KnowledgeGraph from '../graph/KnowledgeGraph.vue'
</script>

<template>
<link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"
        integrity="sha256-p4NxAoJBhIIN+hmNHrzRCf9tD/miZyoHS5obTRR9BMY=" crossorigin="" />

  <div style="height:800px; width:100%" id="map"></div>


<!--  <div style="width: 100%;">-->
<!--    <div style="float:right;">-->
<!--&lt;!&ndash;      <KnowledgeGraph/>&ndash;&gt;-->
<!--    </div>-->
<!--    <div style="float:left; width: 80%">-->
<!--      <div style="height:600px; width:600px" id="map"></div>-->

<!--&lt;!&ndash;      <KnowledgeGraph/>&ndash;&gt;-->
<!--    </div>-->
<!--  </div>-->



</template>

<script>

import "leaflet/dist/leaflet.css"
import leaflet from "leaflet"
import { onBeforeUnmount } from 'vue'

import { visualiseObservation } from '@/components/map/observation'
import { observationIcon, mobilityHindranceIcon } from '@/components/map/mapConstants'

import { wktToGeoJSON } from "@terraformer/wkt"
import {visualiseMobilityHindrance} from "@/components/map/mobility-hindrance";

// We store the reference to the SSE client out here
// so we can access it from other methods
let sseClient;

export default {
  name: "ConnectionState",
  data() {
    return {
      data: [],
      map: {},
      markers: []
    };
  },

  mounted() {
    this.map = leaflet.map("map", {zoomAnimation: false}).setView([50.7747, 4.4852], 8)
    leaflet.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
      attribution: '© OpenStreetMap'
    }).addTo(this.map);

    this.requestMapUpdates();

    this.map.on("moveend", () => {
      sseClient.disconnect()
      this.requestMapUpdates()
    });
  },
  methods: {
    handleMobHind(mobilityHindrance) {

      mobilityHindrance.zones.features.forEach(feature => {
        let marker = leaflet.geoJson(feature, {
          pointToLayer: function(feature, LatLng) {
            return leaflet.marker(LatLng, {icon: mobilityHindranceIcon});
          }
        })

        this.map.addLayer(marker)
        marker.bindPopup(visualiseMobilityHindrance(mobilityHindrance), {autoPan: false})
        this.markers.push(marker)
      })
    },
    handleObservationPoints(observationPoint) {
      function getWktFromString(str){
        const matches = str.split('"');
        return matches[1] ? matches[1].split('>')[1] : str;
      }

      if (observationPoint.observations.lenght !== 0) {
        var geometry = wktToGeoJSON(getWktFromString(observationPoint.wkt));

        var geojsonFeature = {
          "type": "Feature",
          geometry
        };
        
        let marker = leaflet.geoJson(geojsonFeature, {
          pointToLayer: function(feature, LatLng) {
            return leaflet.marker(LatLng, {icon: observationIcon});
          }
        })

        this.map.addLayer(marker)
        marker.bindPopup(visualiseObservation(observationPoint.observations), {autoPan: false})
        this.markers.push(marker)
      }
    },

    requestMapUpdates() {
      this.markers.forEach(marker => this.map.removeLayer(marker))
      sseClient = this.$sse.create({
          url: 'http://localhost:3000/sse?bounds='+ JSON.stringify(this.map.getBounds()) + "&zoom=" + this.map._zoom,
          format: 'json',
          withCredentials: false,
          polyfill: true,
        });
      
      sseClient.connect().then(() => {
        
      })
      .catch((err) => {
        console.error('Failed to connect to server', err);
      });

      sseClient.on('error', (e) => {
        console.error('lost connection or failed to parse!', e);
      });
      sseClient.on('mobility-hindrance', this.handleMobHind)
      sseClient.on('observation-point', this.handleObservationPoints)
    }
  },
  setup() {
    onBeforeUnmount(() => {
      // Make sure to close the connection with the events server
      // when the component is destroyed, or we'll have ghost connections!
      sseClient.disconnect();
      // Alternatively, we could have added the `sse: { cleanup: true }` option to our component,
      // and the SSEManager would have automatically disconnected during beforeDestroy.
    })
  }

};

</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style>
table {
  font-family: arial, sans-serif;
  border-collapse: collapse;
  width: 100%;
}

td, th {
  border: 1px solid black;
  text-align: left;
  padding: 8px;
}

tr:nth-child(even) {
  background-color: #dddddd;
}
</style>
