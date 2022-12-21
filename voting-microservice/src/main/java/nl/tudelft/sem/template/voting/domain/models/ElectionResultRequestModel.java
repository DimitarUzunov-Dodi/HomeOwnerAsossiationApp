package nl.tudelft.sem.template.voting.domain.models;

import java.util.Date;
import lombok.Data;

@Data
public class ElectionResultRequestModel {
    private Date date;
    private String result;
}
