package nl.tudelft.sem.template.voting.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.AttributeConverter;

public class CandidateAttributeConverter implements AttributeConverter<List<Integer>, String> {

    @Override
    public String convertToDatabaseColumn(List<Integer> attribute) {
        if (attribute.size() == 0) {
            return null;
        }
        return attribute.toString().substring(1, attribute.toString().length() - 1);
    }

    @Override
    public List<Integer> convertToEntityAttribute(String dbData) {
        List<Integer> candidates = new ArrayList<>();
        if (dbData == null) {
            return candidates;
        }

        String[] split = dbData.split(", ");
        for (String s : split) {
            candidates.add(Integer.parseInt(s));
        }
        return candidates;
    }
}
