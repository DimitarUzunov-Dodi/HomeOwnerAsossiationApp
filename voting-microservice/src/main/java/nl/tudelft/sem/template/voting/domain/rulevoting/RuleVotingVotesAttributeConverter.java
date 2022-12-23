package nl.tudelft.sem.template.voting.domain.rulevoting;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import org.springframework.data.util.Pair;

@Converter
public class RuleVotingVotesAttributeConverter implements AttributeConverter<List<Pair<String, String>>, String> {
    @Override
    public String convertToDatabaseColumn(List<Pair<String, String>> attribute) {
        if (attribute.size() == 0) {
            return null;
        }

        StringBuilder str = new StringBuilder();

        for (Pair<String, String> p : attribute) {
            str.append(p.getFirst());
            str.append(",");
            str.append(p.getSecond());
            str.append(",");
        }
        return str.substring(0, str.length() - 1);
    }

    @Override
    public List<Pair<String, String>> convertToEntityAttribute(String dbData) {
        List<Pair<String, String>> votes = new ArrayList<>();
        if (dbData == null || dbData.isEmpty()) {
            return votes;
        }
        String[] split = dbData.split(",");
        for (int i = 0; i < split.length; i += 2) {
            votes.add(Pair.of(split[i], split[i + 1]));
        }
        return votes;
    }
}
