package nl.tudelft.sem.template.association.domain.location;

import javax.persistence.AttributeConverter;

public class AddressConverter implements AttributeConverter<Address, String> {
    @Override
    public String convertToDatabaseColumn(Address attribute) {
        return attribute.getCountry() + "@@@" + attribute.getCity() + "@@@" + attribute.getHouseNumber() + "@@@"
                + attribute.getStreet() + "@@@" + attribute.getPostalCode();
    }

    @Override
    public Address convertToEntityAttribute(String dbData) {
        String[] str = dbData.split("@@@");
        if (str.length != 5) {
            throw new IllegalArgumentException("Address format error");
        }
        Location location = new Location(str[0],str[1]);
        return new Address(location, str[2], str[3], str[4]);
    }
}
