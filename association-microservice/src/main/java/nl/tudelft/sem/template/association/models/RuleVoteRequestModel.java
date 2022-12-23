package nl.tudelft.sem.template.association.models;

import lombok.Data;

@Data
public class RuleVoteRequestModel {
    private Integer associationId;
    private String userId;
    private String rule;
}
