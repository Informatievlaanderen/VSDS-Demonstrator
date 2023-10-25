package be.informatievlaanderen.vsds.demonstrator.triple.rest;

import be.informatievlaanderen.vsds.demonstrator.triple.application.services.TripleService;
import be.informatievlaanderen.vsds.demonstrator.triple.domain.valueobjects.Triple;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api")
public class TriplesController {
    private final TripleService tripleService;

    public TriplesController(TripleService tripleService) {
        this.tripleService = tripleService;
    }


    @GetMapping(value = "/triples")
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    public List<Triple> retrieveTriplesOfNode(@RequestParam String memberId, @RequestParam String collection) {
        return tripleService.getTriplesById(memberId, collection);
    }
}