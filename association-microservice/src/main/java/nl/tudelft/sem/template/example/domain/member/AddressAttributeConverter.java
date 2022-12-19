package nl.tudelft.sem.template.example.domain.member;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class AddressAttributeConverter implements AttributeConverter<Address, String> {
    @Override
    public String convertToDatabaseColumn(Address attribute) {
        return attribute.toString();
    }

    @Override
    public Address convertToEntityAttribute(String dbData) {
        return new Address(dbData);
    }
}
