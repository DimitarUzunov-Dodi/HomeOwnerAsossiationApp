package nl.tudelft.sem.template.association.domain.activity;

import java.util.*;
import javax.persistence.AttributeConverter;


public class UserIdConverter implements AttributeConverter<List<String>, String> {

    @Override
    public String convertToDatabaseColumn(List<String> ids) {
        return ids.toString().substring(1, ids.toString().length() - 1);
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        List<String> ids = new ArrayList<>();

        if (dbData == null || dbData.isEmpty()) {
            return ids;
        }

        String[] split = dbData.split(", ");
        for (String s : split) {
            ids.add(s);
        }
        return ids;
    }
}





