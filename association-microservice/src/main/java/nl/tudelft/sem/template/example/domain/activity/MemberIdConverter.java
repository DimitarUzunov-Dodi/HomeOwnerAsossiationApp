package nl.tudelft.sem.template.example.domain.activity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.AttributeConverter;

public class MemberIdConverter implements AttributeConverter<List<Integer>, String> {

    @Override
    public String convertToDatabaseColumn(List<Integer> ids) {
        return ids.toString().substring(1, ids.toString().length() - 1);
    }

    @Override
    public List<Integer> convertToEntityAttribute(String dbData) {
        List<Integer> ids = new ArrayList<>();

        String[] split = dbData.split(", ");
        for (String s : split) {
            ids.add(Integer.parseInt(s));
        }
        return ids;
    }
}





