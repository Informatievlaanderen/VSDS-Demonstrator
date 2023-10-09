package be.informatievlaanderen.vsds.demonstrator.member.application.valueobjects;

import be.informatievlaanderen.vsds.demonstrator.member.application.config.EventStreamConfig;
import be.informatievlaanderen.vsds.demonstrator.member.domain.member.entities.Member;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class IngestedMemberDtoTest {
    private static final String ID = "https://data.vlaanderen.be/id/verkeersmetingen/Verkeersmeting/3774/aantal/5/2023-10-16T09:01:43.861Z";
    private static final String STREAM = "verkeersmeting";
    private static EventStreamConfig eventStreamConfig;

    @BeforeAll
    static void beforeAll() {
        eventStreamConfig = new EventStreamConfig();
        eventStreamConfig.setMemberType("https://data.vlaanderen.be/ns/verkeersmetingen#Verkeersmeting");
        eventStreamConfig.setTimestampPath("http://www.w3.org/ns/prov#generatedAtTime");
        eventStreamConfig.setPropertyPredicates(Map.of(
                "fullName", "http://custom/meetpunt#VolledigeNaam",
                "countObservationResult", "http://def.isotc211.org/iso19156/2011/CountObservation#OM_CountObservation.result",
                "observationResult", "http://def.isotc211.org/iso19156/2011/Observation#OM_Observation.result"
        ));
    }

    @Test
    void test_ingestion() throws FactoryException, TransformException {
        Model model = RDFParser.source("members/traffic-count.nq").lang(Lang.NQUADS).toModel();
        IngestedMemberDto ingestedMember = new IngestedMemberDto("verkeersmeting", model);

        Member member = ingestedMember.getMember(eventStreamConfig);

        assertEquals(ID, member.getMemberId());
        assertEquals("Kennedy snede 3", member.getProperties().get("fullName"));
    }
}