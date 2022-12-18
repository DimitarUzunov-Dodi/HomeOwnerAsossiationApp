package nl.tudelft.sem.template.voting.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import org.springframework.data.util.Pair;

@Converter
public class ElectionVotesAttributeConverter implements AttributeConverter<List<Pair<Integer, Integer>>, String> {
    @Override
    public String convertToDatabaseColumn(List<Pair<Integer, Integer>> attribute) {
        if (attribute.size() == 0) {
            return null;
        }
        List<Integer> candidates = new ArrayList<>();
        for (Pair<Integer, Integer> p : attribute) {
            candidates.add(p.getFirst());
            candidates.add(p.getSecond());
        }
        return candidates.toString().substring(1, candidates.toString().length() - 1);
    }

    @Override
    public List<Pair<Integer, Integer>> convertToEntityAttribute(String dbData) {
        List<Pair<Integer, Integer>> votes = new ArrayList<>();

        if (dbData == null) {
            return votes;
        }

        List<Integer> values = new ArrayList<>();

        String[] split = dbData.split(", ");
        for (String s : split) {
            values.add(Integer.parseInt(s));
        }

        for (int i = 0; i < values.size(); i += 2) {
            votes.add(Pair.of(values.get(i), values.get(i + 1)));
        }
        return votes;
    }
}
