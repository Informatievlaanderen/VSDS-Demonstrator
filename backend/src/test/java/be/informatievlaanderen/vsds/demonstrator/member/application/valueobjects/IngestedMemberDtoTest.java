package be.informatievlaanderen.vsds.demonstrator.member.application.valueobjects;

import be.informatievlaanderen.vsds.demonstrator.member.application.config.EventStreamConfig;
import be.informatievlaanderen.vsds.demonstrator.member.application.exceptions.NoGeometryProvidedException;
import be.informatievlaanderen.vsds.demonstrator.member.domain.member.entities.Member;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.impl.NodeIteratorImpl;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.util.iterator.NiceIterator;
import org.junit.jupiter.api.Test;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class IngestedMemberDtoTest {
    private static final String ID = "https://data.vlaanderen.be/id/verkeersmetingen/Verkeersmeting/3774/aantal/5/2023-10-16T09:01:43.861Z";
    private static final String AWV_COLLECTION = "verkeersmeting";
    private static final String BLUE_BIKES_COLLECTION = "bluebikes";

    @Test
    void given_ValidMember_test_Ingestion() throws FactoryException, TransformException {
        EventStreamConfig eventStreamConfig = initAwvConfig();
        Model model = RDFParser.source("members/traffic-count.nq").lang(Lang.NQUADS).toModel();
        IngestedMemberDto ingestedMember = new IngestedMemberDto(AWV_COLLECTION, model);
        Map<String, String> properties = Map.of(
                "fullName", "Kennedy snede 3",
                "countObservationResult", "4",
                "observationResult", "4"
        );

        Member member = ingestedMember.getMember(eventStreamConfig);

        assertThat(member.getMemberId()).isEqualTo(ID);
        assertThat(member.getProperties()).containsAllEntriesOf(properties);
        assertThat(member.getTimestamp()).isEqualTo("2023-10-16T10:00:00");
    }

    @Test
    void given_MemberWithTimestampWithoutTZ_when_IngestMember_then_CreateUtcTimestamp() throws FactoryException, TransformException {
        EventStreamConfig eventStreamConfig = initBlueBikesConfig();
        Model model = RDFParser.source("members/blue-bike-station.nq").lang(Lang.NQUADS).toModel();
        IngestedMemberDto ingestedMemberDto = new IngestedMemberDto(BLUE_BIKES_COLLECTION, model);

        Member member = ingestedMemberDto.getMember(eventStreamConfig);

        assertThat(member.getTimestamp()).isEqualTo("2023-10-27T11:01:25");
    }

    @Test
    void given_MemberWithoutGeometry_when_IngestMember_then_ThrowException() {
        EventStreamConfig eventStreamConfig = initAwvConfig();
        Model model = mock(Model.class);
        IngestedMemberDto ingestedMemberDto = new IngestedMemberDto(AWV_COLLECTION, model);
        when(model.listObjectsOfProperty(any())).thenReturn(new NodeIteratorImpl(NiceIterator.emptyIterator(), null));

        assertThatThrownBy(() -> ingestedMemberDto.getMember(eventStreamConfig))
                .isInstanceOf(NoGeometryProvidedException.class)
                .hasMessage("No geometry could be found in the provided member");
    }

    private EventStreamConfig initAwvConfig() {
        EventStreamConfig eventStreamConfig = new EventStreamConfig();
        eventStreamConfig.setMemberType("https://data.vlaanderen.be/ns/verkeersmetingen#Verkeersmeting");
        eventStreamConfig.setTimestampPath("http://www.w3.org/ns/prov#generatedAtTime");
        eventStreamConfig.setVersionOfPath("http://purl.org/dc/terms/isVersionOf");
        eventStreamConfig.setPropertyPredicates(Map.of(
                "fullName", "http://custom/meetpunt#VolledigeNaam",
                "countObservationResult", "http://def.isotc211.org/iso19156/2011/CountObservation#OM_CountObservation.result",
                "observationResult", "http://def.isotc211.org/iso19156/2011/Observation#OM_Observation.result"
        ));
        return eventStreamConfig;
    }

    private EventStreamConfig initBlueBikesConfig() {
        EventStreamConfig eventStreamConfig = new EventStreamConfig();
        eventStreamConfig.setMemberType("https://purl.eu/ns/mobility/passenger-transport-hubs#ResourceReport");
        eventStreamConfig.setTimestampPath("http://www.w3.org/ns/prov#generatedAtTime");
        eventStreamConfig.setTimezoneId("Europe/Brussels");
        eventStreamConfig.setVersionOfPath("http://purl.org/dc/terms/isVersionOf");
        eventStreamConfig.setPropertyPredicates(Map.of(
                "fullName", "http://schema.org/name",
                "available", "http://schema.mobivoc.org/#currentValue"
        ));
        return eventStreamConfig;
    }
}