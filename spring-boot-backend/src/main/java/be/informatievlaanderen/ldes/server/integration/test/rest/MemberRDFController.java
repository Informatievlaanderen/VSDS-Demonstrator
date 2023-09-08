package be.informatievlaanderen.ldes.server.integration.test.rest;

import be.informatievlaanderen.ldes.server.integration.test.domain.membergeometry.services.MemberGeometryService;
import be.informatievlaanderen.ldes.server.integration.test.rest.dtos.MapBoundsDto;
import be.informatievlaanderen.ldes.server.integration.test.rest.dtos.MemberGeometryDto;
import be.informatievlaanderen.ldes.server.integration.test.rest.dtos.TripleDTO;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
public class MemberRDFController {
    private final MemberGeometryService service;

    public MemberRDFController(MemberGeometryService service) {
        this.service = service;
    }

    @GetMapping(value = "/geometry/{memberId}")
    @CrossOrigin(origins = "*", allowedHeaders = "")
    public MemberGeometryDto retrieveLdesFragment(@PathVariable String memberId) {
        return service.getMemberById(memberId);
    }

    @PostMapping(value = "/in-rectangle", consumes = {"application/json"})
    @CrossOrigin(origins = "*", allowedHeaders = "")
    public List<MemberGeometryDto> getMembersInRectangle(@RequestBody MapBoundsDto mapBoundsDto) {
        return service.getMembersInRectangle(mapBoundsDto.getGeometry());
    }

    @GetMapping(value = "/triples/{memberId}")
    @CrossOrigin(origins = "*", allowedHeaders = "")
    public List<TripleDTO> retrieveTriplesOfNode(@PathVariable("memberId") String memberId) throws IOException {
       memberId="https://private-api.gipod.vlaanderen.be/api/v1/mobility-hindrances/15191214/29836822";
        return new TripleFetcher("test", "http://localhost:8080/rdf4j-server/repositories/").fetchNodeHttp(memberId);
//    return null;
    }
}
