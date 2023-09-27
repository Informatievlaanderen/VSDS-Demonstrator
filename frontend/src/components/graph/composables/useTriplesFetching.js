import axios from "axios";
import * as d3 from "d3";
import {triplesToGraph} from "@/components/graph/functions/triplesToGraph";

function visualizeTriples(triples) {
    let children = d3.select("svg").selectAll("*")
    children.remove();

    const width = 600;
    const height = 500;
    let svg = d3.select("#knowledge-graph");
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

    let links = svg
        .selectAll(".link")
        .data(graph.links)
        .enter()
        .append("line")
        .attr("marker-end", "url(#end)")
        .attr("class", "link")
        .attr("stroke-width", 1); //links
    // ==================== Add Link Names =====================
    let linkTexts = svg
        .selectAll(".link-text")
        .data(graph.links)
        .enter()
        .append("text")
        .attr("class", "link-text")
        .text(function (d) {
            return d.predicate;
        });
    // ==================== Add Link Names =====================
    let nodeTexts = svg
        .selectAll(".node-text")
        .data(graph.nodes)
        .enter()
        .append("text")
        .attr("class", "node-text")
        .text(function (d) {
            return d.label;
        });
    // ==================== Add Node =====================
    let nodes = svg
        .selectAll(".node")
        .data(graph.nodes)
        .enter()
        .append("circle")
        .attr("class", "node")
        .attr("r", 8)
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

export function useTriplesFetching(memberId) {
    axios({
        method: 'get',
        url: '/triples/' + memberId,
    }).then((response) => {
        console.log(response.data)
        visualizeTriples(response.data)
    });
}