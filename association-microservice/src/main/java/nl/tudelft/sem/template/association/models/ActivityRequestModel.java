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

    public boolean isComplete() {
        return eventName != null && description != null && startingDate != null && expirationDate != null;
    }

    /**
     * Constructor for testing purposes.
     *
     * @param eventName      name of the event
     * @param description    description of the event
     * @param startingDate   date from which the event starts
     * @param expirationDate date on which the event ends
     */
    public ActivityRequestModel(String eventName, String description, Date startingDate, Date expirationDate) {
        this.eventName = eventName;
        this.description = description;
        this.startingDate = startingDate;
        this.expirationDate = expirationDate;
    }
}
