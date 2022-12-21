package nl.tudelft.sem.template.association.domain.activity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class ActivityServiceTest {
    public Activity activity;
    public Activity activity2;
    public List<Activity> activities;
    public ActivityRepository activityRepoMock;
    public ActivityService activityService;

    @BeforeEach
    void setUp() {
        String eventName = "Cool Event";
        String description = "This is the description of the event";
        Date startingDate = new Date();
        Date expirationDate = new Date();
        activity = new Activity(eventName, description, startingDate, expirationDate, 1, 21);
        activity2 = new Activity(eventName, description, startingDate, expirationDate, 1, 41);
        activities = new ArrayList<>();
        activities.add(activity);
        activities.add(activity2);
        activityRepoMock = mock(ActivityRepository.class);
        activityService = new ActivityService(activityRepoMock);
    }

    @Test
    void getActivityNoUser() throws NoSuchElementException {
        //Arrange
        when(activityRepoMock.findByActivityId(1)).thenReturn(Optional.empty());


        //Act
        Activity expected = null;
        assertThat(activityService.getActivity(1)).isEqualTo(expected);
    }

    @Test
    void getActivityUserFound() {
        //Arrange
        when(activityRepoMock.findByActivityId(0)).thenReturn(Optional.of(activity));

        //Act
        Activity expected = activity;
        assertThat(activityService.getActivity(0)).isEqualTo(expected);


    }

    @Test
    void getNoticeBoard() {
        //Arrange
        when(activityRepoMock.findAllByAssociationId(1)).thenReturn(activities);

        //Act
        List<Activity> expected = activities;
        assertThat(activityService.getNoticeBoard(1)).isEqualTo(expected);
    }



    @Test
    void addInterested() {
        //Arrange
        when(activityRepoMock.findByActivityId(0)).thenReturn(Optional.of(activity));

        //Act
        activityService.addInterested(0, 41);
        verify(activityRepoMock).findByActivityId(0);
    }

    @Test
    void addParticipating() {
        //Arrange
        when(activityRepoMock.findByActivityId(0)).thenReturn(Optional.of(activity));

        //Act
        activityService.addParticipating(0, 41);
        verify(activityRepoMock).findByActivityId(0);
    }

    @Test
    void removeInterested() {
        //Arrange
        when(activityRepoMock.findByActivityId(0)).thenReturn(Optional.of(activity));

        //Act
        activityService.removeInterested(0, 41);
        verify(activityRepoMock).findByActivityId(0);
    }

    @Test
    void removeParticipating() {
        //Arrange
        when(activityRepoMock.findByActivityId(0)).thenReturn(Optional.of(activity));

        //Act
        activityService.removeParticipating(0, 41);
        verify(activityRepoMock).findByActivityId(0);
    }
}