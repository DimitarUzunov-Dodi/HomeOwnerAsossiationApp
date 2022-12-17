package nl.tudelft.sem.template.authentication.domain.user;

import lombok.EqualsAndHashCode;

/**
 * A DDD value object representing a UserID in our domain.
 */
@EqualsAndHashCode
public class UserId {
    private final transient String userIdValue;

    public UserId(String userId) {
        // validate UserID
        this.userIdValue = userId;
    }

    @Override
    public String toString() {
        return userIdValue;
    }
}
