package nl.tudelft.sem.template.association.models;

import lombok.Data;

/**
 * Model representing a request with associationId.
 */
@Data
public class UserAssociationRequestModel {
    private int userId;
    private int associationId;
}