package nl.tudelft.sem.template.association.models;

import java.util.List;
import lombok.Data;

@Data
public class ElectionResultRequestListModel {
    private List<RuleVoteResultRequestModel> requestModels;
}
