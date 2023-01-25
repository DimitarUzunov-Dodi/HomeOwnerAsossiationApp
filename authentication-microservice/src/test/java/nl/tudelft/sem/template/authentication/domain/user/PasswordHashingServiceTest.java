package nl.tudelft.sem.template.authentication.domain.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class PasswordHashingServiceTest {

    private PasswordHashingService passwordHashingService;

    @BeforeEach
    public void setUp() {
        passwordHashingService = new PasswordHashingService(new BCryptPasswordEncoder());
    }

    @Test
    public void nullTest() {
        assertThatThrownBy(() -> passwordHashingService.hash(null)).isInstanceOf(Exception.class);
    }

    @Test
    public void emptyString() {
        assertThatThrownBy(() -> passwordHashingService.hash(new Password(" "))).isInstanceOf(Exception.class);
    }

    @Test
    public void normalTest() throws Exception {
        assertThat(passwordHashingService.hash(new Password("test")))
                .isEqualTo(new HashedPassword("$2a$10$eFtiwWFlbkK5zBCylkoXIe7lD0cnCAR3H4Z1yCS.xWWGAHpWqTaU6"));
    }

}