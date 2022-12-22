package nl.tudelft.sem.template.voting.models;

import lombok.Data;

/**
 * Model representing a rule proposal request.
 */
@Data
public class RuleAmendmentRequestModel {
    private Integer associationId;
    private String userId;
    private String rule;
    private String amendment;
}
