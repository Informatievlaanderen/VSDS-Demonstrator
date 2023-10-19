import axios from "axios";
import * as d3 from "d3";
import {triplesToGraph} from "@/components/graph/functions/triplesToGraph";

const NODE_RADIUS = 8;
const NODE_STROKE_WIDTH = 1.5;
const NODE_TEXT_FONT_SIZE = 11;
const LINK_TEXT_FONT_SIZE = 9;

function visualizeTriples(triples) {
    const div = d3.select("#knowledge-graph")
    div.select("svg").remove();
    const svg = div.append("svg")
    svg.selectAll("*").remove();
    const g = svg.append("g");

    const tooltip = div.append("div")
        .attr("id", "tooltip")
        .attr("class", "shadow-medium body body-xxsmall-regular")
        .style("display", "none")

    const width = +svg.style("width").replace("px", "")
    const height = +svg.style("height").replace("px", "");
    const graph = triplesToGraph(triples);
    d3.select("#knowledge-graph-loading").remove();
    d3.select("#knowledge-graph-zoom-buttons").style("opacity", "1")
    const force = d3.forceSimulation(graph.nodes);

    function dragstart() {
        d3.select(this).classed("fixed", true);
    }

    function clamp(x, lo, hi) {
        return x < lo ? lo : x > hi ? hi : x;
    }

    function onMouseOver(event, text) {
        tooltip.style("display", "block").text(text)
    }

    function onMouseOut() {
        tooltip.style("display", "none");
    }

    function onMouseMove(event, marginBottom) {
        let width = +tooltip.style("width").replace("px", "");
        tooltip.style("left", `${event.layerX - width / 2}px`).style("top", `${event.layerY - marginBottom}px`)
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
        .on("mouseover", (event, d) => onMouseOver(event, d.predicate.id))
        .on("mouseout", onMouseOut)
        .on("mousemove", event => onMouseMove(event, 42))
        .text(d => d.predicate.label);
    // ==================== Add Link Names =====================
    const nodeTexts = g
        .selectAll(".node-text")
        .data(graph.nodes)
        .enter()
        .append("text")
        .attr("class", "node-text")
        .style("font-size", `${NODE_TEXT_FONT_SIZE}px`)
        .on("mouseover", (event, d) => onMouseOver(event, d.id))
        .on("mouseout", onMouseOut)
        .on("mousemove", event => onMouseMove(event, 42))
        .text(d => d.label);
    // ==================== Add Node =====================
    const nodes = g
        .selectAll(".node")
        .data(graph.nodes)
        .enter()
        .append("circle")
        .attr("class", "node")
        .attr("r", NODE_RADIUS)
        .style("stroke-width", `${NODE_STROKE_WIDTH}px`)
        .call(drag);

    let transform;

    const zoom = d3.zoom().on("zoom", e => {
        console.log(e.transform)
        g.attr("transform", () => transform = e.transform);
        nodes.attr("r", NODE_RADIUS / Math.sqrt(transform.k))
        nodes.style("stroke-width", `${NODE_STROKE_WIDTH / Math.sqrt(transform.k)}px`)
        nodeTexts.style("font-size", `${NODE_TEXT_FONT_SIZE / Math.sqrt(transform.k)}px`)
        linkTexts.style("font-size", `${LINK_TEXT_FONT_SIZE / Math.sqrt(transform.k)}px`)
    })

    const zoomInBtn = d3.select("#knowledge-graph-zoom-in-btn")
        .on("click", () => zoom.scaleBy(svg.transition().duration(350), 1.5))
    const zoomOutBtn = d3.select("#knowledge-graph-zoom-out-btn")
        .on("click", () => zoom.scaleBy(svg.transition().duration(350), 0.75))

    function ticked() {
        const DISTANCE_FACTOR = 2.5;
        nodes
            .attr("cx", function (d) {
                return DISTANCE_FACTOR * d.x;
            })
            .attr("cy", function (d) {
                return DISTANCE_FACTOR * d.y;
            });

        links
            .attr("x1", function (d) {
                return DISTANCE_FACTOR * d.source.x;
            })
            .attr("y1", function (d) {
                return DISTANCE_FACTOR * d.source.y;
            })
            .attr("x2", function (d) {
                return DISTANCE_FACTOR * d.target.x;
            })
            .attr("y2", function (d) {
                return DISTANCE_FACTOR * d.target.y;
            });

        nodeTexts
            .attr("x", function (d) {
                return DISTANCE_FACTOR * d.x + 12;
            })
            .attr("y", function (d) {
                return DISTANCE_FACTOR * d.y + 3;
            });

        linkTexts
            .attr("x", function (d) {
                return 4 + DISTANCE_FACTOR * (d.source.x + d.target.x) / 2;
            })
            .attr("y", function (d) {
                return 4 + DISTANCE_FACTOR * (d.source.y + d.target.y) / 2;
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