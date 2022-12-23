package nl.tudelft.sem.template.voting.models;

import lombok.Data;

@Data
public class RuleVoteRequestModel {
    private Long ruleVoteId;
    private String userId;
    private String vote;
    private int associationId;
}
