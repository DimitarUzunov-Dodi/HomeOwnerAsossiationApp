package nl.tudelft.sem.template.authentication.domain.user;

/**
 * A DDD domain event that indicated a user was created.
 */
public class UserWasCreatedEvent {
    private final UserId userId;

    public UserWasCreatedEvent(UserId userId) {
        this.userId = userId;
    }

    public UserId getUserId() {
        return this.userId;
    }
}
