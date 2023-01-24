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
    @Column(name = "activity_id", nullable = false, unique = true)
    private int activityId;

    @Getter
    @Column(name = "activity_Details", nullable = false)
    @Convert(converter = ActivityDetailsConverter.class)
    private ActivityDetails activityDetails;

    @Getter
    @Column(name = "association_id", nullable = false)
    private int associationId;


    @Column(name = "publisher_id", nullable = false)
    private String publisherId;

    @Getter
    @Column(name = "participating_members_id", nullable = false)
    @Convert(converter = UserIdConverter.class)
    private Set<String> participatingUserId;

    @Getter
    @Column(name = "interested_members_id", nullable = false)
    @Convert(converter = UserIdConverter.class)
    private Set<String> interestedUserId;


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
                    Date expirationDate, int associationId, String publisherId) {
        this.activityDetails = new ActivityDetails(eventName, description, startingDate, expirationDate);
        this.associationId = associationId;
        this.publisherId = publisherId;
        this.participatingUserId =  new HashSet<String>();
        this.interestedUserId = new HashSet<String>();
    }


    /** adds id to the interested list.
     *
     * @param userId id of the interested member
     */
    public void addInterested(String userId) {

        removeParticipating(userId);
        interestedUserId.add(userId);

    }

    /** removes id from the interested list if it exists.
     *
     * @param userId id of the interested member
     */
    public void removeInterested(String userId) {
        interestedUserId.remove(userId);
    }

    /** adds id to the participating list.
     *
     * @param userId id of the goingTo member
     */
    public void addParticipating(String userId) {
        //removeInterested(userId);
        participatingUserId.add(userId);
    }

    /** removes id from the participating list if it exists.
     *
     * @param userId id of the goingTo member
     */
    public void removeParticipating(String userId) {
        participatingUserId.remove(userId);

    }

}
