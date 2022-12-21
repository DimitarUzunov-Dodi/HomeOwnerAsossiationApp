package nl.tudelft.sem.template.association.models;

import lombok.Data;

@Data
public class RuleProposalRequestModelInternal {
    private Integer associationId;
    private Integer userId;
    private String rule;
}
