package nl.tudelft.sem.template.association.domain.history;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.*;
import org.junit.jupiter.api.Test;


public class EventListAttributeConverterTests {
    EventListAttributeConverter converter = new EventListAttributeConverter();

    @Test
    public void eventToStringTest() {
        String description = "Memories of Murder";

        Date creationDate = new GregorianCalendar(1010, Calendar.JANUARY, 10).getTime();

        Event event = new Event(description, creationDate);

        assertThat(event.toString()).isEqualTo("1010-01-10 | Memories of Murder");
    }

    @Test
    public void convertStringToEventTest() {
        String description = "Apocalypse Now";
        Date creationDate = new GregorianCalendar(1010, Calendar.JANUARY, 10).getTime();

        Event event = new Event(description, creationDate);

        assertThat(converter.convertStringToEvent(event.toString())).isEqualTo(event);
    }

    @Test
    public void convertToEntityAttributeTest() {
        List<Event> events = new ArrayList<>();
        String description1 = "Suspiria (1977)";
        String description2 = "Suspiria (2018)";

        Date date = new GregorianCalendar(1010, Calendar.JANUARY, 10).getTime();

        Event event1 = new Event(description1, date);
        Event event2 = new Event(description2, date);

        events.add(event1);
        events.add(event2);

        String eventsString = events.toString();
        eventsString = eventsString.substring(0, eventsString.length() - 1);

        assertThat(converter.convertToEntityAttribute(eventsString)).isEqualTo(events);
    }

    @Test
    public void convertToDatabaseColumn() {
        List<Event> events = new ArrayList<>();
        String description1 = "Whiplash";
        String description2 = "La La Land";

        Date date = new GregorianCalendar(1010, Calendar.JANUARY, 10).getTime();

        Event event1 = new Event(description1, date);
        Event event2 = new Event(description2, date);

        events.add(event1);
        events.add(event2);

        String eventsString = events.toString();
        eventsString = eventsString.substring(1, eventsString.length() - 1);

        assertThat(converter.convertToDatabaseColumn(events)).isEqualTo(eventsString);
    }
}
