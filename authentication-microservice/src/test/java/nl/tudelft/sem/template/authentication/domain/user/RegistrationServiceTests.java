package nl.tudelft.sem.template.authentication.domain.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Fail.fail;
import static org.mockito.Mockito.when;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test", "mockPasswordEncoder"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class RegistrationServiceTests {

    @Autowired
    private transient RegistrationService registrationService;

    @Autowired
    private transient PasswordHashingService mockPasswordEncoder;

    @Autowired
    private transient UserRepository userRepository;

    @Test
    public void createUser_withNullUserId_throwsException() throws Exception {
        final UserId testUser = null;
        final Password testPassword = new Password("password123");
        final HashedPassword testHashedPassword = new HashedPassword("hashedTestPassword");
        when(mockPasswordEncoder.hash(testPassword)).thenReturn(testHashedPassword);

        try {
            registrationService.registerUser(testUser, testPassword);
            fail("Registration should've failed!");
        } catch (Exception e) {
            assertThat(e.getMessage()).isNotEmpty();
        }
    }

    @Test
    public void createUser_withNullPassword_throwsException() throws Exception {
        final UserId testUser = new UserId("JEFFERY");
        final Password testPassword = null;
        final HashedPassword testHashedPassword = null;
        when(mockPasswordEncoder.hash(testPassword)).thenReturn(testHashedPassword);

        try {
            registrationService.registerUser(testUser, testPassword);
            fail("Registration should've failed!");
        } catch (Exception e) {
            assertThat(e.getMessage()).isNotEmpty();
        }
    }

    @Test
    public void createUser_withValidData_worksCorrectly() throws Exception {
        // Arrange
        final UserId testUser = new UserId("SomeUser");
        final Password testPassword = new Password("password123");
        final HashedPassword testHashedPassword = new HashedPassword("hashedTestPassword");
        when(mockPasswordEncoder.hash(testPassword)).thenReturn(testHashedPassword);

        // Act
        registrationService.registerUser(testUser, testPassword);

        // Assert
        AppUser savedUser = userRepository.findByUserId(testUser).orElseThrow();

        assertThat(savedUser.getUserId()).isEqualTo(testUser);
        assertThat(savedUser.getPassword()).isEqualTo(testHashedPassword);
    }

    @Test
    public void createUser_withExistingUser_throwsException() throws InvalidFieldException {
        // Arrange
        final UserId testUser = new UserId("SomeUser");
        final HashedPassword existingTestPassword = new HashedPassword("password123");
        final Password newTestPassword = new Password("password456");

        AppUser existingAppUser = new AppUser(testUser, existingTestPassword);
        userRepository.save(existingAppUser);

        // Act
        ThrowingCallable action = () -> registrationService.registerUser(testUser, newTestPassword);

        // Assert
        assertThatExceptionOfType(Exception.class)
                .isThrownBy(action);

        AppUser savedUser = userRepository.findByUserId(testUser).orElseThrow();

        assertThat(savedUser.getUserId()).isEqualTo(testUser);
        assertThat(savedUser.getPassword()).isEqualTo(existingTestPassword);
    }

    @Test
    public void createUser_withInvalidData_throwsException() throws InvalidFieldException {


        final UserId validUser = new UserId("clairo");
        final Password validPassword = new Password("north");

        try {
            final UserId invalidUser = new UserId("");
            final Password invalidPassword = new Password("");
            registrationService.registerUser(invalidUser, invalidPassword);
            fail("User register or hash should've thrown an exception!");
        } catch (Exception e) {
            assertThat(e.getMessage()).isNotEmpty();
        }

        try {
            final UserId invalidUser = new UserId("");
            registrationService.registerUser(invalidUser, validPassword);
            fail("User registration should've thrown an exception!");
        } catch (Exception e) {
            assertThat(e.getMessage()).isNotEmpty();
        }

        try {
            final Password invalidPassword = new Password("");
            registrationService.registerUser(validUser, invalidPassword);
            fail("Hash should've thrown an exception!");
        } catch (Exception e) {
            assertThat(e.getMessage()).isNotEmpty();
        }
    }

    @Test
    public void changePass_withNonExistingUser_throwsException() throws Exception {
        UserId nonExistingUser = new UserId("hey");
        Password newPassword = new Password("newPassword");

        try {
            registrationService.changePassword(nonExistingUser, newPassword);
            fail("Change password should've thrown an exception!");
        } catch (Exception e) {
            assertThat(e.getMessage()).isEqualTo("CREDENTIALS_NOT_MATCHING");
        }
    }

    @Test
    public void changePass_withValidPass_worksCorrectly() throws Exception {
        final UserId testUser = new UserId("victim");
        final Password oldPassword = new Password("oldpass");
        final Password newPassword = new Password("newpass");
        final HashedPassword oldHash = new HashedPassword("oldpass");
        final HashedPassword newHash = new HashedPassword("newpass");

        when(mockPasswordEncoder.hash(oldPassword)).thenReturn(oldHash);
        when(mockPasswordEncoder.hash(newPassword)).thenReturn(newHash);

        registrationService.registerUser(testUser, oldPassword);

        registrationService.changePassword(testUser, newPassword);

        AppUser savedUser = userRepository.findByUserId(testUser).orElseThrow();

        assertThat(savedUser.getUserId()).isEqualTo(testUser);
        assertThat(savedUser.getPassword()).isEqualTo(newHash);
    }
}
