package nl.tudelft.sem.template.authentication.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import nl.tudelft.sem.template.authentication.authentication.JwtTokenGenerator;
import nl.tudelft.sem.template.authentication.domain.user.*;
import nl.tudelft.sem.template.authentication.integration.utils.JsonUtil;
import nl.tudelft.sem.template.authentication.models.AuthenticationRequestModel;
import nl.tudelft.sem.template.authentication.models.AuthenticationResponseModel;
import nl.tudelft.sem.template.authentication.models.RegistrationRequestModel;
import nl.tudelft.sem.template.authentication.models.UpdatePasswordRequestModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@ExtendWith(SpringExtension.class)
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test", "mockPasswordEncoder", "mockTokenGenerator", "mockAuthenticationManager"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class UsersTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private transient PasswordHashingService mockPasswordEncoder;

    @Autowired
    private transient JwtTokenGenerator mockJwtTokenGenerator;

    @Autowired
    private transient AuthenticationManager mockAuthenticationManager;

    @Autowired
    private transient UserRepository userRepository;

    @Test
    public void register_withValidData_worksCorrectly() throws Exception {
        // Arrange
        final UserId testUser = new UserId("SomeUser");
        final Password testPassword = new Password("password123");
        final HashedPassword testHashedPassword = new HashedPassword("hashedTestPassword");
        when(mockPasswordEncoder.hash(testPassword)).thenReturn(testHashedPassword);

        RegistrationRequestModel model = new RegistrationRequestModel();
        model.setUserId(testUser.toString());
        model.setPassword(testPassword.toString());

        // Act
        ResultActions resultActions = mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model)));

        // Assert
        resultActions.andExpect(status().isOk());

        AppUser savedUser = userRepository.findByUserId(testUser).orElseThrow();

        assertThat(savedUser.getUserId()).isEqualTo(testUser);
        assertThat(savedUser.getPassword()).isEqualTo(testHashedPassword);
    }

    @Test
    public void register_withExistingUser_throwsException() throws Exception {
        // Arrange
        final UserId testUser = new UserId("SomeUser");
        final Password newTestPassword = new Password("password456");
        final HashedPassword existingTestPassword = new HashedPassword("password123");

        AppUser existingAppUser = new AppUser(testUser, existingTestPassword);
        userRepository.save(existingAppUser);

        RegistrationRequestModel model = new RegistrationRequestModel();
        model.setUserId(testUser.toString());
        model.setPassword(newTestPassword.toString());

        // Act
        ResultActions resultActions = mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model)));

        // Assert
        resultActions.andExpect(status().isBadRequest());

        AppUser savedUser = userRepository.findByUserId(testUser).orElseThrow();

        assertThat(savedUser.getUserId()).isEqualTo(testUser);
        assertThat(savedUser.getPassword()).isEqualTo(existingTestPassword);
    }

    @Test
    public void register_withInvalidFields_throwsException() throws Exception {
        RegistrationRequestModel model = new RegistrationRequestModel();
        model.setUserId("SomeUser");
        model.setPassword(null);

        // Act
        ResultActions resultActions = mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model)));

        // Assert
        resultActions.andExpect(status().isBadRequest());

        assertThat(resultActions.andReturn().getResponse().getErrorMessage()).isEqualTo("INVALID_FIELDS");
    }

    @Test
    public void register_withInvalidPassword_throwsException() throws Exception {
        RegistrationRequestModel model = new RegistrationRequestModel();
        model.setUserId(null);
        model.setPassword(null);

        // Act
        ResultActions resultActions = mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model)));

        // Assert
        resultActions.andExpect(status().isBadRequest());

        assertThat(resultActions.andReturn().getResponse().getErrorMessage()).isEqualTo("INVALID_FIELDS");
    }

    @Test
    public void login_withValidUser_returnsToken() throws Exception {
        // Arrange
        final UserId testUser = new UserId("SomeUser");
        final Password testPassword = new Password("password123");
        final HashedPassword testHashedPassword = new HashedPassword("hashedTestPassword");
        when(mockPasswordEncoder.hash(testPassword)).thenReturn(testHashedPassword);

        when(mockAuthenticationManager.authenticate(argThat(authentication ->
                !testUser.toString().equals(authentication.getPrincipal())
                        || !testPassword.toString().equals(authentication.getCredentials())
        ))).thenThrow(new UsernameNotFoundException("User not found"));

        final String testToken = "testJWTToken";
        when(mockJwtTokenGenerator.generateToken(
                argThat(userDetails -> userDetails.getUsername().equals(testUser.toString())))
        ).thenReturn(testToken);

        AppUser appUser = new AppUser(testUser, testHashedPassword);
        userRepository.save(appUser);

        AuthenticationRequestModel model = new AuthenticationRequestModel();
        model.setUserId(testUser.toString());
        model.setPassword(testPassword.toString());

        // Act
        ResultActions resultActions = mockMvc.perform(post("/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model)));


        // Assert
        MvcResult result = resultActions
                .andExpect(status().isOk())
                .andReturn();

        AuthenticationResponseModel responseModel = JsonUtil.deserialize(result.getResponse().getContentAsString(),
                AuthenticationResponseModel.class);

        assertThat(responseModel.getToken()).isEqualTo(testToken);

        verify(mockAuthenticationManager).authenticate(argThat(authentication ->
                testUser.toString().equals(authentication.getPrincipal())
                        && testPassword.toString().equals(authentication.getCredentials())));
    }

    @Test
    public void login_withNonexistentUsername_returns403() throws Exception {
        // Arrange
        final String testUser = "SomeUser";
        final String testPassword = "password123";

        when(mockAuthenticationManager.authenticate(argThat(authentication ->
                testUser.equals(authentication.getPrincipal())
                        && testPassword.equals(authentication.getCredentials())
        ))).thenThrow(new UsernameNotFoundException("User not found"));

        AuthenticationRequestModel model = new AuthenticationRequestModel();
        model.setUserId(testUser);
        model.setPassword(testPassword);

        // Act
        ResultActions resultActions = mockMvc.perform(post("/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model)));

        // Assert
        resultActions.andExpect(status().isForbidden());

        verify(mockAuthenticationManager).authenticate(argThat(authentication ->
                testUser.equals(authentication.getPrincipal())
                        && testPassword.equals(authentication.getCredentials())));

        verify(mockJwtTokenGenerator, times(0)).generateToken(any());
    }

    @Test
    public void login_withInvalidPassword_returns403() throws Exception {
        // Arrange
        final String testUser = "SomeUser";
        final String wrongPassword = "password1234";
        final String testPassword = "password123";
        final HashedPassword testHashedPassword = new HashedPassword("hashedTestPassword");
        when(mockPasswordEncoder.hash(new Password(testPassword))).thenReturn(testHashedPassword);

        when(mockAuthenticationManager.authenticate(argThat(authentication ->
                testUser.equals(authentication.getPrincipal())
                        && wrongPassword.equals(authentication.getCredentials())
        ))).thenThrow(new BadCredentialsException("Invalid password"));

        AppUser appUser = new AppUser(new UserId(testUser), testHashedPassword);
        userRepository.save(appUser);

        AuthenticationRequestModel model = new AuthenticationRequestModel();
        model.setUserId(testUser);
        model.setPassword(wrongPassword);

        // Act
        ResultActions resultActions = mockMvc.perform(post("/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model)));

        // Assert
        resultActions.andExpect(status().isUnauthorized());

        verify(mockAuthenticationManager).authenticate(argThat(authentication ->
                testUser.equals(authentication.getPrincipal())
                        && wrongPassword.equals(authentication.getCredentials())));

        verify(mockJwtTokenGenerator, times(0)).generateToken(any());
    }

    @Test
    public void changePass_withValidPass_worksCorrectly() throws Exception {
        // Arrange
        final UserId testUser = new UserId("SomeUser");
        final Password testPassword = new Password("password123");
        final Password newTestPassword = new Password("newpass");
        final HashedPassword testHashedPassword = new HashedPassword("hashedTestPassword");
        final HashedPassword newTestHashedPassword = new HashedPassword("newpass");

        when(mockPasswordEncoder.hash(testPassword)).thenReturn(testHashedPassword);
        when(mockPasswordEncoder.hash(newTestPassword)).thenReturn(newTestHashedPassword);

        AppUser appUser = new AppUser(testUser, testHashedPassword);
        userRepository.save(appUser);

        UpdatePasswordRequestModel model = new UpdatePasswordRequestModel();
        model.setUserId(testUser.toString());
        model.setPassword(testPassword.toString());
        model.setNewPassword(newTestPassword.toString());
        // Act
        ResultActions resultActions = mockMvc.perform(post("/changepass")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model)));

        // Assert
        resultActions.andExpect(status().isOk());

        AppUser savedUser = userRepository.findByUserId(testUser).orElseThrow();

        assertThat(savedUser.getUserId()).isEqualTo(testUser);
        assertThat(savedUser.getPassword()).isEqualTo(newTestHashedPassword);
    }

    @Test
    public void changePass_withInvalidCredentials_throwsException() throws Exception {
        // Arrange
        final UserId testUser = new UserId("SomeUser");
        final Password testPassword = new Password("password");
        final Password incorrectTestPassword = new Password("incorrect");
        final Password newTestPassword = new Password("newpass");
        final HashedPassword testHashedPassword = new HashedPassword("password");
        final HashedPassword incorrectTestHashedPassword = new HashedPassword("incorrect");
        final HashedPassword newTestHashedPassword = new HashedPassword("newpass");

        when(mockPasswordEncoder.hash(testPassword)).thenReturn(testHashedPassword);
        when(mockPasswordEncoder.hash(newTestPassword)).thenReturn(newTestHashedPassword);
        when(mockPasswordEncoder.hash(incorrectTestPassword)).thenReturn(incorrectTestHashedPassword);

        when(mockAuthenticationManager.authenticate(argThat(authentication ->
                testUser.equals(authentication.getPrincipal())
                        && incorrectTestPassword.equals(authentication.getCredentials())
        ))).thenThrow(new BadCredentialsException("Invalid password"));

        AppUser appUser = new AppUser(testUser, testHashedPassword);
        userRepository.save(appUser);

        UpdatePasswordRequestModel model = new UpdatePasswordRequestModel();
        model.setUserId(testUser.toString());
        model.setPassword(incorrectTestPassword.toString());
        model.setNewPassword(newTestPassword.toString());
        // Act
        ResultActions resultActions = mockMvc.perform(post("/changepass")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model)));

        // Assert

        resultActions.andExpect(status().isBadRequest());

        AppUser savedUser = userRepository.findByUserId(testUser).orElseThrow();

        assertThat(savedUser.getUserId()).isEqualTo(testUser);
        assertThat(savedUser.getPassword()).isEqualTo(testHashedPassword);
    }

    @Test
    public void changePass_withSamePass_throwsException() throws Exception {
        // Arrange
        final UserId testUser = new UserId("SomeUser");
        final Password testPassword = new Password("password");
        final Password newTestPassword = new Password("newpass");
        final HashedPassword testHashedPassword = new HashedPassword("password");
        final HashedPassword newTestHashedPassword = new HashedPassword("newpass");

        when(mockPasswordEncoder.hash(testPassword)).thenReturn(testHashedPassword);
        when(mockPasswordEncoder.hash(newTestPassword)).thenReturn(newTestHashedPassword);

        AppUser appUser = new AppUser(testUser, testHashedPassword);
        userRepository.save(appUser);

        UpdatePasswordRequestModel model = new UpdatePasswordRequestModel();
        model.setUserId(testUser.toString());
        model.setPassword(newTestPassword.toString());
        model.setNewPassword(newTestPassword.toString());

        // Act
        ResultActions resultActions = mockMvc.perform(post("/changepass")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model)));

        // Assert

        resultActions.andExpect(status().isBadRequest());

        AppUser savedUser = userRepository.findByUserId(testUser).orElseThrow();

        assertThat(savedUser.getUserId()).isEqualTo(testUser);
        assertThat(savedUser.getPassword()).isEqualTo(testHashedPassword);
    }

    @Test
    public void changePass_withInvalidPassword_throwsException() throws Exception {
        final UserId testUser = new UserId("SomeUser");
        final Password testPassword = new Password("password");
        final Password incorrectTestPassword = new Password("incorrect");
        final Password newTestPassword = new Password("newpass");
        final HashedPassword testHashedPassword = new HashedPassword("password");
        final HashedPassword incorrectTestHashedPassword = new HashedPassword("incorrect");
        final HashedPassword newTestHashedPassword = new HashedPassword("newpass");

        when(mockPasswordEncoder.hash(testPassword)).thenReturn(testHashedPassword);
        when(mockPasswordEncoder.hash(newTestPassword)).thenReturn(newTestHashedPassword);
        when(mockPasswordEncoder.hash(incorrectTestPassword)).thenReturn(incorrectTestHashedPassword);

        AppUser appUser = new AppUser(testUser, testHashedPassword);
        userRepository.save(appUser);

        UpdatePasswordRequestModel model = new UpdatePasswordRequestModel();
        model.setUserId(testUser.toString());
        model.setPassword(incorrectTestPassword.toString());
        model.setNewPassword(null);
        // Act
        ResultActions resultActions = mockMvc.perform(post("/changepass")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model)));

        // Assert

        resultActions.andExpect(status().isBadRequest());

        assertThat(resultActions.andReturn().getResponse().getErrorMessage()).isEqualTo("One or more fields are invalid!");

        AppUser savedUser = userRepository.findByUserId(testUser).orElseThrow();

        assertThat(savedUser.getUserId()).isEqualTo(testUser);
        assertThat(savedUser.getPassword()).isEqualTo(testHashedPassword);
    }
}
