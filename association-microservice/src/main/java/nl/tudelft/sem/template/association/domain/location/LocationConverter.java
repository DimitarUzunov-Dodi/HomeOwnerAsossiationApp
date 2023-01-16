package nl.tudelft.sem.template.association.domain.location;

import javax.persistence.AttributeConverter;

public class LocationConverter implements AttributeConverter<Location, String> {
    @Override
    public String convertToDatabaseColumn(Location attribute) {
        return attribute.getCountry() + "@@@" + attribute.getCity();
    }

    @Override
    public Location convertToEntityAttribute(String dbData) throws IllegalArgumentException {
        String[] str = dbData.split("@@@");
        if (str.length != 2) {
            throw new IllegalArgumentException("Location format error");
        }
        return new Location(str[0], str[1]);
    }
}
