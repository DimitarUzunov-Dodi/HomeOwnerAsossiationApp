package nl.tudelft.sem.template.voting.models;

import lombok.Data;

/**
 * Model representing a request to cast a vote in an election.
 */
@Data
public class ElectionVoteRequestModel {
    private String voterId;
    private int associationId;
    private String candidateId;
}