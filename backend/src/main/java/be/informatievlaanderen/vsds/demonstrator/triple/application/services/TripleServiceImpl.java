package be.informatievlaanderen.vsds.demonstrator.triple.application.services;

import be.informatievlaanderen.vsds.demonstrator.triple.domain.valueobjects.Triple;
import be.informatievlaanderen.vsds.demonstrator.triple.domain.repositories.TripleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TripleServiceImpl implements TripleService {
    private final TripleRepository repository;

    public TripleServiceImpl(TripleRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Triple> getTriplesById(String id, String collection) {
        return repository.getById(id, collection);
    }
}
