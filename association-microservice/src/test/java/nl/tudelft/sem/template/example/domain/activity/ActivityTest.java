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
        assertTrue(activity.getInterestedUserId().isEmpty());
        activity.addParticipating(2);
        activity.addInterested(2);
        assertFalse(activity.getInterestedUserId().isEmpty());
        assertTrue(activity.getParticipatingUserId().isEmpty());
    }

    @Test
    void removeInterested() {
        activity.addInterested(2);
        activity.removeInterested(2);
        assertTrue(activity.getInterestedUserId().isEmpty());
        activity.removeInterested(2);
        assertTrue(activity.getInterestedUserId().isEmpty());
    }

    @Test
    void addGoingTo() {
        assertTrue(activity.getParticipatingUserId().isEmpty());
        activity.addParticipating(2);
        assertFalse(activity.getParticipatingUserId().isEmpty());

    }

    @Test
    void removeGoingTo() {
        activity.addParticipating(2);
        activity.removeParticipating(2);
        assertTrue(activity.getInterestedUserId().isEmpty());

    }

    @Test
    void getGoingToMembersId() {
        List<Integer> result = new ArrayList<Integer>();
        result.add(1);
        assertEquals(activity.getParticipatingUserId(), Collections.emptyList());
        activity.addParticipating(1);
        assertEquals(activity.getParticipatingUserId(), result);
    }

    @Test
    void getInterestedMembersId() {
        List<Integer> result = new ArrayList<Integer>();
        result.add(1);
        assertEquals(activity.getInterestedUserId(), Collections.emptyList());
        activity.addInterested(1);
        assertEquals(activity.getInterestedUserId(), result);
    }
}