package nl.tudelft.sem.template.association.domain.user;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class UserTest {
    private User user;

    @BeforeEach
    void setup() {
        user = new User("userId", "name");
    }


    @Test
    void getId() {
        assertEquals(0, user.getId());
    }

    @Test
    void getUserId() {
        assertEquals("userId", user.getUserId());
    }

    @Test
    void getName() {
        assertEquals("name", user.getName());
    }

    @Test
    void setName() {
        user.setName("newName");
        assertEquals("newName", user.getName());
    }
}