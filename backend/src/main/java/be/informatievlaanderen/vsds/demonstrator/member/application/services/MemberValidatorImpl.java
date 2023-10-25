package be.informatievlaanderen.vsds.demonstrator.member.application.services;

import be.informatievlaanderen.vsds.demonstrator.member.application.config.EventStreamConfig;
import be.informatievlaanderen.vsds.demonstrator.member.application.config.StreamsConfig;
import be.informatievlaanderen.vsds.demonstrator.member.application.exceptions.MembertypeException;
import be.informatievlaanderen.vsds.demonstrator.member.application.exceptions.MissingCollectionException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.springframework.stereotype.Service;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;

@Service
public class MemberValidatorImpl implements MemberValidator {
    private static final Property RDF_SYNTAX_TYPE = createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");

    private final StreamsConfig streams;

    public MemberValidatorImpl(StreamsConfig streams) {
        this.streams = streams;
    }

    @Override
    public void validate(Model member, String collectionName) {
        EventStreamConfig streamConfig = streams.getStream(collectionName)
                .orElseThrow(() -> new MissingCollectionException(collectionName));
        if (!testMembertype(member, streamConfig.getMemberType())) {
            throw new MembertypeException("todo", streamConfig.getMemberType());
        }
    }

    private boolean testMembertype(Model member, String memberType) {
        return member.listStatements(null, RDF_SYNTAX_TYPE, createResource(memberType)).hasNext();
    }
}
