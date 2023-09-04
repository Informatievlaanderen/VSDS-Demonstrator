package be.informatievlaanderen.ldes.server.integration.test.rest;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class MemberRDFController {

    @GetMapping(value = "{memberId}")
    @CrossOrigin(origins = "*", allowedHeaders = "")
    public List<TripleDTO> retrieveLdesFragment(@PathVariable("memberId") String memberId) {
       return List.of(new TripleDTO(memberId,"ex:hasFood", UUID.randomUUID().toString()));
    }
}
