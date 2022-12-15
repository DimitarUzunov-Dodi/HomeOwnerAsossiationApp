package nl.tudelft.sem.template.authentication.domain.user;

import java.util.Objects;
import javax.persistence.*;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.authentication.domain.HasEvents;

/**
 * A DDD entity representing an application user in our domain.
 */
@Entity
@Table(name = "users")
@NoArgsConstructor
public class AppUser extends HasEvents {
    /**
     * Identifier for the application user.
     */
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, unique = true)
    private int id;

    @Column(name = "member_id", nullable = false, unique = true)
    @Convert(converter = MemberIdAttributeConverter.class)
    private MemberId memberId;

    @Column(name = "password_hash", nullable = false)
    @Convert(converter = HashedPasswordAttributeConverter.class)
    private HashedPassword password;

    /**
     * Create new application user.
     *
     * @param memberId The MemberId for the new user
     * @param password The password for the new user
     */
    public AppUser(MemberId memberId, HashedPassword password) {
        this.memberId = memberId;
        this.password = password;
        this.recordThat(new UserWasCreatedEvent(memberId));
    }

    public void changePassword(HashedPassword password) {
        this.password = password;
        this.recordThat(new PasswordWasChangedEvent(this));
    }

    public MemberId getMemberId() {
        return memberId;
    }

    public HashedPassword getPassword() {
        return password;
    }

    /**
     * Equality is only based on the identifier.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AppUser appUser = (AppUser) o;
        return id == (appUser.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberId);
    }
}
