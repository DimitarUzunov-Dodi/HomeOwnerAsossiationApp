package nl.tudelft.sem.template.authentication.domain.user;

import static nl.tudelft.sem.template.authentication.domain.user.FieldValidation.validateField;

import lombok.EqualsAndHashCode;


/**
 * A DDD value object representing a hashed password in our domain.
 */
@EqualsAndHashCode
public class HashedPassword {
    private final transient String hash;

    /**
     * Constructor for hashed passwords.
     *
     * @param hash hashed password in string form
     * @throws InvalidFieldException if the hash is invalid
     */
    public HashedPassword(String hash) throws InvalidFieldException {
        if (hash != null && !hash.isEmpty()) {
            this.hash = hash;
        } else {
            throw new InvalidFieldException();
        }
    }

    @Override
    public String toString() {
        return hash;
    }
}
