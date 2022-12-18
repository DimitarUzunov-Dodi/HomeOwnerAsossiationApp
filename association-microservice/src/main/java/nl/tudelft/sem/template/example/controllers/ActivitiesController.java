package nl.tudelft.sem.template.example.controllers;

import java.util.List;
import nl.tudelft.sem.template.example.authentication.AuthManager;
import nl.tudelft.sem.template.example.domain.activity.Activity;
import nl.tudelft.sem.template.example.domain.activity.ActivityService;
import nl.tudelft.sem.template.example.models.ActivityRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** Controller for the activities and functionality of the Notice Board.
 */
public class ActivitiesController {

    private final transient ActivityService activityService;

    private final transient AuthManager authManager;

    @Autowired
    public ActivitiesController(AuthManager authManager, ActivityService activityService) {
        this.authManager = authManager;
        this.activityService = activityService;
    }

    @GetMapping("/{associationId}/noticeBoard")
    public ResponseEntity<List<Activity>> displayNoticeBoard(@PathVariable int associationId) {
        return ResponseEntity.ok(activityService.getNoticeBoard(associationId));
    }

    /** adds anew activity in the activity repository.
     *
     * @param associationId id of the association the activity is added to
     * @param publisherId id of the publishing member
     * @param activityRequest JSON body holding the rest of the parameter need ed
     */
    @PostMapping("/{publisherId/{associationId}}")
    public void addActivity(@PathVariable int associationId, @PathVariable int publisherId,
                            @RequestBody ActivityRequest activityRequest) {
        activityService.addActivity(activityRequest.getEventName(),
                activityRequest.getDescription(),
                activityRequest.getStartingDate(),
                activityRequest.getExpirationDate(),
                associationId, publisherId);
    }

    @PostMapping("/{memberId}/noticeBoard/{activityId}")
    public void addInterested(@PathVariable int activityId, @PathVariable int memberId) {
        activityService.addInterested(activityId, memberId);
    }

    @PostMapping("/{memberId}/noticeBoard/{activityId}")
    public void addGoingTo(@PathVariable int activityId, @PathVariable int memberId) {
        activityService.addGoingTo(activityId, memberId);
    }

    @PostMapping("/{memberId}/noticeBoard/{activityId}")
    public void removeInterested(@PathVariable int activityId, @PathVariable int memberId) {
        activityService.removeInterested(activityId, memberId);
    }

    @PostMapping("/{memberId}/noticeBoard/{activityId}")
    public void removeGoingTo(@PathVariable int activityId, @PathVariable int memberId) {
        activityService.removeGoingTo(activityId, memberId);
    }



}
