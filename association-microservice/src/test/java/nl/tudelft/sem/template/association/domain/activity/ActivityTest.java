package nl.tudelft.sem.template.association.domain.activity;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

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
        activity = new Activity(eventName, description, startingDate, expirationDate, 1, "Jo");
    }

    @Test
    void addInterested() {
        assertTrue(activity.getInterestedUserId().isEmpty());
        activity.addParticipating("Jo");
        activity.addInterested("Jo");
        assertFalse(activity.getInterestedUserId().isEmpty());
        assertTrue(activity.getParticipatingUserId().isEmpty());
    }

    @Test
    void removeInterested() {
        activity.addInterested("Jo");
        activity.removeInterested("Jo");
        assertTrue(activity.getInterestedUserId().isEmpty());
        activity.removeInterested("Jo");
        assertTrue(activity.getInterestedUserId().isEmpty());
    }

    @Test
    void addGoingTo() {
        assertTrue(activity.getParticipatingUserId().isEmpty());
        activity.addParticipating("Jo");
        assertFalse(activity.getParticipatingUserId().isEmpty());

    }

    @Test
    void removeGoingTo() {
        activity.addParticipating("Jo");
        activity.removeParticipating("Jo");
        assertTrue(activity.getInterestedUserId().isEmpty());

    }

    @Test
    void getGoingToMembersId() {
        Set<String> result = new HashSet<>();
        result.add("Bob");
        assertEquals(activity.getParticipatingUserId(), Collections.emptySet());
        activity.addParticipating("Bob");
        assertEquals(activity.getParticipatingUserId(), result);
    }

    @Test
    void getInterestedMembersId() {
        Set<String> result = new HashSet<>();
        result.add("Bob");
        assertEquals(activity.getInterestedUserId(), Collections.emptySet());
        activity.addInterested("Bob");
        assertEquals(activity.getInterestedUserId(), result);
    }
}