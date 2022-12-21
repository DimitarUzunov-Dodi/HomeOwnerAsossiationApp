package nl.tudelft.sem.template.association.domain.activity;

import java.util.*;
import javax.persistence.AttributeConverter;


public class UserIdConverter implements AttributeConverter<List<Integer>, String> {

    @Override
    public String convertToDatabaseColumn(List<Integer> ids) {
        return ids.toString().substring(1, ids.toString().length() - 1);
    }

    @Override
    public List<Integer> convertToEntityAttribute(String dbData) {
        List<Integer> ids = new ArrayList<>();

        if (dbData.isEmpty()) {
            return ids;
        }

        String[] split = dbData.split(", ");
        for (String s : split) {
            ids.add(Integer.parseInt(s));
        }
        return ids;
    }
}





