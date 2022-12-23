package nl.tudelft.sem.template.association.domain.association;

import nl.tudelft.sem.template.association.domain.history.Event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class RulesAttributeConverter implements AttributeConverter<List<String>, String> {

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        String str = attribute.toString();
        str = str.substring(1, str.length() - 1);
        return str;
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        System.out.println(dbData);
        String[] strings = dbData.split(", ");
        List<String> res = new ArrayList<>();
        for (int i = 0; i < strings.length; i++) {
            System.out.println(strings[i]);
            res.add(strings[i]);
        }
        System.out.println(res);
        return res;
    }
}
