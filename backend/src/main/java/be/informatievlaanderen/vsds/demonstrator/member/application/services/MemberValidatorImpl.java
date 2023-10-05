package be.informatievlaanderen.vsds.demonstrator.member.application.services;

import be.informatievlaanderen.vsds.demonstrator.member.application.config.EventStreamConfig;
import be.informatievlaanderen.vsds.demonstrator.member.application.config.StreamsConfig;
import be.informatievlaanderen.vsds.demonstrator.member.application.exceptions.MembertypeException;
import be.informatievlaanderen.vsds.demonstrator.member.application.exceptions.MissingCollectionException;
import org.apache.jena.rdf.model.Model;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class MemberValidatorImpl implements MemberValidator {

    private final StreamsConfig streams;

    public MemberValidatorImpl(StreamsConfig streams) {
        this.streams = streams;
    }

    @Override
    public void validate(Model member, String collectionName) {
        Optional<EventStreamConfig> streamConfig = streams.getStreams().stream().filter(streams -> Objects.equals(streams.getName(), collectionName)).findFirst();
        if(streamConfig.isEmpty()) {
            throw new MissingCollectionException(collectionName);
        } else if (!testMembertype(member, streamConfig.get().getMemberType())) {
            throw new MembertypeException("todo", streamConfig.get().getMemberType());
        }

    }

    private boolean testMembertype(Model member, String memberType) {
        //TODO
        return true;
    }
}
