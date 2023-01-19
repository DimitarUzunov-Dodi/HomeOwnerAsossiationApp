package nl.tudelft.sem.template.association.models;

import lombok.Data;

/**
 * Model representing a registration request.
 */
@Data
public class RegistrationRequestModel {
    private String userId;
    private String password;
}