package nl.tudelft.sem.template.voting.domain.election;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import org.springframework.data.util.Pair;

@Converter
public class ElectionVotesAttributeConverter implements AttributeConverter<List<Pair<String, String>>, String> {
    @Override
    public String convertToDatabaseColumn(List<Pair<String, String>> attribute) {
        if (attribute.size() == 0) {
            return null;
        }
        List<String> candidates = new ArrayList<>();
        for (Pair<String, String> p : attribute) {
            candidates.add(p.getFirst());
            candidates.add(p.getSecond());
        }
        return candidates.toString().substring(1, candidates.toString().length() - 1);
    }

    @Override
    public List<Pair<String, String>> convertToEntityAttribute(String dbData) {
        List<Pair<String, String>> votes = new ArrayList<>();

        if (dbData == null || dbData.isEmpty()) {
            return votes;
        }

        List<String> values = new ArrayList<>();

        String[] split = dbData.split(", ");
        values.addAll(Arrays.asList(split));

        for (int i = 0; i < values.size(); i += 2) {
            votes.add(Pair.of(values.get(i), values.get(i + 1)));
        }
        return votes;
    }
}
