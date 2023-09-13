package be.informatievlaanderen.ldes.server.integration.test.domain.triplefetcher.services;

import be.informatievlaanderen.ldes.server.integration.test.rest.dtos.TripleDTO;

import java.util.List;

public interface RdfFetcherService {

    List<TripleDTO> fetchNode(String id);
}
