package nl.tudelft.sem.template.association.models;

import lombok.Data;

@Data
public class ElectionVoteRequestModelInternal {
    private int voterId;
    private int associationId;
    private int candidateId;
}
