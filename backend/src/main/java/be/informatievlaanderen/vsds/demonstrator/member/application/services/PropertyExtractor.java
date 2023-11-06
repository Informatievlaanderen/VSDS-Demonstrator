package be.informatievlaanderen.vsds.demonstrator.member.application.services;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;

import java.util.List;

public interface PropertyExtractor {

	List<RDFNode> getProperties(Model model);

}
