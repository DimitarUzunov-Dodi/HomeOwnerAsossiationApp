package nl.tudelft.sem.template.association.models;

import lombok.Data;

@Data
public class ElectionVoteRequestModel {
    private String voterId;
    private int associationId;
    private String candidateId;
}
