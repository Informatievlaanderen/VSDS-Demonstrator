package be.informatievlaanderen.vsds.demonstrator.triple.application.services;

import be.informatievlaanderen.vsds.demonstrator.triple.application.valueobjects.Triple;

import java.util.List;

public interface TripleService {
    List<Triple> getTriplesById(String id);
}
