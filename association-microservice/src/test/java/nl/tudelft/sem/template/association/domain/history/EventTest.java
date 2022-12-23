package nl.tudelft.sem.template.association.domain.history;


import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;




class EventTest {
    private Event event;
    private Date date = new Date();

    @BeforeEach
    void setup() {
        event = new Event("description", new Date());
    }

    @Test
    void setDescription() {
        assertNotEquals("newDescription", event.getDescription());
        event.setDescription("newDescription");
        assertEquals("newDescription", event.getDescription());
    }

    @Test
    void setDate() {
        event.setDate(date);
        assertEquals(date, event.getDate());
    }
}