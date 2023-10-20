package be.informatievlaanderen.vsds.demonstrator.triple.domain.repositories;

import be.informatievlaanderen.vsds.demonstrator.triple.domain.valueobjects.Triple;

import java.util.List;

public interface TripleRepository {
    List<Triple> getById(String id);
}
