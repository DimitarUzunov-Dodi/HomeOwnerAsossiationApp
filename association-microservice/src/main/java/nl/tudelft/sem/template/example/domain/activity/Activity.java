package nl.tudelft.sem.template.example.domain.activity;

import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="activities")
@NoArgsConstructor
public class Activity {
    @Id
    @GeneratedValue
    @Column(name = "activity_id", nullable = false, unique = true)
    private int activityId;

    @Column(name = "event_name", nullable = false)
    private String eventName;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "starting_date",nullable = false)
    private Date startingDate;

    @Column(name = "expiration_date",nullable = false)
    private Date expirationDate;

    @Column(name = "association_id",nullable = false)
    private int associationId;

    @Column(name = "publisher_id",nullable = false)
    private int publisherId;

    //add columns for interested and going
    @Column(name = "going_to_members_id", nullable = false)
    @Convert(converter = MemberIdConverter.class)
    private List<Integer> goingToMembersId;


    @Column(name = "interested_members_id", nullable = false)
    @Convert(converter = MemberIdConverter.class)
    private List<Integer> interestedMembersId;




    public Activity(String eventName, String description, Date startingDate, Date expirationDate, int associationId, int publisherId) {
        this.eventName = eventName;
        this.description = description;
        this.startingDate = startingDate;
        this.expirationDate = expirationDate;
        this.associationId = associationId;
        this.publisherId = publisherId;
        this.goingToMembersId =  Collections.emptyList();
        this.interestedMembersId = Collections.emptyList();
    }

    public void addInterested(int memberId){
        if(interestedMembersId.contains(memberId)){
            return;
        } else {
            removeGoingTo(memberId);
            interestedMembersId.add(memberId);
        }
    }

    public void removeInterested(int memberId){
        if(interestedMembersId.contains(memberId)){
            interestedMembersId.remove(interestedMembersId.indexOf(memberId));
        }
    }

    public void addGoingTo(int memberId){
        if(goingToMembersId.contains(memberId)){
            return;
        } else {
            removeInterested(memberId);
            goingToMembersId.add(memberId);
        }
    }

    public void removeGoingTo(int memberId){
        if(goingToMembersId.contains(memberId)){
            goingToMembersId.remove(goingToMembersId.indexOf(memberId));
        }
    }

}
