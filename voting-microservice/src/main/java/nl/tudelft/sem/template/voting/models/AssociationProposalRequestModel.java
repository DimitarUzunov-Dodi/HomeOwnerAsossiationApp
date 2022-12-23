package nl.tudelft.sem.template.voting.models;

import lombok.Data;

@Data
public class AssociationProposalRequestModel {
    private int associationId;
    private String proposal;
}
