package nl.tudelft.sem.template.association.models;

import lombok.Data;

/**
 * Model representing a rule voting verification request.
 */
@Data
public class RuleVerificationRequestModel {
    private String userId;
    private Integer associationId;
}
