package nl.tudelft.sem.template.voting.models;

import lombok.Data;

@Data
public class RuleVoteRequestModel {
    private Long ruleVoteId;
    private Integer userId;
    private String vote;
}
