package be.informatievlaanderen.ldes.server.integration.test.rest;

import be.informatievlaanderen.ldes.server.integration.test.domain.membergeometry.services.MemberGeometryService;
import be.informatievlaanderen.ldes.server.integration.test.rest.config.GraphDBConfig;
import be.informatievlaanderen.ldes.server.integration.test.rest.dtos.MapBoundsDto;
import be.informatievlaanderen.ldes.server.integration.test.rest.dtos.MemberGeometryDto;
import be.informatievlaanderen.ldes.server.integration.test.rest.dtos.TripleDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
public class MemberRDFController {
    private final MemberGeometryService service;
    private final GraphDBConfig graphDBConfig;

    public MemberRDFController(MemberGeometryService service, GraphDBConfig graphDBConfig) {
        this.service = service;
        this.graphDBConfig = graphDBConfig;
    }

    @GetMapping(value = "/geometry/{memberId}")
    public MemberGeometryDto retrieveLdesFragment(@PathVariable String memberId) {
        return service.getMemberById(memberId);
    }

    @PostMapping(value = "/in-rectangle", consumes = {"application/json"})
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    public List<MemberGeometryDto> getMembersInRectangle(@RequestBody MapBoundsDto mapBoundsDto, @RequestParam LocalDateTime timestamp) {
        return service.getMembersInRectangle(mapBoundsDto.getGeometry(), timestamp);
    }

    @GetMapping(value = "/triples/**")
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    public List<TripleDTO> retrieveTriplesOfNode(HttpServletRequest request) throws IOException {
        String requestURL = request.getRequestURL().toString();

        String memberId = requestURL.split("/triples/")[1];
        return new TripleFetcher(graphDBConfig.getRepositoryId(), graphDBConfig.getUrl()).fetchNodeHttp(memberId);
    }
}
