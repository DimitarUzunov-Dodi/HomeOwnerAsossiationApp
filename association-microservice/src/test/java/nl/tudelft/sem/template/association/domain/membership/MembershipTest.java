package nl.tudelft.sem.template.association.domain.membership;


import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class MembershipTest {
    private Membership membership;

    @BeforeEach
    void setup() {
        membership = new Membership("userId", 2, "country", "city", "Street",
                 "houseNumber", "postalCode");

    }

    @Test
    void getLeaveDate() {
        assertEquals(null, membership.getLeaveDate());
    }

    @Test
    void getCity() {
        assertEquals("city", membership.getCity());
    }

    @Test
    void setCity() {
        membership.setCity("Grad");
        assertEquals("Grad", membership.getCity());
    }

    @Test
    void getStreet() {
        assertEquals("Street", membership.getStreet());
    }

    @Test
    void setStreet() {
        membership.setStreet("Ulica");
        assertEquals("Ulica", membership.getStreet());
    }

    @Test
    void getHouseNumber() {
        assertEquals("houseNumber", membership.getHouseNumber());
    }

    @Test
    void setHouseNumber() {
        membership.setHouseNumber("39");
        assertEquals("39", membership.getHouseNumber());
    }

    @Test
    void getPostalCode() {
        assertEquals("postalCode", membership.getPostalCode());

    }

    @Test
    void setPostalCode() {
        membership.setPostalCode("1680");
        assertEquals("1680", membership.getPostalCode());
    }

    @Test
    void leave() {
        assertEquals(null, membership.getLeaveDate());
        membership.leave();
        assertNotEquals(null, membership.getLeaveDate());
    }

    @Test
    void getCountry() {
        assertEquals("country", membership.getCountry());

    }

    @Test
    void setCountry() {
        membership.setCountry("Laplandia");
        assertEquals("Laplandia", membership.getCountry());
    }
}