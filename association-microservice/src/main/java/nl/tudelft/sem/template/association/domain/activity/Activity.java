package nl.tudelft.sem.template.association.domain.activity;

import java.util.*;
import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Repository template of the activities class.
 */
@Entity
@Table(name = "activities")
@NoArgsConstructor
public class Activity {
    @Id
    @GeneratedValue
    @Getter
    @Column(name = "activity_id", nullable = false, unique = true)
    private int activityId;

    @Getter
    @Column(name = "event_name", nullable = false)
    private String eventName;

    @Getter
    @Column(name = "description", nullable = false)
    private String description;

    @Getter
    @Column(name = "starting_date", nullable = false)
    private Date startingDate;

    @Getter
    @Column(name = "expiration_date", nullable = false)
    private Date expirationDate;

    @Getter
    @Column(name = "association_id", nullable = false)
    private int associationId;

    @Getter
    @Column(name = "publisher_id", nullable = false)
    private int publisherId;

    @Getter
    @Column(name = "participating_members_id", nullable = false)
    @Convert(converter = UserIdConverter.class)
    private List<Integer> participatingUserId;

    @Getter
    @Column(name = "interested_members_id", nullable = false)
    @Convert(converter = UserIdConverter.class)
    private List<Integer> interestedUserId;


    /** Constructor for the activity class.
     *
     * @param eventName name of the event.
     * @param description description of the event
     * @param startingDate timestamp to start the event
     * @param expirationDate timestamp to end the event
     * @param associationId association id the activity belong to
     * @param publisherId id of the publisher of the event
     *
     */
    public Activity(String eventName, String description, Date startingDate,
                    Date expirationDate, int associationId, int publisherId) {
        this.eventName = eventName;
        this.description = description;
        this.startingDate = startingDate;
        this.expirationDate = expirationDate;
        this.associationId = associationId;
        this.publisherId = publisherId;
        this.participatingUserId =  new ArrayList<Integer>();
        this.interestedUserId = new ArrayList<Integer>();
    }

    /** adds id to the interested list.
     *
     * @param userId id of the interested member
     */
    public void addInterested(int userId) {
        if (interestedUserId.contains(userId)) {
            return;
        } else {
            removeParticipating(userId);
            interestedUserId.add(userId);
        }
    }

    /** removes id from the interested list if it exists.
     *
     * @param userId id of the interested member
     */
    public void removeInterested(int userId) {
        if (interestedUserId.contains(userId)) {
            interestedUserId.remove(interestedUserId.indexOf(userId));
        }
    }

    /** adds id to the participating list.
     *
     * @param userId id of the goingTo member
     */
    public void addParticipating(int userId) {
        if (participatingUserId.contains(userId)) {
            return;
        } else {
            removeInterested(userId);
            participatingUserId.add(userId);
        }
    }

    /** removes id from the participating list if it exists.
     *
     * @param userId id of the goingTo member
     */
    public void removeParticipating(int userId) {
        if (participatingUserId.contains(userId)) {
            participatingUserId.remove(participatingUserId.indexOf(userId));
        }
    }

}
