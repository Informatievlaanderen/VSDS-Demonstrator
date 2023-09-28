<template>
  <div style="display: flex; width: 100%">
    <div style="width: 50%" id="map"></div>
    <div style="width: 50%">
      <KnowledgeGraph :member-id="memberId"></KnowledgeGraph>
    </div>
  </div>
  <div>
    <Slider @timestamp-changed="(timestamp, period) => {
      time = timestamp;
      timePeriod = period;
    }"
            @realtime-toggled="(isRealTimeEnabled) => isRealTimeEnabled ? connect() : disconnect()"
    />
  </div>
</template>

<script>
import "leaflet/dist/leaflet.css"
import L from 'leaflet';
import axios from 'axios'
import {useMarkers} from "@/components/map/composables/useMarkers";
import {ref} from "vue";
import Slider from "@/components/slider/Slider.vue";
import Stomp from "webstomp-client";
import KnowledgeGraph from "@/components/graph/KnowledgeGraph.vue";

export default {
  components: {KnowledgeGraph, Slider},
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
      simulation: null,
      stompClient: null
    };
  },

  mounted() {
    console.log("mounted")
    this.connect()

    this.map = L.map("map", {zoomAnimation: false, zoomControl: false}).setView([50.7747, 4.4852], 8)
    L.control.zoom({position: "topright"}).addTo(this.map)
    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
      attribution: '© OpenStreetMap'
    }).addTo(this.map);
    // this.map.on("popupclose", () => this.memberId = null)
    this.map.on("moveend", () => {
      this.fetchMembers();
    });
    this.fetchMembers();

  },
  methods: {
    fetchMembers() {
      axios({
        method: 'post',
        url: '/api/in-rectangle',
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
        this.handleMemberGeometries(response.data)
      });
    },
    handleMemberGeometries(memberGeometries) {
      this.markers.forEach(marker => this.map.removeLayer(marker))
      this.markers = useMarkers(memberGeometries, (memberId) => this.memberId = memberId);
      this.markers.forEach(marker => marker.addTo(this.map))
    },
    //websocket
    connect() {
      this.stompClient = new Stomp.client('ws://localhost:8084/update');
      this.stompClient.connect(
          {},
          frame => {
            this.stompClient.subscribe("/broker/member", (member) => {
              let body = JSON.parse(member.body)
              let marker = useMarkers([body]).at(0)
              marker.setStyle({color: 'red'})
              setTimeout(function () {
                this.updateMarker(marker)
              }.bind(this), 1000)
              this.markers.push(marker)
              marker.addTo(this.map)
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
    },
    updateMarker(marker) {
      marker.setStyle({color: 'blue'})
    }
  }

};

</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.leaflet-control-zoom-in,
.leaflet-control-zoom-out {
  color: #05C !important;
  border: 0 !important;
  width: 35px !important;
  height: 35px !important;
  line-height: 35px !important;
  background: #FFF !important;
}

.leaflet-control-zoom-in {
  border-radius: 3px 3px 0 0 !important;
}

.leaflet-control-zoom-out {
  border-radius: 0 0 3px 3px !important;
}

.leaflet-control-zoom {
  border: none !important;
  border-radius: 3px !important;
  box-shadow: 0 2px 12px 0 rgb(106, 118, 134, 0.35) !important;
}

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