package nl.tudelft.sem.template.association.domain.activity;

import java.util.Date;
import lombok.Getter;

public class EventDetails {

    @Getter
    private String eventName;
    private String description;
    private Date startingDate;
    @Getter
    private Date expirationDate;


    /** Constructor for the Event Data.
     *
     * @param eventName name of the event
     * @param description  description of the event
     * @param startingDate starting date of the event
     * @param expirationDate ending time of the event
     */
    public EventDetails(String eventName, String description, Date startingDate, Date expirationDate) {
        this.eventName = eventName;
        this.description = description;
        this.startingDate = startingDate;
        this.expirationDate = expirationDate;
    }


}
