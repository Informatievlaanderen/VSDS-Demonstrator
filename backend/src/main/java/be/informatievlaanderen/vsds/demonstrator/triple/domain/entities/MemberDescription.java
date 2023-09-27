package be.informatievlaanderen.vsds.demonstrator.triple.domain.entities;


import org.eclipse.rdf4j.model.Model;

public class MemberDescription {
    private final String memberId;
    private final Model model;

    public MemberDescription(String memberId, Model model) {
        this.memberId = memberId;
        this.model = model;
    }

    public String getMemberId() {
        return memberId;
    }

    public Model getModel() {
        return model;
    }
}
