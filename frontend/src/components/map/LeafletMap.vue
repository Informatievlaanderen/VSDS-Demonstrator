<template>
  <div class="z-stack">
    <MapButtons :layers-to-show="layersToShow" @on-layers-updated="(name) => this.updateLayers(name)"></MapButtons>
    <div class="linked-data-container">
      <div style="width: 50%" id="map"></div>
      <div style="width: 50%">
        <KnowledgeGraph :member="member"></KnowledgeGraph>
      </div>
    </div>
  </div>
  <div>
    <Slider @timestamp-changed="(timestamp) => time = timestamp"
            @realtime-toggled="isRealTimeEnabled => isRealTimeEnabled ? subscribe() : unsubscribe()"
    />
  </div>
</template>

<script>
import clusterSmall from "../../assets/svgs/legend/maps.marker.cluster-small.svg"
import clusterMedium from "../../assets/svgs/legend/maps.marker.cluster-medium.svg"
import clusterLarge from "../../assets/svgs/legend/maps.marker.cluster-large.svg"
import clusterSmallAlt from "../../assets/svgs/legend/maps.marker.cluster-small-alt.svg"
import clusterMediumAlt from "../../assets/svgs/legend/maps.marker.cluster-medium-alt.svg"
import clusterLargeAlt from "../../assets/svgs/legend/maps.marker.cluster-large-alt.svg"
import "leaflet/dist/leaflet.css"
import L from 'leaflet';
import "leaflet.markercluster/dist/MarkerCluster.css"
import "leaflet.markercluster/dist/leaflet.markercluster"
import axios from 'axios'
import {useMarkers} from "@/components/map/composables/useMarkers";
import {ref} from "vue";
import Slider from "@/components/slider/Slider.vue";
import KnowledgeGraph from "@/components/graph/KnowledgeGraph.vue";
import MapButtons from "@/components/modal/MapButtons.vue";
import {streams} from "../../../streams.json"

const iconCreateFunction = (cluster, name) => {
  const count = cluster.getChildCount();
  let clusterSize;
  let iconAnchor;
  let iconUrl;

  if (count < 21) {
    clusterSize = "small";
    iconAnchor = [23, 23];
    iconUrl = name === "bluebikes" ? clusterSmall : clusterSmallAlt
  } else if (count < 61) {
    clusterSize = "medium";
    iconAnchor = [33, 33];
    iconUrl = name === "bluebikes" ? clusterMedium : clusterMediumAlt
  } else {
    clusterSize = "large";
    iconAnchor = [40.5, 40.5];
    iconUrl = name === "bluebikes" ? clusterLarge : clusterLargeAlt
  }

  const className = `marker-cluster-${clusterSize}`;
  const color = name === "bluebikes" ? "#ffffff" : "#333332";
  return L.divIcon({
    html: `<div class="marker-cluster-flanders"><img src="${iconUrl}"><span class="body body-xxsmall-regular" style="color: ${color}">${count}</span></div>`,
    className,
    iconAnchor,
  })
}

