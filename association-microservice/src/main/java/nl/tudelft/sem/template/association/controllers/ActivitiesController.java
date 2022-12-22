package nl.tudelft.sem.template.association.controllers;

import java.util.Date;
import java.util.List;
import nl.tudelft.sem.template.association.authentication.AuthManager;
import nl.tudelft.sem.template.association.domain.activity.Activity;
import nl.tudelft.sem.template.association.domain.activity.ActivityService;
import nl.tudelft.sem.template.association.domain.association.AssociationService;
import nl.tudelft.sem.template.association.models.ActivityRequestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for the activities and functionality of the Notice Board.
 */
@Controller
@RequestMapping("/activities")
public class ActivitiesController {

    private final transient ActivityService activityService;

    private final transient AuthManager authManager;

    private final transient AssociationService associationService;

    /**
     * Constructor for or controller.
     *
     * @param authManager        AuthManager
     * @param activityService    ActivityService
     * @param associationService AssociationService
     */
    @Autowired
    public ActivitiesController(AuthManager authManager, ActivityService activityService,
                                AssociationService associationService) {
        this.authManager = authManager;
        this.activityService = activityService;
        this.associationService = associationService;
    }

    /**
     * Gets all the activities of a certain association, without the ones who have expired.
     *
     * @param associationId id of the association
     * @return a response from the server, list of activities if successful.
     */
    @GetMapping("/{associationId}/noticeboard")
    public ResponseEntity<?> displayNoticeBoard(@PathVariable int associationId) {
        try {
            associationService.getAssociationById(associationId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }

        List<Activity> noticeBoard = activityService.getNoticeBoard(associationId);

        noticeBoard.stream().filter(x -> x.getExpirationDate().compareTo(new Date()) > 0);
        return ResponseEntity.ok(noticeBoard);
    }

    /**
     * adds a new activity in the activity repository.
     *
     * @param associationId   id of the association the activity is added to
     * @param publisherId     id of the publishing member
     * @param activityRequest JSON body holding the rest of the parameter need ed
     */
    @PostMapping("/{associationId}/{publisherId}")
    public ResponseEntity<?> addActivity(@PathVariable int associationId, @PathVariable String publisherId,
                                         @RequestBody ActivityRequestModel activityRequest) {
        try {
            boolean isMember = associationService.getAssociationById(associationId).getMemberUserIds().contains(publisherId);

            if (isMember && activityRequest.isComplete()) {
                activityService.addActivity(activityRequest.getEventName(),
                        activityRequest.getDescription(),
                        activityRequest.getStartingDate(),
                        activityRequest.getExpirationDate(),
                        associationId, publisherId);

                return ResponseEntity.ok("Activity added");

            } else {
                if (!isMember) {
                    return new ResponseEntity<>("You are not a member of this association.",
                            HttpStatus.UNAUTHORIZED);
                }
                return new ResponseEntity<>("Request not full", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Association does not exist.",
                    HttpStatus.BAD_REQUEST);
        }

    }


    /**
     * Gets an activity corresponding to its id.
     *
     * @param activityId id of the activity
     * @return the corresponding activity
     */
    @GetMapping("/noticeboard/{activityId}")
    public ResponseEntity<?> getActivity(@PathVariable int activityId) {
        Activity response = activityService.getActivity(activityId);
        if (response == null) {
            return new ResponseEntity<>("That activity does not exits.",
                    HttpStatus.BAD_REQUEST);
        } else {
            return ResponseEntity.ok(response);
        }

    }


    /**
     * Adds interested user to the list of the corresponding activity.
     *
     * @param activityId id of the activity
     * @param userId     id of the interested user
     * @return response from the server
     */
    @PostMapping("/addInterested/{activityId}/{userId}")
    public ResponseEntity<?> addInterested(@PathVariable int activityId, @PathVariable String userId) {

        try {
            Activity activity = activityService.getActivity(activityId);
            if (activity != null) {

                int associationId = activity.getAssociationId();
                boolean isMember = associationService.getAssociationById(associationId).getMemberUserIds().contains(userId);

                if (isMember) {
                    activityService.addInterested(activityId, userId);
                    return ResponseEntity.ok().build();

                } else {
                    return new ResponseEntity<>("You have to be a member of the association to be interested in the event.",
                            HttpStatus.UNAUTHORIZED);
                }
            }
            return new ResponseEntity<>("That activity does not exits for you to be interested in it.",
                    HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            return new ResponseEntity<>("Association does not exist.",
                    HttpStatus.BAD_REQUEST);
        }

    }

    /**
     * Adds participating user to the list of the corresponding activity.
     *
     * @param activityId id of the activity
     * @param userId     id of the participating user
     * @return response from the server
     */
    @PostMapping("/addParticipating/{activityId}/{userId}")
    public ResponseEntity<?> addParticipating(@PathVariable int activityId, @PathVariable String userId) {

        try {
            Activity activity = activityService.getActivity(activityId);
            if (activity != null) {

                int associationId = activity.getAssociationId();
                boolean isMember = associationService.getAssociationById(associationId).getMemberUserIds().contains(userId);

                if (isMember) {
                    activityService.addParticipating(activityId, userId);
                    return ResponseEntity.ok().build();

                } else {
                    return new ResponseEntity<>("You have to be a member of the association for participating in the event.",
                            HttpStatus.UNAUTHORIZED);
                }
            }
            return new ResponseEntity<>("That activity does not exits for you to be participating in it.",
                    HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Association does not exist.",
                    HttpStatus.BAD_REQUEST);
        }
    }


    /**
     * Removes interested user from the list of the corresponding activity.
     *
     * @param activityId id of the activity
     * @param userId     id of the user
     * @return response from the server
     */
    @PostMapping("/removeInterested/{activityId}/{userId}")
    public ResponseEntity<?> removeInterested(@PathVariable int activityId, @PathVariable String userId) {

        try {
            Activity activity = activityService.getActivity(activityId);
            if (activity != null) {

                int associationId = activity.getAssociationId();
                boolean isMember = associationService.getAssociationById(associationId).getMemberUserIds().contains(userId);

                if (isMember) {
                    activityService.removeInterested(activityId, userId);
                    return ResponseEntity.ok().build();

                } else {
                    return new ResponseEntity<>("You have to be a member of this association,"
                            + " to be able to remove your interested reaction.",
                            HttpStatus.UNAUTHORIZED);
                }
            }
            return new ResponseEntity<>("That activity does not exits, for you to remove your interested reaction.",
                    HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            return new ResponseEntity<>("Association does not exist.",
                    HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Removes participating user ot the list of the corresponding activity.
     *
     * @param activityId id of the activity
     * @param userId     id of the user
     * @return response from the server
     */
    @PostMapping("/removeParticipating/{activityId}/{userId}")
    public ResponseEntity<?> removeParticipating(@PathVariable int activityId, @PathVariable String userId) {

        try {
            Activity activity = activityService.getActivity(activityId);
            if (activity != null) {

                int associationId = activity.getAssociationId();
                boolean isMember = associationService.getAssociationById(associationId).getMemberUserIds().contains(userId);

                if (isMember) {
                    activityService.removeParticipating(activityId, userId);
                    return ResponseEntity.ok().build();

                } else {
                    return new ResponseEntity<>("You have to be a member of this association"
                            + " to be able to remove your interested reaction.",
                            HttpStatus.UNAUTHORIZED);
                }
            }
            return new ResponseEntity<>("That activity does not exits, for you to remove your participating reaction.",
                    HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            return new ResponseEntity<>("Association does not exist.",
                    HttpStatus.BAD_REQUEST);
        }
    }

}
