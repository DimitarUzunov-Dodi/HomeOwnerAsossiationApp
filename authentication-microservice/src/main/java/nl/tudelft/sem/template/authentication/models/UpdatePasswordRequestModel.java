package nl.tudelft.sem.template.authentication.models;

import lombok.Data;

/**
 * Model representing an update user request.
 */
@Data
public class UpdatePasswordRequestModel {
    private String memberId;
    private String password;
    private String newPassword;
}
