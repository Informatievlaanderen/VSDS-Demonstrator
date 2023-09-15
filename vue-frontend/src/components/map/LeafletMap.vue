<script setup>
</script>

<template>
  <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"
        integrity="sha256-p4NxAoJBhIIN+hmNHrzRCf9tD/miZyoHS5obTRR9BMY=" crossorigin=""/>

  <div style="float:left; height:500px; width:600px">
    <svg></svg>
  </div>
  <div style="float:right; height:500px; width:500px" id="map"></div>
</template>

<script>

import "leaflet/dist/leaflet.css"
import L from 'leaflet';
import * as d3 from "d3";
import axios from 'axios'
import {triplesToGraph} from "@/components/graph/functions/triplesToGraph";

export default {
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
    this.map = L.map("map", {zoomAnimation: false}).setView([50.7747, 4.4852], 8)
    this.map.on('moveend', function () {

    });
    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
      attribution: '© OpenStreetMap'
    }).addTo(this.map);
    this.map.on("moveend", () => {
      extracted.call(this);
    });
    extracted.call(this);

    function extracted() {
      axios({
        method: 'post',
        url: 'http://localhost:5173/in-rectangle?timestamp=2023-08-27T20:42:40.230',
        data: this.map.getBounds(),
        headers: {
          'Content-type': 'application/json',
          'Access-Control-Allow-Origin': '*'
        }
      }).then((response) => {
        this.handleMemberGeometries(response.data)
      });
    }
  },
  methods: {
    handleMemberGeometries(memberGeometries) {
      function visualizeTriples(triples) {
        let children = d3.select("svg").selectAll("*")
        children.remove();

        const width = 600;
        const height = 500;
        let svg = d3.select("svg").attr("width", width).attr("height", height);
        let graph = triplesToGraph(triples);
        let force = d3.forceSimulation(graph.nodes);

        function dragstart() {
          d3.select(this).classed("fixed", true);
        }

        function clamp(x, lo, hi) {
          return x < lo ? lo : x > hi ? hi : x;
        }

        function dragged(event, d) {
          d.fx = clamp(event.x, 0, width);
          d.fy = clamp(event.y, 0, height);
          force.alpha(1).restart();
        }

        const drag = d3.drag().on("start", dragstart).on("drag", dragged);

        svg
            .append("svg:defs")
            .selectAll("marker")
            .data(["end"])
            .enter()
            .append("svg:marker")
            .attr("id", String)
            .attr("viewBox", "0 -5 10 10")
            .attr("refX", 30)
            .attr("refY", -0.5)
            .attr("markerWidth", 6)
            .attr("markerHeight", 6)
            .attr("orient", "auto")
            .append("svg:polyline")
            .attr("points", "0,-5 10,0 0,5");

        var links = svg
            .selectAll(".link")
            .data(graph.links)
            .enter()
            .append("line")
            .attr("marker-end", "url(#end)")
            .attr("class", "link")
            .attr("stroke-width", 1); //links
        // ==================== Add Link Names =====================
        var linkTexts = svg
            .selectAll(".link-text")
            .data(graph.links)
            .enter()
            .append("text")
            .attr("class", "link-text")
            .text(function (d) {
              return d.predicate;
            });
        // ==================== Add Link Names =====================
        var nodeTexts = svg
            .selectAll(".node-text")
            .data(graph.nodes)
            .enter()
            .append("text")
            .attr("class", "node-text")
            .text(function (d) {
              return d.label;
            });
        // ==================== Add Node =====================
        var nodes = svg
            .selectAll(".node")
            .data(graph.nodes)
            .enter()
            .append("circle")
            .attr("class", "node")
            .attr("r", 8)
            // .on("click", async function (d) {
            //   let triples = await axios
            //       .get('http://localhost:8080/'+d.name)
            //   await this.visualizeTriples(triples.data);
            //   // alert("You clicked on node " + d.name);
            // }
            // .bind(this))
            .call(drag);

        function ticked() {
          nodes
              .attr("cx", function (d) {
                return 2 * d.x;
              })
              .attr("cy", function (d) {
                return 2 * d.y;
              });

          links
              .attr("x1", function (d) {
                return 2 * d.source.x;
              })
              .attr("y1", function (d) {
                return 2 * d.source.y;
              })
              .attr("x2", function (d) {
                return 2 * d.target.x;
              })
              .attr("y2", function (d) {
                return 2 * d.target.y;
              });

          nodeTexts
              .attr("x", function (d) {
                return 2 * d.x + 12;
              })
              .attr("y", function (d) {
                return 2 * d.y + 3;
              });

          linkTexts
              .attr("x", function (d) {
                return 4 + 2 * (d.source.x + d.target.x) / 2;
              })
              .attr("y", function (d) {
                return 4 + 2 * (d.source.y + d.target.y) / 2;
              });
        }

        force.on("tick", ticked);

        return force
            .force(
                "link",
                d3.forceLink(graph.links).id((d) => d.id)
            )
            .force("charge", d3.forceManyBody())
            .force("center", d3.forceCenter(width / 5, height / 4));
      }

      function whenClicked(e) {

        let memberId = e.sourceTarget._popup._content
        axios({
          method: 'get',
          url: 'http://localhost:5173/triples/' + memberId,
        }).then((response) => {
          visualizeTriples(response.data)
        });
      }

      function onEachFeature(feature, layer) {
        if (feature.properties && feature.properties.popupContent) {
          layer.bindPopup(feature.properties.popupContent);
        }
        //bind click
        layer.on({
          click: whenClicked
        });
      }

      memberGeometries.forEach(feature => {
        var geoJsonFeature = {
          "type": "Feature",
          "geometry": feature.geojsonGeometry,
          "properties": {
            "popupContent": feature.memberId
          }
        }
        let marker = L.geoJson(geoJsonFeature, {onEachFeature: onEachFeature}).addTo(this.map)
        this.markers.push(marker)
      })
    },
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
