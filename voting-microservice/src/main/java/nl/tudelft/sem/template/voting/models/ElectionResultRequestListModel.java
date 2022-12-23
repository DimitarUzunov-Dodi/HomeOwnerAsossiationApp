package nl.tudelft.sem.template.voting.models;

import java.util.List;
import lombok.Data;

@Data
public class ElectionResultRequestListModel {
    private List<RuleVoteResultRequestModel> requestModels;
}
