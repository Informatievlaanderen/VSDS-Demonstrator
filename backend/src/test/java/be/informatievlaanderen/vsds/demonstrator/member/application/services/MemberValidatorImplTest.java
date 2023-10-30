package be.informatievlaanderen.vsds.demonstrator.member.application.services;

import be.informatievlaanderen.vsds.demonstrator.member.application.config.EventStreamConfig;
import be.informatievlaanderen.vsds.demonstrator.member.application.config.StreamsConfig;
import be.informatievlaanderen.vsds.demonstrator.member.application.exceptions.MemberTypeException;
import be.informatievlaanderen.vsds.demonstrator.member.application.exceptions.MissingCollectionException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberValidatorImplTest {
    private static final String MOBILITY_HINDRANCE_MEMBER_TYPE = "https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder";
    private static final String COLLECTION = "gipod";
    private MemberValidatorImpl memberValidator;
    private Model member;

    @BeforeEach
    void setUp() throws IOException {
        member = readModelFromFile();
    }

    @Test
    void given_ConfigWithCollectionAndMemberType_when_MemberIsTested_then_ThrowNoException() {
        setUpValidator(MOBILITY_HINDRANCE_MEMBER_TYPE);

        assertThatNoException().isThrownBy(() -> memberValidator.validate(member, COLLECTION));
    }

    @Test
    void given_ConfigWithoutCollection_when_MemberIsTested_then_ThrowException() {
        final String collection = "absent-collection";
        setUpValidator(MOBILITY_HINDRANCE_MEMBER_TYPE);

        assertThatThrownBy(() -> memberValidator.validate(member, collection))
                .isInstanceOf(MissingCollectionException.class)
                .hasMessage("Eventstream with name %s could not be found.", collection);
    }

    @Test
    void given_ConfigWithCollectionAndMemberType_when_MemberFromOtherTypeIsTested_then_ThrowException() {
        final String memberType = "http://some-domain.org/ns#other-type";
        setUpValidator(memberType);

        assertThatThrownBy(() -> memberValidator.validate(member, COLLECTION))
                .isInstanceOf(MemberTypeException.class)
                .hasMessage("Member of collection %s was not of expected type %s", COLLECTION, memberType);
    }

    private void setUpValidator(String memberType) {
        StreamsConfig configs = new StreamsConfig();
        EventStreamConfig streamConfig = new EventStreamConfig();
        streamConfig.setMemberType(memberType);
        configs.setStreams(Map.of(COLLECTION, streamConfig));
        memberValidator = new MemberValidatorImpl(configs);
    }

    private Model readModelFromFile() throws IOException {
        Path path = ResourceUtils.getFile("classpath:members/mobility-hindrance.nq").toPath();
        return RDFParser.source(path).lang(Lang.NQUADS).toModel();
    }
}