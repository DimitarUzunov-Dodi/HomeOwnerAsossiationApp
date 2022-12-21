package nl.tudelft.sem.template.association.models;

import lombok.Data;

@Data
public class RuleAmendRequestModelInternal {
    private Integer associationId;
    private Integer userId;
    private String rule;
    private String amendment;
}
