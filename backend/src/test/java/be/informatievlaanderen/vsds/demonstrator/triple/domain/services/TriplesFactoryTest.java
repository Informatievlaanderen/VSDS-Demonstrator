package be.informatievlaanderen.vsds.demonstrator.triple.domain.services;

import be.informatievlaanderen.vsds.demonstrator.triple.domain.valueobjects.Node;
import be.informatievlaanderen.vsds.demonstrator.triple.domain.valueobjects.Triple;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class TriplesFactoryTest {
    @Test
    void given_RdfModel_when_GetTriplesFromFactory_then_ReturnList() throws IOException {
        Model model = readModelFromFile();

        List<Triple> actualTriples = TriplesFactory.fromModel(model).getTriples();
        List<String> predicates = model.predicates().stream()
                .map(IRI::stringValue)
                .toList();

        assertThat(actualTriples)
                .hasSameSizeAs(model)
                .map(triple -> triple.getPredicate().getValue())
                .containsAll(predicates);
    }

    @Test
    void given_RdfModel_when_GetTriplesFromFactoryWithNamespaces_then_ReturnList() throws IOException {
        Model model = readModelFromFile();
        Map<String, String> namespaceMap = initNamespaceMap();

        List<Triple> triples = TriplesFactory.fromModel(model).withNamespaces(namespaceMap).getTriples();

        assertThat(triples)
                .hasSameSizeAs(model)
                .map(Triple::getPredicate)
                .filteredOn(predicate -> !predicate.getValue().equals(predicate.getPrefixedValue()))
                .map(Node::getPrefixedValue)
                .map(value -> value.split(":")[0])
                .containsAll(namespaceMap.keySet());
    }

    private Model readModelFromFile() throws IOException {
        File file = ResourceUtils.getFile("classpath:members/mobility-hindrance.nq");
        return Rio.parse(Files.newInputStream(file.toPath()), RDFFormat.NQUADS);
    }

    private Map<String, String> initNamespaceMap() {
        return Map.of(
                "rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#",
                "dct", "http://purl.org/dc/terms/",
                "adms", "http://www.w3.org/ns/adms#",
                "prov", "http://www.w3.org/ns/prov#",
                "purl", "http://purl.org/dc/elements/1.1/"
        );
    }
}