package nl.tudelft.sem.template.voting.models;

import lombok.Data;

/**
 * Model representing a request with associationId.
 */
@Data
public class VoteRequestModel {
    private int voterId;
    private int associationId;
    private int candidateId;
}