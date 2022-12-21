package nl.tudelft.sem.template.association.domain.association;

import java.util.Arrays;
import java.util.List;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class RulesAttributeConverter implements AttributeConverter<List<String>, String> {
    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        String rt = "";
        for (String r : attribute) {
            if (rt.equals("")) {
                rt = r;
            } else {
                rt = rt + "@@@" + r;
            }
        }
        return rt;
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        return Arrays.asList(dbData.split("@@@"));
    }
}
