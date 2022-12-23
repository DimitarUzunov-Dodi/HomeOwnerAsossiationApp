package nl.tudelft.sem.template.association.domain.history;

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
        List<String> list = new ArrayList<>();

        for (Event e : events) {
            list.add(e.toString());
        }

        String str = list.toString();
        str = str.substring(1, str.length() - 1);

        return str;
    }

    @Override
    public List<Event> convertToEntityAttribute(String dbData) {
        String[] strings = dbData.substring(1, dbData.length() - 1).split(", ");
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
            String[] strings = str.split(" \\| ");

            String description = strings[1];

            String pattern = "yyyy-MM-dd";
            SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.ENGLISH);

            Date date = sdf.parse(strings[0]);

            return new Event(description, date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

}
