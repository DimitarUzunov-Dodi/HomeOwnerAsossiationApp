package nl.tudelft.sem.template.association.models;

import lombok.Data;

/**
 * Model representing a request with associationId.
 */
@Data
public class JoinAssociationRequestModel {
    int userId;
    int associationId;
    String country;
    String city;
    String street;
    String houseNumber;
    String postalCode;
}