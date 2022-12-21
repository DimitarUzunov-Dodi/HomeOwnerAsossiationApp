package nl.tudelft.sem.template.association.controllers;

import java.util.List;
import nl.tudelft.sem.template.association.authentication.AuthManager;
import nl.tudelft.sem.template.association.domain.activity.Activity;
import nl.tudelft.sem.template.association.domain.activity.ActivityService;
import nl.tudelft.sem.template.association.models.ActivityRequestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/** Controller for the activities and functionality of the Notice Board.
 */
@Controller
@RequestMapping("/activities")
public class ActivitiesController {

    private final transient ActivityService activityService;

    private final transient AuthManager authManager;

    @Autowired
    public ActivitiesController(AuthManager authManager, ActivityService activityService) {
        this.authManager = authManager;
        this.activityService = activityService;
    }

    @GetMapping("/{associationId}/noticeboard")
    public ResponseEntity<List<Activity>> displayNoticeBoard(@PathVariable int associationId) {
        return ResponseEntity.ok(activityService.getNoticeBoard(associationId));
    }

    /** adds a new activity in the activity repository.
     *
     * @param associationId id of the association the activity is added to
     * @param publisherId id of the publishing member
     * @param activityRequest JSON body holding the rest of the parameter need ed
     */
    @PostMapping("/{associationId}/{publisherId}")
    public ResponseEntity<?> addActivity(@PathVariable int associationId, @PathVariable int publisherId,
                            @RequestBody ActivityRequestModel activityRequest) {
        activityService.addActivity(activityRequest.getEventName(),
                activityRequest.getDescription(),
                activityRequest.getStartingDate(),
                activityRequest.getExpirationDate(),
                associationId, publisherId);

        return  ResponseEntity.ok().build();
    }

    /** Gets an activity corresponding to its id.
     *
     * @param activityId id of the activity
     * @return the corresponding activity
     */
    @GetMapping("/noticeboard/{activityId}")
    public ResponseEntity<Activity> getActivity(@PathVariable int activityId) {
        Activity response = activityService.getActivity(activityId);
        if (response == null) {
            return (ResponseEntity<Activity>) ResponseEntity.notFound();
        } else {
            return ResponseEntity.ok(response);
        }

    }


    /** Adds interested user to the list of the corresponding activity.
     *
     * @param activityId id of the activity
     * @param userId id of the interested user
     * @return response from the server
     */
    @PostMapping("/addInterested/{activityId}/{userId}")
    public ResponseEntity<?> addInterested(@PathVariable int activityId, @PathVariable int userId) {
        activityService.addInterested(activityId, userId);

        return  ResponseEntity.ok().build();
    }

    /** Adds participating user to the list of the corresponding activity.
     *
     * @param activityId id of the activity
     * @param userId id of the participating user
     * @return response from the server
     */
    @PostMapping("/addParticipating/{activityId}/{userId}")
    public ResponseEntity<?> addParticipating(@PathVariable int activityId, @PathVariable int userId) {
        activityService.addParticipating(activityId, userId);

        return  ResponseEntity.ok().build();
    }

    /** Removes interested user from the list of the corresponding activity.
     *
     * @param activityId id of the activity
     * @param userId id of the user
     * @return response from the server
     */
    @PostMapping("/removeInterested/{activityId}/{userId}")
    public ResponseEntity<?> removeInterested(@PathVariable int activityId, @PathVariable int userId) {
        activityService.removeInterested(activityId, userId);

        return  ResponseEntity.ok().build();
    }

    /** Removes participating user ot the list of the corresponding activity.
     *
     * @param activityId id of the activity
     * @param userId id of the user
     * @return response from the server
     */
    @PostMapping("/removeParticipating/{activityId}/{userId}")
    public ResponseEntity<?> removeParticipating(@PathVariable int activityId, @PathVariable int userId) {
        activityService.removeParticipating(activityId, userId);

        return  ResponseEntity.ok().build();
    }



}
