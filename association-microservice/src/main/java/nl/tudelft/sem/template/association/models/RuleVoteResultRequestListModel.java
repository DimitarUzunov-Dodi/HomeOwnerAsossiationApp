package nl.tudelft.sem.template.association.models;

import java.util.List;
import lombok.Data;

@Data
public class RuleVoteResultRequestListModel {
    private List<RuleVoteResultRequestModel> requestModels;
}
