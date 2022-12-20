package nl.tudelft.sem.template.example.models;

import java.util.Date;
import lombok.Data;


/**
 * Model representing a request to add a new activity.
 */
@Data
public class ActivityRequest {
    private String eventName;
    private String description;
    private Date startingDate;
    private Date expirationDate;
}
