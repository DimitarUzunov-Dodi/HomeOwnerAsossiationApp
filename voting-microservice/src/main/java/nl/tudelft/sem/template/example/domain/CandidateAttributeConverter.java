package nl.tudelft.sem.template.example.domain;

import javax.persistence.AttributeConverter;
import java.util.ArrayList;
import java.util.List;

public class CandidateAttributeConverter implements AttributeConverter<List<Integer>, String> {

    @Override
    public String convertToDatabaseColumn(List<Integer> attribute) {
        return attribute.toString().substring(1, attribute.toString().length() - 1);
    }

    @Override
    public List<Integer> convertToEntityAttribute(String dbData) {
        List<Integer> candidates = new ArrayList<>();

        String[] split = dbData.split(", ");
        for (String s : split) {
            candidates.add(Integer.parseInt(s));
        }
        return candidates;
    }
}
