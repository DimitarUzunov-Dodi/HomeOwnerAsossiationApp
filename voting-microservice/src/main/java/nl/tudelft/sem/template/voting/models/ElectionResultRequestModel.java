package nl.tudelft.sem.template.voting.models;

import java.util.Date;
import java.util.HashMap;
import lombok.Data;

@Data
public class ElectionResultRequestModel {
    private Date date;
    private String result;
    private HashMap<String, Integer> standings;
    private Integer associationId;
}
