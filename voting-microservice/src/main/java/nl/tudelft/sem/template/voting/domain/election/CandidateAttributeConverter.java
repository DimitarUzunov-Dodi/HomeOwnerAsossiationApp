package nl.tudelft.sem.template.voting.domain.election;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class CandidateAttributeConverter implements AttributeConverter<Set<String>, String> {

    @Override
    public String convertToDatabaseColumn(Set<String> attribute) {
        if (attribute.size() == 0) {
            return null;
        }
        return attribute.toString().substring(1, attribute.toString().length() - 1);
    }

    @Override
    public Set<String> convertToEntityAttribute(String dbData) {
        HashSet<String> candidates = new HashSet<>();
        if (dbData == null || dbData.isEmpty()) {
            return candidates;
        }

        String[] split = dbData.split(", ");
        candidates.addAll(Arrays.asList(split));
        return candidates;
    }
}
