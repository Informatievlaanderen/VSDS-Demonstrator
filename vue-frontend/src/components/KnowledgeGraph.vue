<template>
  <div>
    <svg></svg>
  </div>
</template>
<script>
import * as d3 from "d3";
import axios from 'axios'
import {triplesToGraph} from "@/components/functions/triplesToGraph";

export default {
  methods:
      {
        async visualizeTriples(triples) {
          const width = 800;
          const height = 500;
          const svg = d3.select("svg").attr("width", width).attr("height", height);

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
              .on("click", async function (d) {
                let triples = await axios
                    .get('http://localhost:8080/'+d.name)
                await this.visualizeTriples(triples.data);
                // alert("You clicked on node " + d.name);
              }.bind(this))
              .call(drag);

          function ticked() {
            nodes
                .attr("cx", function (d) {
                  return d.x;
                })
                .attr("cy", function (d) {
                  return d.y;
                });

            links
                .attr("x1", function (d) {
                  return d.source.x;
                })
                .attr("y1", function (d) {
                  return d.source.y;
                })
                .attr("x2", function (d) {
                  return d.target.x;
                })
                .attr("y2", function (d) {
                  return d.target.y;
                });

            nodeTexts
                .attr("x", function (d) {
                  return d.x + 12;
                })
                .attr("y", function (d) {
                  return d.y + 3;
                });

            linkTexts
                .attr("x", function (d) {
                  return 4 + (d.source.x + d.target.x) / 2;
                })
                .attr("y", function (d) {
                  return 4 + (d.source.y + d.target.y) / 2;
                });
          }

          force.on("tick", ticked);

          force
              .force(
                  "link",
                  d3.forceLink(graph.links).id((d) => d.id)
              )
              .force("charge", d3.forceManyBody())
              .force("center", d3.forceCenter(width / 2, height / 2));
        }
      }, async mounted() {

    // let triples = [
    //   {
    //     subject: "ex:ThaiLand",
    //     predicate: "ex:hasFood",
    //     object: "ex:TomYumKung",
    //   },
    //   {
    //     subject: "ex:TomYumKung",
    //     predicate: "rdf:type",
    //     object: "ex:SpicyFood",
    //   },
    //   {
    //     subject: "ex:TomYumKung",
    //     predicate: "ex:includes",
    //     object: "ex:shrimp",
    //   },
    //   {
    //     subject: "ex:TomYumKung",
    //     predicate: "ex:includes",
    //     object: "ex:chilly",
    //   },
    //   {
    //     subject: "ex:TomYumKung",
    //     predicate: "ex:includes",
    //     object: "ex:lemon",
    //   },
    //   {subject: "ex:lemon", predicate: "ex:hasTaste", object: "ex:sour"},
    //   {subject: "ex:chilly", predicate: "ex:hasTaste", object: "ex:spicy"},
    // ];

    let triples = await axios
        .get('http://localhost:8080/start')
    await this.visualizeTriples(triples.data);
  },
};
</script>
<style type="text/css">
.node {
  stroke: #fff;
  fill: #ddd;
  stroke-width: 1.5px;
}

.link {
  stroke: #999;
  stroke-opacity: 0.6;
  stroke-width: 1px;
}

marker {
  stroke: #999;
  fill: rgba(124, 240, 10, 0);
}

.node-text {
  font: 11px sans-serif;
  fill: black;
}

.link-text {
  font: 9px sans-serif;
  fill: grey;
}

svg {
  border: 1px solid black;
}
</style>