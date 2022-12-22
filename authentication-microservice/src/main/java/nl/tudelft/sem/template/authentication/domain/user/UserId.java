package nl.tudelft.sem.template.authentication.domain.user;

import static nl.tudelft.sem.template.authentication.domain.user.FieldValidation.validateField;

import lombok.EqualsAndHashCode;

/**
 * A DDD value object representing a UserID in our domain.
 */
@EqualsAndHashCode
public class UserId {
    private final transient String userIdValue;

    /**
     * Constructor for user ids.
     *
     * @param userId userId in string form
     * @throws InvalidFieldException if the userId is invalid
     */
    public UserId(String userId) throws InvalidFieldException {
        if (validateField(userId)) {
            this.userIdValue = userId;
        } else {
            throw new InvalidFieldException();
        }
    }

    @Override
    public String toString() {
        return userIdValue;
    }
}
