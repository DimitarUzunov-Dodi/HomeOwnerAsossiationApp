package nl.tudelft.sem.template.association.models;

import java.util.Date;
import lombok.Data;


/**
 * Model representing a request to add a new activity.
 */
@Data
public class AddActivityRequestModel {
    private Integer associationId;
    private String eventName;
    private String description;
    private Date startingDate;
    private Date expirationDate;

    public boolean isComplete() {
        return associationId != null && eventName != null && description != null
                && startingDate != null && expirationDate != null;
    }
}
