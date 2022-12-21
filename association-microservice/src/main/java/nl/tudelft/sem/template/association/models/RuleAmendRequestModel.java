package nl.tudelft.sem.template.association.models;

import lombok.Data;

@Data
public class RuleAmendRequestModel {
    private Integer associationId;
    private String userId;
    private String rule;
    private String amendment;
}
