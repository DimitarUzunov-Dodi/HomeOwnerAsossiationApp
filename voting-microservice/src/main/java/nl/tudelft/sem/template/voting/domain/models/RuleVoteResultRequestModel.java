package nl.tudelft.sem.template.voting.domain.models;

import java.util.Date;
import lombok.Data;

@Data
public class RuleVoteResultRequestModel {
    private Date date;
    private String result;
}
