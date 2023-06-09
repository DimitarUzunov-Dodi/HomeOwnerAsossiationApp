package nl.tudelft.sem.template.voting.models;

import java.util.Date;
import lombok.Data;

@Data
public class RuleVoteResultRequestModel {
    private boolean passed;
    private boolean isAnAmendment;
    private Integer associationId;
    private String type;
    private String rule;
    private String amendment;
    private String result;
    private Date date;

}
