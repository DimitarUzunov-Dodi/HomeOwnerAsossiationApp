package nl.tudelft.sem.template.example.domain.Membership;

import nl.tudelft.sem.template.example.domain.Membership.Address;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class AddressAttributeConverter implements AttributeConverter<Address,String> {
    @Override
    public String convertToDatabaseColumn(Address attribute) {
        return attribute.toString();
    }

    @Override
    public Address convertToEntityAttribute(String dbData) {
        return new Address(dbData);
    }
}
