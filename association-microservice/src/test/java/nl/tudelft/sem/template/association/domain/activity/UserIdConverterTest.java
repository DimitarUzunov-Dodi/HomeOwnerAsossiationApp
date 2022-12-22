package nl.tudelft.sem.template.association.domain.activity;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class UserIdConverterTest {
    Set<String> ids;
    UserIdConverter converter;

    @BeforeEach
    void setUp() {
        ids = new HashSet<String>();
        ids.add("1");
        ids.add("2");
        ids.add("3");
        converter = new UserIdConverter();
    }

    @Test
    void convertToDatabaseColumn() {
        assertEquals("1, 2, 3", converter.convertToDatabaseColumn(ids));
        ids.add("4");
        ids.add("5");
        assertEquals("1, 2, 3, 4, 5", converter.convertToDatabaseColumn(ids));
    }

    @Test
    void convertToEntityAttribute() {
        assertEquals(ids, converter.convertToEntityAttribute("1, 2, 3"));
        ids.add("1");
        ids.add("2");
        assertEquals(ids, converter.convertToEntityAttribute("1, 2, 3, 1, 2"));

    }

    @Test
    void testConvertToDatabaseColumnNull() {
        assertEquals(new HashSet<>(), converter.convertToEntityAttribute(null));
    }
}