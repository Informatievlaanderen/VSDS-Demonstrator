export function update(svg){
    // ==================== Add Marker ====================
    svg.append("svg:defs").selectAll("marker")
        .data(["end"])
        .enter().append("svg:marker")
        .attr("id", String)
        .attr("viewBox", "0 -5 10 10")
        .attr("refX", 30)
        .attr("refY", -0.5)
        .attr("markerWidth", 6)
        .attr("markerHeight", 6)
        .attr("orient", "auto")
        .append("svg:polyline")
        .attr("points", "0,-5 10,0 0,5")
    ;

    // ==================== Add Links ====================
    var links = svg.selectAll(".link")
        .data(graph.links)
        .enter()
        .append("line")
        .attr("marker-end", "url(#end)")
        .attr("class", "link")
        .attr("stroke-width",1)
    ;//links

    // ==================== Add Link Names =====================
    var linkTexts = svg.selectAll(".link-text")
        .data(graph.links)
        .enter()
        .append("text")
        .attr("class", "link-text")
        .text( function (d) { return d.predicate; })
    ;

    //linkTexts.append("title")
    //		.text(function(d) { return d.predicate; });

    // ==================== Add Link Names =====================
    var nodeTexts = svg.selectAll(".node-text")
        .data(graph.nodes)
        .enter()
        .append("text")
        .attr("class", "node-text")
        .text( function (d) { return d.label; })
    ;

    //nodeTexts.append("title")
    //		.text(function(d) { return d.label; });

    // ==================== Add Node =====================
    var nodes = svg.selectAll(".node")
        .data(graph.nodes)
        .enter()
        .append("circle")
        .attr("class", "node")
        .attr("r",8)
        .call(force.drag)
    ;//nodes

    // ==================== Force ====================
    force.on("tick", function() {
        nodes
            .attr("cx", function(d){ return d.x; })
            .attr("cy", function(d){ return d.y; })
        ;

        links
            .attr("x1", 	function(d)	{ return d.source.x; })
            .attr("y1", 	function(d) { return d.source.y; })
            .attr("x2", 	function(d) { return d.target.x; })
            .attr("y2", 	function(d) { return d.target.y; })
        ;

        nodeTexts
            .attr("x", function(d) { return d.x + 12 ; })
            .attr("y", function(d) { return d.y + 3; })
        ;


        linkTexts
            .attr("x", function(d) { return 4 + (d.source.x + d.target.x)/2  ; })
            .attr("y", function(d) { return 4 + (d.source.y + d.target.y)/2 ; })
        ;
    });

    // ==================== Run ====================
    force
        .nodes(graph.nodes)
        .links(graph.links)
        .charge(-500)
        .linkDistance(100)
        .start()
    ;
}