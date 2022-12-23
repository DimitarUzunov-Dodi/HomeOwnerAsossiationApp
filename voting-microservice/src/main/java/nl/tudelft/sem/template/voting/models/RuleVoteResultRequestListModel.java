package nl.tudelft.sem.template.voting.models;

import java.util.List;
import lombok.Data;

@Data
public class RuleVoteResultRequestListModel {
    private List<RuleVoteResultRequestModel> requestModels;
}
