<template>
  <div>
    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"
          integrity="sha256-p4NxAoJBhIIN+hmNHrzRCf9tD/miZyoHS5obTRR9BMY=" crossorigin=""/>

    <div style="float:left; height:500px; width:600px">
      <svg></svg>
    </div>

    <div style="float:right; height:500px; width:500px" id="map"></div>
  </div>
  <div>
    <Slider @timestamp-changed="(timestamp, period) => {
      time = timestamp;
      timePeriod = period;
    }"/>
  </div>
</template>

<script>

import "leaflet/dist/leaflet.css"
import L from 'leaflet';
import axios from 'axios'
import {useMarkers} from "@/components/map/useMarkers";
import {ref} from "vue";
import Slider from "@/components/slider/Slider.vue";
import {triplesToGraph} from "@/components/graph/functions/triplesToGraph";
import Stomp from "webstomp-client";

export default {
  components: {Slider},
  watch: {
    time: function () {
      this.fetchMembers();
    }
  },
  setup() {
    const time = ref(new Date().getTime())
    const timePeriod = ref("PT10M")

    return {
      time,
      timePeriod
    }
  },
  name: "ConnectionState",
  data() {
    return {
      data: [],
      map: {},
      markers: [],
      memberId: null,
      simulation: null
    };
  },

  mounted() {
    this.connect()

    this.map = L.map("map", {zoomAnimation: false}).setView([50.7747, 4.4852], 8)
    this.map.on('moveend', function () {

    });
    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
      attribution: '© OpenStreetMap'
    }).addTo(this.map);
    this.map.on("moveend", () => {
      this.fetchMembers();
    });
    this.fetchMembers();

  },
  methods: {
    fetchMembers() {
      axios({
        method: 'post',
        url: 'http://localhost:5173/in-rectangle',
        params: {
          timestamp: new Date(this.time).toISOString().replace("Z", ""),
          timePeriod: this.timePeriod
        },
        data: this.map.getBounds(),
        headers: {
          'Content-type': 'application/json',
          'Access-Control-Allow-Origin': '*'
        }
      }).then((response) => {
        console.log(response.data)
        this.handleMemberGeometries(response.data)
      });
    },
    handleMemberGeometries(memberGeometries) {
      this.markers.forEach(marker => this.map.removeLayer(marker))
      this.markers = useMarkers(memberGeometries);
      this.markers.forEach(marker => marker.addTo(this.map))
    },
    //websocket
    connect() {
      const stompC = new Stomp.client('ws://localhost:8084/update');
      stompC.connect(
          {},
          frame => {
            console.log(frame);
            stompC.subscribe("/broker/member", (member) => {
              // console.log(member);
              var geoJsonFeature = {
                "type": "Feature",
                "geometry": member.geojsonGeometry,
                "properties": {
                  "popupContent": member.memberId
                }
              }
              let marker = L.geoJson(geoJsonFeature, {style: {color: "red"}})
              // this.markers.push(marker)
              marker.addTo(this.map)
              console.log(marker)
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
