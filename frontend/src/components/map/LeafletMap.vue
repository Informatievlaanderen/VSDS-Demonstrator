<template>
  <div class="z-stack">
    <MapButtons :layers-to-show="layersToShow" @on-layers-updated="(name) => this.updateLayers(name)"></MapButtons>
    <div class="linked-data-container">
      <div style="width: 50%" id="map"></div>
      <div style="width: 50%">
        <KnowledgeGraph :member-id="memberId"></KnowledgeGraph>
      </div>
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
import clusterSmall from "../../assets/svgs/legend/maps.marker.cluster-small.svg"
import clusterMedium from "../../assets/svgs/legend/maps.marker.cluster-medium.svg"
import clusterLarge from "../../assets/svgs/legend/maps.marker.cluster-large.svg"
import "leaflet/dist/leaflet.css"
import L from 'leaflet';
import "leaflet.markercluster/dist/MarkerCluster.css"
import "leaflet.markercluster/dist/leaflet.markercluster"
import axios from 'axios'
import {useMarkers} from "@/components/map/composables/useMarkers";
import {ref} from "vue";
import Slider from "@/components/slider/Slider.vue";
import Stomp from "webstomp-client";
import KnowledgeGraph from "@/components/graph/KnowledgeGraph.vue";
import MapButtons from "@/components/modal/MapButtons.vue";

const iconCreateFunction = (cluster) => {
  const count = cluster.getChildCount();
  let clusterSize = "";
  let iconAnchor = [];
  let iconUrl;

  if (count < 21) {
    clusterSize = "small";
    iconAnchor = [23, 23]
    iconUrl = clusterSmall
  } else if (count < 61) {
    clusterSize = "medium";
    iconAnchor = [33, 33];
    iconUrl = clusterMedium;
  } else {
    clusterSize = "large";
    iconAnchor = [40.5, 40.5]
    iconUrl = clusterLarge;
  }

  const className = `marker-cluster-${clusterSize}`;

  return L.divIcon({
    html: `<div class="marker-cluster-flanders"><img src="${iconUrl}"><span>${count}</span></div>`,
    className,
    iconAnchor,
  })
}

export default {
  components: {MapButtons, KnowledgeGraph, Slider},
  watch: {
    time: function () {
      this.fetchMembers();
    },
  },
  setup() {
    const layerNames = ["gipod", "verkeersmeting"]

    const time = ref(new Date().getTime())
    const timePeriod = ref("PT10M")
    const layersToShow = ref(new Map(layerNames.map(name => [name, true])));
    const layers = new Map(layerNames.map(name => [name, L.markerClusterGroup({iconCreateFunction})]))

    return {
      time,
      timePeriod,
      layersToShow,
      layers
    }
  },
  name: "ConnectionState",
  data() {
    return {
      data: [],
      map: {},
      memberId: null,
      simulation: null,
      stompClient: null
    };
  },

  mounted() {
    this.connect()
    console.log(import.meta.env.VITE_WS_BASE_URL);
    this.map = L.map("map", {zoomAnimation: false, zoomControl: false}).setView([50.7747, 4.4852], 8)
    L.control.zoom({position: "topright"}).addTo(this.map)
    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
      attribution: '© OpenStreetMap'
    }).addTo(this.map);
    //TODO: delete this and hard code the bounds of Flanders/Belgium for performance reasons
    this.map.on("popupclose", () => this.memberId = null)
    // this.map.on("moveend", () => {
    //   this.fetchMembers();
    // });
    this.fetchMembers();
    for (let [key, value] of this.layersToShow.entries()) {
      if (value) {
        this.layers.get(key).addTo(this.map)
      }
    }
  },
  methods: {
    updateLayers(key) {
      if (this.layersToShow.get(key)) {
        this.map.addLayer(this.layers.get(key))
      } else {
        this.map.removeLayer(this.layers.get(key))
      }
    },
    onPopupClosed() {
      this.memberId = null;
    },
    fetchMembers() {
      for (let [key, layer] of this.layers.entries()) {
        axios({
          method: 'post',
          url: `/api/${key}/in-rectangle`,
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
          this.handleMemberGeometries(layer, response.data)
        });

      }
    },
    handleMemberGeometries(layer, memberGeometries) {
      layer.clearLayers();
      let markers = useMarkers(memberGeometries, (memberId) => this.memberId = memberId, this.onPopupClosed)
      layer.addLayers(markers)
    },
    //websocket
    connect() {
      const decolouringTimeout = 1000;
      this.stompClient = new Stomp.client(`${import.meta.env.VITE_WS_BASE_URL}/update`, {debug: false});
      this.stompClient.connect(
          {},
          frame => this.subscribe(),
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
    subscribe() {
      for (let key of this.layersToShow.keys()) {
        this.stompClient.subscribe("/broker/member/" + key, (member) => {
          let body = JSON.parse(member.body)
          let marker = useMarkers([body], (memberId) => this.memberId = memberId, this.onPopupClosed).at(0)
          marker.setStyle({
            color: '#FFA405',
          })
          if (key === "gipod") {
            setTimeout(function () {
              this.updateMarker(marker)
            }.bind(this), 1000)
          }
          this.layers.get(key).addLayer(marker)
        });
      }
    },
    updateMarker(marker) {
      marker.setStyle({color: '#808080'})
    }
  }

};

</script>

<style>
.linked-data-container {
  position: relative;
  z-index: 2;
  display: flex;
  width: 100%;
}

.z-stack {
  position: relative;
  z-index: 1;
  width: 100%;
  height: 450px;
}

.leaflet-control-zoom-in,
.leaflet-control-zoom-out {
  color: #05C !important;
  border-width: 0 !important;
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

.leaflet-interactive {
  margin: 12px;
}

.marker-cluster-flanders {
  position: relative;
  color: #fff;
  justify-content: center;
  display: inline-flex;
}

.marker-cluster-flanders > span {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}
</style>