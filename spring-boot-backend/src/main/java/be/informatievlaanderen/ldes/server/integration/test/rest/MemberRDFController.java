package be.informatievlaanderen.ldes.server.integration.test.rest;

import be.informatievlaanderen.ldes.server.integration.test.domain.membergeometry.services.MemberGeometryService;
import be.informatievlaanderen.ldes.server.integration.test.rest.dtos.MapBoundsDto;
import be.informatievlaanderen.ldes.server.integration.test.rest.dtos.MemberGeometryDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MemberRDFController {
    private final MemberGeometryService service;

    public MemberRDFController(MemberGeometryService service) {
        this.service = service;
    }

    @GetMapping(value = "{memberId}")
    @CrossOrigin(origins = "*", allowedHeaders = "")
    public MemberGeometryDto retrieveLdesFragment(@PathVariable String memberId) {
        return service.getMemberById(memberId);
    }

    @PostMapping(value = "/in-rectangle", consumes = {"application/json"})
    @CrossOrigin(origins = "*", allowedHeaders = "")
    public List<MemberGeometryDto> getMembersInRectangle(@RequestBody MapBoundsDto mapBoundsDto) {
        return service.getMembersInRectangle(mapBoundsDto.getGeometry());
    }
}
