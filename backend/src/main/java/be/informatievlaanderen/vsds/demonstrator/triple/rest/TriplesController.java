package be.informatievlaanderen.vsds.demonstrator.triple.rest;

import be.informatievlaanderen.vsds.demonstrator.triple.application.services.TripleService;
import be.informatievlaanderen.vsds.demonstrator.triple.application.valueobjects.Triple;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api")
public class TriplesController {
    private final TripleService tripleService;

    public TriplesController(TripleService tripleService) {
        this.tripleService = tripleService;
    }


    @GetMapping(value = "/triples/**")
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    public List<Triple> retrieveTriplesOfNode(HttpServletRequest request) {
        String requestURL = request.getRequestURL().toString();
        String memberId = requestURL.split("/triples/")[1];
        return tripleService.getTriplesById(memberId);
    }
}