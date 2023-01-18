package nl.tudelft.sem.template.association.domain.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.persistence.AttributeConverter;

public class ActivityDetailsConverter implements AttributeConverter<ActivityDetails, String> {


    @Override
    public String convertToDatabaseColumn(ActivityDetails details) {
        return details.toString();
    }

    @Override
    public ActivityDetails convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }

        String[] split = dbData.split(", ");
        String eventName = split[0];
        String description = split[1];

        try {

            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
            Date startingDate = sdf.parse(split[2]);
            Date expirationDate = sdf.parse(split[3]);
            return new ActivityDetails(eventName, description, startingDate, expirationDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;

    }

}
