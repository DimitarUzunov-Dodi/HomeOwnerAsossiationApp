package nl.tudelft.sem.template.example.domain.history;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class EventListAttributeConverter implements AttributeConverter<List<Event>, String> {

    @Override
    public String convertToDatabaseColumn(List<Event> events) {
        List<String> res = new ArrayList<>();

        for (Event e : events) {
            res.add(e.toString());
        }

        return res.toString();
    }

    @Override
    public List<Event> convertToEntityAttribute(String dbData) {
        String[] strings = dbData.split(", ");
        List<Event> res = new ArrayList<>();

        for (String str : strings) {
            res.add(convertStringToEvent(str));
        }

        return res;
    }

    /**
     * Parses an event in its string form.
     *
     * @param str string to be parsed
     * @return the parsed event
     */
    public Event convertStringToEvent(String str) {
        try {
            String[] strings = str.split(System.lineSeparator());
            SimpleDateFormat parser = new SimpleDateFormat("YYYY-MM-DD HH:HH", Locale.ENGLISH);
            String description = strings[1];
            Date date = parser.parse(strings[0].substring(0, strings[0].length() - 1));
            return new Event(description, date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

}
