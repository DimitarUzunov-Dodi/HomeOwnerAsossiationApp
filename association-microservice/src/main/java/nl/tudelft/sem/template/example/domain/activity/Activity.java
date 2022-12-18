package nl.tudelft.sem.template.example.domain.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    @Column(name = "going_to_members_id", nullable = false)
    @Convert(converter = MemberIdConverter.class)
    private List<Integer> goingToMembersId;

    @Getter
    @Column(name = "interested_members_id", nullable = false)
    @Convert(converter = MemberIdConverter.class)
    private List<Integer> interestedMembersId;


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
        this.goingToMembersId =  new ArrayList<Integer>();
        this.interestedMembersId = new ArrayList<Integer>();
    }

    /** adds id to the interested list.
     *
     * @param memberId id of the interested member
     */
    public void addInterested(int memberId) {
        if (interestedMembersId.contains(memberId)) {
            return;
        } else {
            removeGoingTo(memberId);
            interestedMembersId.add(memberId);
        }
    }

    /** removes id from the interested list if it exists.
     *
     * @param memberId id of the interested member
     */
    public void removeInterested(int memberId) {
        if (interestedMembersId.contains(memberId)) {
            interestedMembersId.remove(interestedMembersId.indexOf(memberId));
        }
    }

    /** adds id to the goingTo list.
     *
     * @param memberId id of the goingTo member
     */
    public void addGoingTo(int memberId) {
        if (goingToMembersId.contains(memberId)) {
            return;
        } else {
            removeInterested(memberId);
            goingToMembersId.add(memberId);
        }
    }

    /** removes id from the goingTo list if it exists.
     *
     * @param memberId id of the goingTo member
     */
    public void removeGoingTo(int memberId) {
        if (goingToMembersId.contains(memberId)) {
            goingToMembersId.remove(goingToMembersId.indexOf(memberId));
        }
    }

}
