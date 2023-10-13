package be.informatievlaanderen.vsds.demonstrator.member.application.services;

import be.informatievlaanderen.vsds.demonstrator.member.application.config.EventStreamConfig;
import be.informatievlaanderen.vsds.demonstrator.member.application.config.StreamsConfig;
import be.informatievlaanderen.vsds.demonstrator.member.application.exceptions.MissingCollectionException;
import org.apache.jena.rdf.model.Model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class MemberValidatorImplTest {
    private static final String COLLECTION = "collection";

    private StreamsConfig configs;
    private EventStreamConfig streamConfig;
    private MemberValidatorImpl memberValidator;

    @BeforeEach
    void setUp() {
        configs = new StreamsConfig();
        streamConfig = new EventStreamConfig();
        configs.setStreams(Map.of(COLLECTION, streamConfig));
        memberValidator = new MemberValidatorImpl(configs);
    }

    @Test
    void testCollectionNamePresent() {
        Model member = mock(Model.class);

        assertDoesNotThrow(() -> memberValidator.validate(member, COLLECTION));
        assertThrows(MissingCollectionException.class, () -> memberValidator.validate(member, "notPresent"));
    }
}