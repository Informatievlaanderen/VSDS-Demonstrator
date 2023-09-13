package be.informatievlaanderen.vsds.demonstrator.triple.domain.repositories;

import be.informatievlaanderen.vsds.demonstrator.triple.domain.entities.MemberDescription;

public interface TripleRepository {
    MemberDescription getById(String id);
}
