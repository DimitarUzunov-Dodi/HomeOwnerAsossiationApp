package nl.tudelft.sem.template.association.domain.location;

import javax.persistence.AttributeConverter;

public class LocationConverter implements AttributeConverter<Location, String> {
    @Override
    public String convertToDatabaseColumn(Location attribute) {
        return null;
    }

    @Override
    public Location convertToEntityAttribute(String dbData) {
        return null;
    }
}
