package nl.tudelft.sem.template.authentication.domain.user;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * JPA Converter for the UserID value object.
 */
@Converter
public class UserIdAttributeConverter implements AttributeConverter<UserId, String> {

    @Override
    public String convertToDatabaseColumn(UserId attribute) {
        return attribute.toString();
    }

    @Override
    public UserId convertToEntityAttribute(String dbData) {
        try {
            return new UserId(dbData);
        } catch (InvalidFieldException e) {
            throw new RuntimeException(e);
        }
    }

}

