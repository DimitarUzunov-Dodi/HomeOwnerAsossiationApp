package nl.tudelft.sem.template.association.models;

import java.util.Date;
import lombok.Data;


/**
 * Model representing a request to add a new activity.
 */
@Data
public class ActivityRequestModel {
    private String eventName;
    private String description;
    private Date startingDate;
    private Date expirationDate;
}
