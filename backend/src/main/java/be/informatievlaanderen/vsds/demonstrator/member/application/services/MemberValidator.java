package be.informatievlaanderen.vsds.demonstrator.member.application.services;

import org.apache.jena.rdf.model.Model;

public interface MemberValidator {
    void validate(Model member, String collectionName);
}
