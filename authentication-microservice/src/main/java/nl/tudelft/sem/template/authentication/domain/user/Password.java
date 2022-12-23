package nl.tudelft.sem.template.authentication.domain.user;

import static nl.tudelft.sem.template.authentication.domain.user.FieldValidation.validateField;

import lombok.EqualsAndHashCode;

/**
 * A DDD value object representing a password in our domain.
 */
@EqualsAndHashCode
public class Password {
    private final transient String passwordValue;

    /**
     * Constructor for passwords.
     *
     * @param password password in string format
     * @throws InvalidFieldException if the password is invalid
     */
    public Password(String password) throws InvalidFieldException {
        if (validateField(password)) {
            this.passwordValue = password;
        } else {
            throw new InvalidFieldException();
        }
    }

    @Override
    public String toString() {
        return passwordValue;
    }
}
