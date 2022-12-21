package nl.tudelft.sem.template.association.models;

import lombok.Data;

@Data
public class RuleVoteRequestModelInternal {
    private Integer associationId;
    private Integer userId;
    private String rule;
}
