package nl.tudelft.sem.template.example.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.AttributeConverter;
import org.springframework.data.util.Pair;

public class ElectionVotesAttributeConverter implements AttributeConverter<List<Pair<Integer, Integer>>, String> {
    @Override
    public String convertToDatabaseColumn(List<Pair<Integer, Integer>> attribute) {
        List<Integer> candidates = new ArrayList<>();
        for (Pair<Integer, Integer> p : attribute) {
            candidates.add(p.getFirst());
            candidates.add(p.getSecond());
        }
        return candidates.toString().substring(1, candidates.toString().length() - 1);
    }

    @Override
    public List<Pair<Integer, Integer>> convertToEntityAttribute(String dbData) {
        List<Integer> candidates = new ArrayList<>();

        String[] split = dbData.split(", ");
        for (String s : split) {
            candidates.add(Integer.parseInt(s));
        }
        List<Pair<Integer, Integer>> votes = new ArrayList<>();
        for (int i = 0; i < candidates.size(); i += 2) {
            votes.add(Pair.of(candidates.get(i), candidates.get(i + 1)));
        }
        return votes;
    }
}
