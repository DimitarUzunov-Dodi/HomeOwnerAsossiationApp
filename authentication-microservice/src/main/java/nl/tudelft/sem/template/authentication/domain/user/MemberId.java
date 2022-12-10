package nl.tudelft.sem.template.authentication.domain.user;

import lombok.EqualsAndHashCode;

/**
 * A DDD value object representing a MemberID in our domain.
 */
@EqualsAndHashCode
public class MemberId {
    private final transient String memberIdValue;

    public MemberId(String memberId) {
        // validate MemberID
        this.memberIdValue = memberId;
    }

    @Override
    public String toString() {
        return memberIdValue;
    }
}
