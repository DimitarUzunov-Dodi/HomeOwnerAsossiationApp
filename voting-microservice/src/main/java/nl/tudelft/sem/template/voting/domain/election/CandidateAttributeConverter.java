package nl.tudelft.sem.template.voting.domain.election;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class CandidateAttributeConverter implements AttributeConverter<Set<Integer>, String> {

    @Override
    public String convertToDatabaseColumn(Set<Integer> attribute) {
        if (attribute.size() == 0) {
            return null;
        }
        return attribute.toString().substring(1, attribute.toString().length() - 1);
    }

    @Override
    public Set<Integer> convertToEntityAttribute(String dbData) {
        HashSet<Integer> candidates = new HashSet<>();
        if (dbData == null || dbData.isEmpty()) {
            return candidates;
        }

        String[] split = dbData.split(", ");
        for (String s : split) {
            candidates.add(Integer.parseInt(s));
        }
        return candidates;
    }
}
