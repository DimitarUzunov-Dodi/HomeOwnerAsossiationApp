package nl.tudelft.sem.template.example.domain;

import org.springframework.data.util.Pair;

import javax.persistence.AttributeConverter;
import java.util.ArrayList;
import java.util.List;

public class RuleVoteVotesAttributeConverter implements AttributeConverter<List<Pair<Integer, String>>, String> {
    @Override
    public String convertToDatabaseColumn(List<Pair<Integer, String>> attribute) {
        StringBuilder str = new StringBuilder();
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
        String[] split = dbData.split(", ");
        List<Pair<Integer, String>> votes = new ArrayList<>();
        for (int i = 0; i < split.length; i += 2) {
            votes.add(Pair.of(Integer.parseInt(split[i]), split[i + 1]));
        }
        return votes;
    }
}
