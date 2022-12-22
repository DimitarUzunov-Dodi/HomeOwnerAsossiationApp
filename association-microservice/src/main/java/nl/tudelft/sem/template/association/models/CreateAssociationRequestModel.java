package nl.tudelft.sem.template.association.models;

import lombok.Data;

/**
 * Model representing a request with associationId.
 */
@Data
public class CreateAssociationRequestModel {
    String name;
    String country;
    String city;
    String description;
    int councilNumber;
}