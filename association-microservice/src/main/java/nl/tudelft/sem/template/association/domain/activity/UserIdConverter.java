package nl.tudelft.sem.template.association.domain.activity;

import java.util.*;
import javax.persistence.AttributeConverter;


public class UserIdConverter implements AttributeConverter<Set<String>, String> {

    @Override
    public String convertToDatabaseColumn(Set<String> ids) {
        return ids.toString().substring(1, ids.toString().length() - 1);
    }

    @Override
    public Set<String> convertToEntityAttribute(String dbData) {
        Set<String> ids = new HashSet<String>();

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





