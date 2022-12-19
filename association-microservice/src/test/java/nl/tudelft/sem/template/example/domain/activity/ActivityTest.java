package nl.tudelft.sem.template.example.domain.activity;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;



class ActivityTest {
    public Activity activity;

    @BeforeEach
    void setUp() {
        String eventName = "Cool Event";
        String description = "This is the description of the event";
        Date startingDate = new Date();
        Date expirationDate = new Date();
        activity = new Activity(eventName, description, startingDate, expirationDate, 1, 21);
    }

    @Test
    void addInterested() {
        assertTrue(activity.getInterestedMembersId().isEmpty());
        activity.addParticipating(2);
        activity.addInterested(2);
        assertFalse(activity.getInterestedMembersId().isEmpty());
        assertTrue(activity.getParticipatingMembersId().isEmpty());
    }

    @Test
    void removeInterested() {
        activity.addInterested(2);
        activity.removeInterested(2);
        assertTrue(activity.getInterestedMembersId().isEmpty());
        activity.removeInterested(2);
        assertTrue(activity.getInterestedMembersId().isEmpty());
    }

    @Test
    void addGoingTo() {
        assertTrue(activity.getParticipatingMembersId().isEmpty());
        activity.addParticipating(2);
        assertFalse(activity.getParticipatingMembersId().isEmpty());

    }

    @Test
    void removeGoingTo() {
        activity.addParticipating(2);
        activity.removeParticipating(2);
        assertTrue(activity.getInterestedMembersId().isEmpty());

    }

    @Test
    void getGoingToMembersId() {
        List<Integer> result = new ArrayList<Integer>();
        result.add(1);
        assertEquals(activity.getParticipatingMembersId(), Collections.emptyList());
        activity.addParticipating(1);
        assertEquals(activity.getParticipatingMembersId(), result);
    }

    @Test
    void getInterestedMembersId() {
        List<Integer> result = new ArrayList<Integer>();
        result.add(1);
        assertEquals(activity.getInterestedMembersId(), Collections.emptyList());
        activity.addInterested(1);
        assertEquals(activity.getInterestedMembersId(), result);
    }
}