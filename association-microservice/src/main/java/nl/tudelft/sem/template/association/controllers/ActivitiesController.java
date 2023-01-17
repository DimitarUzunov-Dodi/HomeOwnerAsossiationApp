package nl.tudelft.sem.template.association.controllers;

import java.util.Date;
import java.util.List;
import nl.tudelft.sem.template.association.authentication.AuthManager;
import nl.tudelft.sem.template.association.domain.activity.Activity;
import nl.tudelft.sem.template.association.domain.activity.ActivityService;
import nl.tudelft.sem.template.association.domain.association.AssociationService;
import nl.tudelft.sem.template.association.models.ActivityRequestModel;
import nl.tudelft.sem.template.association.models.AddActivityRequestModel;
import nl.tudelft.sem.template.association.models.AssociationRequestModel;
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
     * @param request The body containing the id of the association
     * @return a response from the server, list of activities if successful.
     */
    @GetMapping("/display-noticeboard")
    public ResponseEntity<?> displayNoticeBoard(@RequestBody AssociationRequestModel request) {
        try {
            associationService.getAssociationById(request.getAssociationId());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }

        List<Activity> noticeBoard = activityService.getNoticeBoard(request.getAssociationId());

        noticeBoard.stream().filter(x -> x.getEventDetails().getExpirationDate().compareTo(new Date()) > 0);
        return ResponseEntity.ok(noticeBoard);
    }

    /**
     * adds a new activity in the activity repository.
     *
     * @param activityRequest JSON body holding the rest of the parameter needed
     */
    @PostMapping("/add-activity")
    public ResponseEntity<?> addActivity(@RequestBody AddActivityRequestModel activityRequest) {
        try {
            boolean isMember = associationService.getAssociationById(activityRequest.getAssociationId())
                    .getMemberUserIds().contains(authManager.getUserId());

            if (isMember && activityRequest.isComplete()) {
                activityService.addActivity(activityRequest.getEventName(),
                        activityRequest.getDescription(),
                        activityRequest.getStartingDate(),
                        activityRequest.getExpirationDate(),
                        activityRequest.getAssociationId(), authManager.getUserId());

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
     * @param request The id of the activity
     * @return the corresponding activity
     */
    @GetMapping("/get-activity")
    public ResponseEntity<?> getActivity(@RequestBody ActivityRequestModel request) {
        Activity response = activityService.getActivity(request.getActivityId());
        if (response == null) {
            return new ResponseEntity<>("That activity does not exist.",
                    HttpStatus.BAD_REQUEST);
        } else {
            return ResponseEntity.ok(response);
        }

    }


    /**
     * Adds interested user to the list of the corresponding activity.
     *
     * @param request The request body containing the activity id and user id.
     * @return response from the server
     */
    @PostMapping("/add-interest")
    public ResponseEntity<?> addInterested(@RequestBody ActivityRequestModel request) {

        try {
            Activity activity = activityService.getActivity(request.getActivityId());
            if (activity != null) {

                int associationId = activity.getAssociationId();
                boolean isMember = associationService.getAssociationById(associationId).getMemberUserIds()
                        .contains(authManager.getUserId());

                if (isMember) {
                    activityService.addInterested(request.getActivityId(), authManager.getUserId());
                    return ResponseEntity.ok("Interested in activity " + activity.getEventDetails().getEventName());

                } else {
                    return new ResponseEntity<>("You have to be a member of the association to be interested in the event.",
                            HttpStatus.UNAUTHORIZED);
                }
            }
            return new ResponseEntity<>("That activity does not exits for you to be interested in it.",
                    HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            return new ResponseEntity<>("Association does not exist, you can't be interested.",
                    HttpStatus.BAD_REQUEST);
        }

    }

    /**
     * Adds participating user to the list of the corresponding activity.
     *
     * @param request The request body containing the activity id and user id.
     * @return response from the server
     */
    @PostMapping("/add-participating")
    public ResponseEntity<?> addParticipating(@RequestBody ActivityRequestModel request) {

        try {
            Activity activity = activityService.getActivity(request.getActivityId());
            if (activity != null) {

                int associationId = activity.getAssociationId();
                boolean isMember = associationService.getAssociationById(associationId).getMemberUserIds()
                        .contains(authManager.getUserId());

                if (isMember) {
                    activityService.addParticipating(request.getActivityId(), authManager.getUserId());
                    return ResponseEntity.ok("Participating in activity " + activity.getEventDetails().getEventName());

                } else {
                    return new ResponseEntity<>("You have to be a member of the association for participating in the event.",
                            HttpStatus.UNAUTHORIZED);
                }
            }
            return new ResponseEntity<>("That activity does not exits for you to be participating in it.",
                    HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Association does not exist, you can't be participating.",
                    HttpStatus.BAD_REQUEST);
        }
    }


    /**
     * Removes interested user from the list of the corresponding activity.
     *
     * @param request The request body containing the activity id and user id.
     * @return response from the server
     */
    @PostMapping("/remove-interested")
    public ResponseEntity<?> removeInterested(@RequestBody ActivityRequestModel request) {

        try {
            Activity activity = activityService.getActivity(request.getActivityId());
            if (activity != null) {

                int associationId = activity.getAssociationId();
                boolean isMember = associationService.getAssociationById(associationId).getMemberUserIds()
                        .contains(authManager.getUserId());

                if (isMember) {
                    activityService.removeInterested(request.getActivityId(), authManager.getUserId());
                    return ResponseEntity.ok("Not interested in activity " + activity.getEventDetails().getEventName());

                } else {
                    return new ResponseEntity<>("You have to be a member of this association,"
                            + " to be able to remove your interested reaction.",
                            HttpStatus.UNAUTHORIZED);
                }
            }
            return new ResponseEntity<>("That activity does not exits, for you to remove your interested reaction.",
                    HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            return new ResponseEntity<>("Association does not exist, you couldn't have been interested.",
                    HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Removes participating user ot the list of the corresponding activity.
     *
     * @param request The request body containing the activity id and user id.
     * @return response from the server
     */
    @PostMapping("/remove-participating")
    public ResponseEntity<?> removeParticipating(@RequestBody ActivityRequestModel request) {

        try {
            Activity activity = activityService.getActivity(request.getActivityId());
            if (activity != null) {

                int associationId = activity.getAssociationId();
                boolean isMember = associationService.getAssociationById(associationId).getMemberUserIds()
                        .contains(authManager.getUserId());

                if (isMember) {
                    activityService.removeParticipating(request.getActivityId(), authManager.getUserId());
                    return ResponseEntity.ok("Not participating in activity " + activity.getEventDetails().getEventName());

                } else {
                    return new ResponseEntity<>("You have to be a member of this association"
                            + " to be able to remove your interested reaction.",
                            HttpStatus.UNAUTHORIZED);
                }
            }
            return new ResponseEntity<>("That activity does not exits, for you to remove your participating reaction.",
                    HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            return new ResponseEntity<>("Association does not exist, you couldn't have been participating.",
                    HttpStatus.BAD_REQUEST);
        }
    }

}
