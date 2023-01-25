package nl.tudelft.sem.template.authentication.domain.user;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import org.junit.jupiter.api.Test;

class AppUserTest {

    @Test
    public void testEqualsNull() throws InvalidFieldException {
        AppUser appUser = new AppUser(new UserId("userid"), new HashedPassword("hash"));
        assertThat(appUser.equals(null)).isFalse();
    }

    @Test
    public void testEqualsDifferentClass() throws InvalidFieldException {
        AppUser appUser = new AppUser(new UserId("userid"), new HashedPassword("hash"));
        Date date = new Date(1040);
        assertThat(appUser.equals(date)).isFalse();
    }

    @Test
    public void testEquals() throws InvalidFieldException {
        AppUser appUser = new AppUser(new UserId("userid"), new HashedPassword("hash"));
        assertThat(appUser.equals(appUser)).isTrue();
    }

    @Test
    public void testEqualsDifferentObject() throws InvalidFieldException {
        AppUser appUser1 = new AppUser(new UserId("userid"), new HashedPassword("hash"));
        AppUser appUser2 = new AppUser(new UserId("userid"), new HashedPassword("hash"));
        assertThat(appUser1.equals(appUser2)).isTrue();
    }

    @Test
    public void testNotEqualsUserId() throws InvalidFieldException {
        AppUser appUser1 = new AppUser(new UserId("userid"), new HashedPassword("hash"));
        AppUser appUser2 = new AppUser(new UserId("userid1"), new HashedPassword("hash"));
        assertThat(appUser1.equals(appUser2)).isFalse();
    }

    @Test
    public void testEqualsButPasswordDifferent() throws InvalidFieldException {
        AppUser appUser1 = new AppUser(new UserId("userid"), new HashedPassword("hash"));
        AppUser appUser2 = new AppUser(new UserId("userid"), new HashedPassword("hash1"));
        assertThat(appUser1.equals(appUser2)).isTrue();
    }

    @Test
    public void changePassword() throws InvalidFieldException {
        AppUser appUser1 = new AppUser(new UserId("userid"), new HashedPassword("hash"));
        appUser1.changePassword(new HashedPassword("hash2"));

        assertThat(appUser1.getPassword()).isEqualTo(new HashedPassword("hash2"));
    }

}