package nl.tudelft.sem.template.authentication.domain.user;

/**
 * A DDD domain event that indicated a user was created.
 */
public class UserWasCreatedEvent {
    private final MemberId memberId;

    public UserWasCreatedEvent(MemberId memberId) {
        this.memberId = memberId;
    }

    public MemberId getMemberId() {
        return this.memberId;
    }
}
