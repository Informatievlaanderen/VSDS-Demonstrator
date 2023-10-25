package be.informatievlaanderen.vsds.demonstrator.member.application.services;

import be.informatievlaanderen.vsds.demonstrator.member.application.config.EventStreamConfig;
import be.informatievlaanderen.vsds.demonstrator.member.application.config.StreamsConfig;
import be.informatievlaanderen.vsds.demonstrator.member.application.exceptions.MissingCollectionException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MemberValidatorImplTest {
    private static final String COLLECTION = "gipod";

    private MemberValidatorImpl memberValidator;

    @BeforeEach
    void setUp() {
        StreamsConfig configs = new StreamsConfig();
        EventStreamConfig streamConfig = new EventStreamConfig();
        configs.setStreams(Map.of(COLLECTION, streamConfig));
        memberValidator = new MemberValidatorImpl(configs);
    }

    @Test
    void testCollectionNamePresent() throws IOException {
        Model member = readModelFromFile();
        assertDoesNotThrow(() -> memberValidator.validate(member, COLLECTION));
        assertThrows(MissingCollectionException.class, () -> memberValidator.validate(member, "notPresent"));
    }

    private Model readModelFromFile() throws IOException {
        Path path = ResourceUtils.getFile("classpath:members/mobility-hindrance.nq").toPath();
        return RDFParser.source(path).lang(Lang.NQUADS).toModel();
    }
}