export default {
  components: {MapButtons, KnowledgeGraph, Slider},
  watch: {
    time: function () {
      this.map.closePopup();
      this.fetchMembers();
    },
    'stompClient.connected': {
      handler(newConnectedValue) {
        if(newConnectedValue) {
          this.subscribe();
        }
      },
      immediate: true
    }
  },
  props: ["stompClient"],
  setup() {
    const time = ref(new Date().getTime())
    const member = ref(null);

    const layerNames = Array.from(streams, (stream) => stream.id)
    const layersToShow = ref(new Map(layerNames.map(name => [name, true])));
    const layers = new Map(layerNames.map(name => [name, L.markerClusterGroup({
      iconCreateFunction: (cluster) => iconCreateFunction(cluster, name)
    })]))

    const map = {}
    const subscription = null;

    return {
      time,
      member,
      map,
      layersToShow,
      layers,
      subscription
    }
  },
  name: "ConnectionState",
  mounted() {
    this.map = L.map("map", {zoomAnimation: false, zoomControl: false}).setView([50.9, 4.15], 8)
    L.control.zoom({position: "topright"}).addTo(this.map)
    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
      attribution: '© OpenStreetMap'
    }).addTo(this.map);
    L.tileLayer('https://geo.api.vlaanderen.be/GRB/wmts?SERVICE=WMTS&VERSION=1.0.0&REQUEST=GetTile&LAYER=grb_bsk&STYLE=&FORMAT=image/png&TILEMATRIXSET=GoogleMapsVL&TILEMATRIX={z}&TILEROW={y}&TILECOL={x}', {
      layers: 'GRB-basiskaart',
      maxZoom: 19,
      minZoom: 12,
      format: 'image/png',
      transparent: true,
      version: '1.1.0',
      attribution: '© GRB'
    }).addTo(this.map);
    this.map.on("popupclose", () => this.member = null)
    this.map.on("zoomstart", () => this.map.closePopup())
    for (let [key, value] of this.layersToShow.entries()) {
      if (value) {
        this.layers.get(key).addTo(this.map)
      }
    }
  },
  unmounted() {
    this.subscription?.unsubscribe();
  },
  methods: {
    updateLayers(key) {
      if (this.layersToShow.get(key)) {
        this.map.addLayer(this.layers.get(key))
      } else {
        this.map.removeLayer(this.layers.get(key))
      }
    },
    fetchMembers() {
      for (let [collection, layer] of this.layers.entries()) {
        axios({
          method: 'post',
          url: `/api/${collection}/in-rectangle`,
          params: {
            timestamp: new Date(this.time).toISOString().replace("Z", "")
          },
          data: {
            _northEast: {lat: 51.61113728, lng: 6.60827637},
            _southWest: {lat: 49.37098431, lng: 2.38952637}
          },
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
      let markers = useMarkers(memberGeometries, (member) => this.member = member)
      layer.addLayers(markers)
    },
    subscribe() {
      this.fetchMembers();
      this.subscription = this.stompClient.subscribe("/broker/member/", (message) => {
        const member = JSON.parse(message.body)
        const markerToRemove = this.layers.get(member.collection).getLayers()
            .filter(m => m.feature.properties.isVersionOf === member.isVersionOf)[0];
        const marker = useMarkers([member], (member) => this.member = member).at(0)
        marker?.setStyle({
          color: '#FFA405',
        })
        if (member.collection === "gipod" && marker) {
          setTimeout(function () {
            this.updateMarker(marker)
          }.bind(this), 1000)
        }
        if (markerToRemove) {
          this.layers.get(member.collection).removeLayer(markerToRemove);
        }
        if (marker) {
          this.layers.get(member.collection).addLayer(marker)
        }
      });
    },
    unsubscribe() {
      this.subscription?.unsubscribe();
    },
    updateMarker(marker) {
      marker.setStyle({color: '#A813F7'})
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

.leaflet-popup-content-wrapper {
  border-radius: 3px !important;
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

.popup-verkeersmeting {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
}

.popup-grid {
  display: grid;
  row-gap: 6px;
  column-gap: 12px;
  grid-template-columns: auto auto;
}

.popup-gipod-icon {
  width: 11px;
  height: 16px;
}

.popup-bluebike-icon {
  width: 24px;
  height: 24px;
}

.popup-grid-icon {
  grid-column-start: span 2;
  justify-self: center;
}

.popup-title {
  grid-column-start: span 2;
  justify-self: center;
}

.popup-grid-end {
  justify-self: end;
}

.popup-verkeersmeting-map-marker {
  width: 16px;
  height: 16px;
}

.popup-verkeersmeting-counting-row {
  display: inline-flex;
  align-items: end;
  gap: 6px;
  margin-top: -6px;
}

.popup-verkeersmeting-car-icon {
  width: 24px;
  height: 24px;
}

.popup-verkeersmeting-counting-result {
  font-size: 14px;
  line-height: 17px;
}
</style>