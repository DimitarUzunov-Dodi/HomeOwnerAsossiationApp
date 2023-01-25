package nl.tudelft.sem.template.association.domain.activity;


import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;



class ActivityTest {
    public Activity activity;
    private Date date = new Date();

    @BeforeEach
    void setUp() {
        String eventName = "Cool Event";
        String description = "This is the description of the event";
        Date startingDate = date;
        Date expirationDate = date;
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



    @Test
    void getAssociationId() {
        assertEquals(1, activity.getAssociationId());

    }

    @Test
    void addParticipating() {
        assertTrue(activity.getInterestedUserId().isEmpty());
        String  testUser1 = "TestUser1";
        String  testUser2 = "TestUser2";
        String  testUser3 = "TestUser3";

        activity.addParticipating(testUser3);

        activity.addInterested(testUser1);
        activity.addInterested(testUser2);
        activity.addInterested(testUser3);

        activity.addParticipating(testUser1);
        activity.addParticipating(testUser2);

        assertTrue(activity.getParticipatingUserId().contains(testUser1));
        assertTrue(activity.getParticipatingUserId().contains(testUser2));
        assertFalse(activity.getParticipatingUserId().contains(testUser3));
        assertFalse(activity.getInterestedUserId().contains(testUser1));
        assertFalse(activity.getInterestedUserId().contains(testUser2));
        assertTrue(activity.getInterestedUserId().contains(testUser3));


    }
}