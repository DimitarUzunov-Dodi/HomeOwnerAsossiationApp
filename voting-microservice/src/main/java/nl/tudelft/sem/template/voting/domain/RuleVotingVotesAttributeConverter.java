package nl.tudelft.sem.template.voting.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.AttributeConverter;
import org.springframework.data.util.Pair;

public class RuleVotingVotesAttributeConverter implements AttributeConverter<List<Pair<Integer, String>>, String> {
    @Override
    public String convertToDatabaseColumn(List<Pair<Integer, String>> attribute) {
        StringBuilder str = new StringBuilder();
        if (attribute.size() == 0) return null;
        for (Pair<Integer, String> p : attribute) {
            str.append(p.getFirst());
            str.append(",");
            str.append(p.getSecond());
            str.append(",");
        }
        return str.substring(0, str.length() - 1);
    }

    @Override
    public List<Pair<Integer, String>> convertToEntityAttribute(String dbData) {
        List<Pair<Integer, String>> votes = new ArrayList<>();
        if (dbData == null) return votes;
        String[] split = dbData.split(",");
        for (int i = 0; i < split.length; i += 2) {
            votes.add(Pair.of(Integer.parseInt(split[i]), split[i + 1]));
        }
        return votes;
    }
}
