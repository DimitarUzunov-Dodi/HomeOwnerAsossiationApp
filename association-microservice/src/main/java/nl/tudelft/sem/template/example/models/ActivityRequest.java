package nl.tudelft.sem.template.example.models;

import lombok.Data;

import java.util.Date;

/**
 * Model representing a request to add a new activity
 */
@Data
public class ActivityRequest {
    private String eventName;
    private String description;
    private Date startingDate;
    private Date expirationDate;
}
