package nl.tudelft.sem.template.association.models;

import lombok.Data;

/**
 * Model representing a rule voting verification request.
 */
@Data
public class UserAssociationRequestModel {
    private Integer userId;
    private Integer associationId;
}
