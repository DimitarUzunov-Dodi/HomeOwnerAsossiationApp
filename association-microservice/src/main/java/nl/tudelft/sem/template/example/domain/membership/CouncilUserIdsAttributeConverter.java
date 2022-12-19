package nl.tudelft.sem.template.example.domain.membership;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class CouncilUserIdsAttributeConverter implements AttributeConverter<Set<Integer>, String> {

    @Override
    public String convertToDatabaseColumn(Set<Integer> attribute) {
        if (attribute.size() == 0) {
            return null;
        }
        return attribute.toString().substring(1, attribute.toString().length() - 1);
    }

    @Override
    public Set<Integer> convertToEntityAttribute(String dbData) {
        HashSet<Integer> councilUserIds = new HashSet<>();
        if (dbData == null) {
            return councilUserIds;
        }

        String[] split = dbData.split(", ");
        for (String s : split) {
            councilUserIds.add(Integer.parseInt(s));
        }
        return councilUserIds;
    }
}
