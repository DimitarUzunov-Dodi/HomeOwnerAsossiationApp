package nl.tudelft.sem.template.association.domain.activity;

import java.util.Date;
import lombok.Getter;

public class ActivityDetails {

    @Getter
    private transient String eventName;
    @Getter
    private transient String description;
    private transient Date startingDate;
    @Getter
    private transient Date expirationDate;


    /** Constructor for the Event Data.
     *
     * @param eventName name of the event
     * @param description  description of the event
     * @param startingDate starting date of the event
     * @param expirationDate ending time of the event
     */
    public ActivityDetails(String eventName, String description, Date startingDate, Date expirationDate) {
        this.eventName = eventName;
        this.description = description;
        this.startingDate = startingDate;
        this.expirationDate = expirationDate;
    }

    @Override
    public String toString() {
        return  eventName + ", " + description + ", " + startingDate + ", " + expirationDate;
    }
}
