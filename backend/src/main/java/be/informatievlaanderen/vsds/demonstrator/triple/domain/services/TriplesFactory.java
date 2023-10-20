package be.informatievlaanderen.vsds.demonstrator.triple.domain.services;

import be.informatievlaanderen.vsds.demonstrator.triple.domain.valueobjects.Node;
import be.informatievlaanderen.vsds.demonstrator.triple.domain.valueobjects.Triple;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class TriplesFactory {
    private static final Map<String, String> COMMON_NAMESPACES = Map.ofEntries(
            Map.entry("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#"),
            Map.entry("rdfs", "http://www.w3.org/2000/01/rdf-schema#"),
            Map.entry("owl", "http://www.w3.org/2002/07/owl#"),
            Map.entry("xsd", "http://www.w3.org/2001/XMLSchema#"),
            Map.entry("dct", "http://purl.org/dc/terms/"),
            Map.entry("adms", "http://www.w3.org/ns/adms#"),
            Map.entry("prov", "http://www.w3.org/ns/prov#"),
            Map.entry("purl", "http://purl.org/dc/elements/1.1/"),
            Map.entry("foaf", "http://xmlns.com/foaf/0.1/"),
            Map.entry("org", "http://www.w3.org/ns/org#"),
            Map.entry("legal", "http://www.w3.org/ns/legal#"),
            Map.entry("m8g", "http://data.europa.eu/m8g/"),
            Map.entry("locn", "http://www.w3.org/ns/locn#"),
            Map.entry("ldes", "http://w3id.org/ldes#"),
            Map.entry("tree", "https://w3id.org/tree#"),
            Map.entry("sh", "http://www.w3.org/ns/shacl#"),
            Map.entry("skos", "http://www.w3.org/2004/02/skos/core#"),
            Map.entry("schema", "http://schema.org/")
    );
    private static final String VALID_PREFIX_REGEX = "^[a-zA-Z_][\\w.-]*$"; // NCName regex
    private final Stream<Statement> statementStream;
    private final Map<String, String> namespaces;

    private TriplesFactory(@NotNull Model model) {
        this.statementStream = model.stream();
        namespaces = new HashMap<>(COMMON_NAMESPACES);

    }

    /**
     * Assigns the namespaces to this factory
     *
     * @param namespaces Map with namespace prefix as key and the according name as value
     * @return the factory whereon this method is called
     */
    public TriplesFactory withNamespaces(@NotNull Map<String, String> namespaces) {
        this.namespaces.putAll(namespaces);

        return this;
    }

    /**
     * Initialize an instance of TriplesFactory with the specified Model
     *
     * @param model Model with which the initialization needs to be done
     * @return new instance of TriplesFactory
     */
    public static TriplesFactory fromModel(@NotNull Model model) {
        return new TriplesFactory(model);
    }

    /**
     * Converts all the statements of the model in this factory to a list of triples
     *
     * @return the list of triples
     */
    public List<Triple> getTriples() {
        return statementStream
                .map(statement -> new Triple(valueToNode(statement.getSubject()), valueToNode(statement.getPredicate()), valueToNode(statement.getObject())))
                .toList();
    }

    private Node valueToNode(@NotNull Value value) {
        if (value.isIRI()) {
            String name = ((IRI) value).getNamespace();
            return getNamespace(name).or(() -> createNamespace(name))
                    .map(namespace -> new Node(value.stringValue(), namespace))
                    .orElseGet(() -> new Node(value.stringValue()));
        }
        return new Node(value.stringValue());
    }

    private Optional<Map.Entry<String, String>> getNamespace(@NotNull String name) {
        return namespaces.entrySet().stream().filter(entry -> entry.getValue().equals(name)).findFirst();
    }

    private Optional<Map.Entry<String, String>> createNamespace(@NotNull String name) {
        Optional<String> prefixOptional = name.contains("#")
                ? createNamespacePrefix(name)
                : createNonNamespacePrefix(name);

        return prefixOptional
                .filter(prefix -> prefix.matches(VALID_PREFIX_REGEX))
                .map(prefix -> Map.entry(prefix, name));
    }

    private Optional<String> createNamespacePrefix(@NotNull String name) {
        int lastIndexOfHashtag = name.lastIndexOf('#');
        int lastIndexOfSlash = name.lastIndexOf('/');
        return Optional.of(name.substring(lastIndexOfSlash + 1, lastIndexOfHashtag));

    }

    private Optional<String> createNonNamespacePrefix(@NotNull String name) {
        String[] nameSplit = name.split("/");
        int index = nameSplit.length - 1;
        return Optional.ofNullable(nameSplit[index]);
    }
}
