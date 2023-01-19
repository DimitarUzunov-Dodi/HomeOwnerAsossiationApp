package nl.tudelft.sem.template.association.domain.location;

import javax.persistence.AttributeConverter;

public class AddressConverter implements AttributeConverter<Address, String> {
    private static int strLength = 5;

    @Override
    public String convertToDatabaseColumn(Address attribute) {
        String sp = "@@@";
        return attribute.getCountry() + sp + attribute.getCity() + sp + attribute.getHouseNumber() + sp
                + attribute.getStreet() + sp + attribute.getPostalCode();
    }

    @Override
    public Address convertToEntityAttribute(String dbData) {
        String[] str = dbData.split("@@@");
        if (str.length != strLength) {
            throw new IllegalArgumentException("Address format error");
        }
        Location location = new Location(str[0], str[1]);
        return new Address(location, str[2], str[3], str[4]);
    }
}
