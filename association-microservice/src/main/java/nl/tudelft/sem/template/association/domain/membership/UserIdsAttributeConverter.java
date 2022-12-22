package nl.tudelft.sem.template.association.domain.membership;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class UserIdsAttributeConverter implements AttributeConverter<Set<String>, String> {

    @Override
    public String convertToDatabaseColumn(Set<String> attribute) {
        if (attribute.size() == 0) {
            return null;
        }
        return attribute.toString().substring(1, attribute.toString().length() - 1);
    }

    @Override
    public Set<String> convertToEntityAttribute(String dbData) {
        HashSet<String> userIds = new HashSet<>();
        if (dbData == null || dbData.isEmpty()) {
            return userIds;
        }

        String[] split = dbData.split(", ");
        for (String s : split) {
            userIds.add(s);
        }
        return userIds;
    }
}
