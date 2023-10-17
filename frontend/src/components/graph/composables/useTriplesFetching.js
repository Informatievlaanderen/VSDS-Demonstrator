import axios from "axios";
import * as d3 from "d3";
import {triplesToGraph} from "@/components/graph/functions/triplesToGraph";

const NODE_RADIUS = 8;
const NODE_TEXT_FONT_SIZE = 11;
const LINK_TEXT_FONT_SIZE = 9;

function visualizeTriples(triples) {
    const svg = d3.select("#knowledge-graph");
    svg.selectAll("*").remove();
    const g = svg.append("g");

    const width = +svg.style("width").replace("px", "")
    const height = +svg.style("height").replace("px", "");
    const graph = triplesToGraph(triples);
    const force = d3.forceSimulation(graph.nodes);

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

    g
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

    const links = g
        .selectAll(".link")
        .data(graph.links)
        .enter()
        .append("line")
        .attr("marker-end", "url(#end)")
        .attr("class", "link")
        .attr("stroke-width", 1); //links
    // ==================== Add Link Names =====================
    const linkTexts = g
        .selectAll(".link-text")
        .data(graph.links)
        .enter()
        .append("text")
        .attr("class", "link-text")
        .style("font-size", `${LINK_TEXT_FONT_SIZE}px`)
        .text(function (d) {
            return d.predicate;
        });
    // ==================== Add Link Names =====================
    const nodeTexts = g
        .selectAll(".node-text")
        .data(graph.nodes)
        .enter()
        .append("text")
        .attr("class", "node-text")
        .style("font-size", `${NODE_TEXT_FONT_SIZE}px`)
        .text(function (d) {
            return d.label;
        });
    // ==================== Add Node =====================
    const nodes = g
        .selectAll(".node")
        .data(graph.nodes)
        .enter()
        .append("circle")
        .attr("class", "node")
        .attr("r", NODE_RADIUS)
        .call(drag);

    let transform;

    const zoom = d3.zoom().on("zoom", e => {
        g.attr("transform", () => transform = e.transform);
        nodes.attr("r", NODE_RADIUS / Math.sqrt(transform.k))
        nodeTexts.style("font-size", `${11 / Math.sqrt(transform.k)}px`)
        linkTexts.style("font-size", `${9 / Math.sqrt(transform.k)}px`)
    })

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
    svg
        .call(zoom)
        .call(zoom.transform, d3.zoomIdentity)

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
        url: 'api/triples/' + memberId,
    }).then((response) => {
        visualizeTriples(response.data)
    });
}