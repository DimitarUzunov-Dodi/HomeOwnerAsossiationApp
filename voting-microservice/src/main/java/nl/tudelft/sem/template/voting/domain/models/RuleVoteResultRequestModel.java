package nl.tudelft.sem.template.voting.domain.models;

import java.util.Date;
import lombok.Data;

@Data
public class RuleVoteResultRequestModel {
    private Date date;
    private String type;
    private boolean passed;
    private String result;
    private Integer associationId;
}
