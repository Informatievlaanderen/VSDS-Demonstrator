package be.informatievlaanderen.ldes.server.integration.test.rest;

import be.informatievlaanderen.ldes.server.integration.test.domain.membergeometry.services.MemberGeometryService;

import be.informatievlaanderen.ldes.server.integration.test.rest.config.StreamsConfig;
import be.informatievlaanderen.ldes.server.integration.test.rest.dtos.MemberDTO;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IngestController {
    private final MemberGeometryService service;
    private final StreamsConfig streamsConfig;

    public IngestController(MemberGeometryService service, StreamsConfig streamsConfig) {
        this.service = service;
        this.streamsConfig = streamsConfig;
    }

    @PostMapping(value = "/members")
    public void ingestLdesMember(@RequestBody MemberDTO memberDTO) throws FactoryException, TransformException {
        service.ingestMemberGeometry(memberDTO.getMemberGeometry(streamsConfig.getStreams()));
    }
}
