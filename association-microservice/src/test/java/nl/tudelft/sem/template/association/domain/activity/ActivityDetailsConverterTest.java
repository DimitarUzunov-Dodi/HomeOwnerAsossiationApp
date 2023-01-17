package nl.tudelft.sem.template.association.domain.activity;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ActivityDetailsConverterTest {
    private ActivityDetails details;
    private Date date1;
    private Date date2;
    ActivityDetailsConverter converter;

    @BeforeEach
    void setUp() {
        date1 = new Date();
        date2 = new Date();
        details = new ActivityDetails("name", "description", date1, date2);
        converter = new ActivityDetailsConverter();
    }


    @Test
    void convertToDatabaseColumn() {
        assertEquals("name, description, " + date1 + ", " + date2, converter.convertToDatabaseColumn(details));
    }

    @Test
    void convertToEntityAttribute() {

        assertEquals("name", converter.convertToEntityAttribute("name, description, "
                + date1 + ", " + date2).getEventName());
    }
}