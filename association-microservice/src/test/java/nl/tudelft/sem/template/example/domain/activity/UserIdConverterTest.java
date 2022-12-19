package nl.tudelft.sem.template.example.domain.activity;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class UserIdConverterTest {
    List<Integer> ids;
    UserIdConverter converter;

    @BeforeEach
    void setUp() {
        ids = new ArrayList<Integer>();
        ids.add(1);
        ids.add(2);
        ids.add(3);
        converter = new UserIdConverter();
    }

    @Test
    void convertToDatabaseColumn() {
        assertEquals("1, 2, 3", converter.convertToDatabaseColumn(ids));
        ids.add(1);
        ids.add(2);
        assertEquals("1, 2, 3, 1, 2", converter.convertToDatabaseColumn(ids));
    }

    @Test
    void convertToEntityAttribute() {
        assertEquals(ids, converter.convertToEntityAttribute("1, 2, 3"));
        ids.add(1);
        ids.add(2);
        assertEquals(ids, converter.convertToEntityAttribute("1, 2, 3, 1, 2"));

    }
}