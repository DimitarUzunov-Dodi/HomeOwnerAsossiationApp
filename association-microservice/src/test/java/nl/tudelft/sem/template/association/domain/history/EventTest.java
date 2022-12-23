package nl.tudelft.sem.template.association.domain.history;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class EventTest {
    private Event event;
    private Date date = new Date();
    @BeforeEach
    void setup(){
        event = new Event("description", new Date());
    }

    @Test
    void setDescription() {
        assertNotEquals("newDescription", event.getDescription());
        event.setDescription("newDescription");
        assertEquals("newDescription",event.getDescription());
    }

    @Test
    void setDate() {
        assertNotEquals(date, event.getDate());
        event.setDate(date);
        assertEquals(date,event.getDate());
    }
}