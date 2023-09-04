function filterNodesById(nodes,id){
    return nodes.filter(function(n) { return n.id === id; });
}

export function triplesToGraph(triples){

    //Graph
    var graph={nodes:[], links:[]};

    //Initial Graph from triples
    triples.forEach(function(triple){
        var subjId = triple.subject;
        var predId = triple.predicate;
        var objId = triple.object;

        var subjNode = filterNodesById(graph.nodes, subjId)[0];
        var objNode  = filterNodesById(graph.nodes, objId)[0];

        if(subjNode==null){
            subjNode = {id:subjId, label:subjId, weight:1};
            graph.nodes.push(subjNode);
        }

        if(objNode==null){
            objNode = {id:objId, label:objId, weight:1};
            graph.nodes.push(objNode);
        }


        graph.links.push({source:subjNode, target:objNode, predicate:predId, weight:1});
    });

    return graph;
}
