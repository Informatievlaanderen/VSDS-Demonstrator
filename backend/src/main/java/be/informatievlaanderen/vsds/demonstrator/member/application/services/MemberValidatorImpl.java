package be.informatievlaanderen.vsds.demonstrator.member.application.services;

import be.informatievlaanderen.vsds.demonstrator.member.application.config.EventStreamConfig;
import be.informatievlaanderen.vsds.demonstrator.member.application.config.StreamsConfig;
import be.informatievlaanderen.vsds.demonstrator.member.application.exceptions.MembertypeException;
import be.informatievlaanderen.vsds.demonstrator.member.application.exceptions.MissingCollectionException;
import org.apache.jena.rdf.model.Model;
import org.springframework.stereotype.Service;

@Service
public class MemberValidatorImpl implements MemberValidator {

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
        //TODO
        return true;
    }
}
