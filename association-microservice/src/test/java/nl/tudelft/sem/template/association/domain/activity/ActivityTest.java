package nl.tudelft.sem.template.association.domain.activity;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
    void getActivityId() {
        assertEquals(0, activity.getActivityId());
    }

    @Test
    void getEventName() {
        assertEquals("Cool Event", activity.getEventName());

    }

    @Test
    void getDescription() {
        assertEquals("This is the description of the event", activity.getDescription());
    }

    @Test
    void getStartingDate() {
        assertThat(activity.getStartingDate()).isInstanceOf(Date.class);
        assertEquals(date, activity.getStartingDate());
    }

    @Test
    void getExpirationDate() {
        assertThat(activity.getExpirationDate()).isInstanceOf(Date.class);
        assertEquals(date, activity.getExpirationDate());
    }

    @Test
    void getAssociationId() {
        assertEquals(1, activity.getAssociationId());

    }

    @Test
    void getPublisherId() {
        assertEquals("Jo", activity.getPublisherId());
    }

    @Test
    void getParticipatingUserId() {
        Set set = new HashSet<String>();
        set.add("1user");
        set.add("2user");
        set.add("3user");
        activity.setParticipatingUserId(set);
        assertEquals(set, activity.getParticipatingUserId());
    }

    @Test
    void getInterestedUserId() {
        Set set = new HashSet<String>();
        set.add("1user");
        set.add("2user");
        set.add("3user");
        activity.setInterestedUserId(set);
        assertEquals(set, activity.getInterestedUserId());
    }
}