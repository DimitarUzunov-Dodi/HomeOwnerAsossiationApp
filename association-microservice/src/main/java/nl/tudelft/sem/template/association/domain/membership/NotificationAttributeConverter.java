package nl.tudelft.sem.template.association.domain.membership;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import nl.tudelft.sem.template.association.domain.history.Notification;

@Converter
public class NotificationAttributeConverter implements AttributeConverter<List<Notification>, String> {
    @Override
    public String convertToDatabaseColumn(List<Notification> attribute) {
        if (attribute  == null || attribute.isEmpty()) {
            return null;
        }
        List<String> list = new ArrayList<>();

        for (Notification e : attribute) {
            list.add(e.toString());
        }

        String str = list.toString();
        str = str.substring(1, str.length() - 1);

        return str;
    }

    @Override
    public List<Notification> convertToEntityAttribute(String dbData) {
        List<Notification> res = new ArrayList<>();
        if (dbData == null) {
            return res;
        }
        String[] strings = dbData.substring(1, dbData.length() - 1).split(", ");

        for (String str : strings) {
            res.add(convertStringToNotification(str));
        }

        return res;
    }

    /**
     * Parses a notification in its string form.
     *
     * @param str string to be parsed
     * @return the parsed notification
     */
    public Notification convertStringToNotification(String str) {
        try {
            String[] strings = str.split(" \\| ");

            String description = strings[1];

            String pattern = "yyyy-MM-dd";
            SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.ENGLISH);

            Date date = sdf.parse(strings[0]);

            Notification notification = new Notification(description, date);
            boolean read = Boolean.parseBoolean(strings[3]);
            notification.setRead(read);

            return notification;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
