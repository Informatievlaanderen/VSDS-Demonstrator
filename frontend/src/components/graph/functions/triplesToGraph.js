function filterNodesById(nodes, id) {
    return nodes.filter(function (n) {
        return n.id === id;
    });
}

export function triplesToGraph(triples) {

    //Graph
    var graph = {nodes: [], links: []};

    //Initial Graph from triples
    triples.forEach(function (triple) {
        var subject = triple.subject;
        var predicate = triple.predicate;
        var object = triple.object;

        var subjNode = filterNodesById(graph.nodes, subject.value)[0];
        var objNode = filterNodesById(graph.nodes, object.value)[0];

        if (subjNode == null) {
            subjNode = {id: subject.value, label: subject.prefixedValue, weight: 1};
            graph.nodes.push(subjNode);
        }

        if (objNode == null) {
            objNode = {id: object.value, label: object.prefixedValue, weight: 1};
            graph.nodes.push(objNode);
        }


        graph.links.push({
            source: subjNode,
            target: objNode,
            predicate: {label: predicate.prefixedValue, id: predicate.value},
            weight: 1
        });
    });

    return graph;
}
