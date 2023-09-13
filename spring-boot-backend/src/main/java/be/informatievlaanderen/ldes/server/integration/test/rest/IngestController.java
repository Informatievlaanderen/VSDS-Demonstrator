package be.informatievlaanderen.ldes.server.integration.test.rest;

import be.informatievlaanderen.ldes.server.integration.test.application.services.MemberGeometryService;

import be.informatievlaanderen.ldes.server.integration.test.application.valueobjects.MemberDTO;
import org.apache.jena.rdf.model.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IngestController {
    private final MemberGeometryService service;

    public IngestController(MemberGeometryService service) {
        this.service = service;
    }

    @PostMapping(value = "/members")
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    public void ingestLdesMember(@RequestBody Model model) {
        service.ingestMemberGeometry(new MemberDTO(model));
    }
}
