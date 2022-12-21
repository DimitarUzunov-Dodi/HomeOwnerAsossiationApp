package nl.tudelft.sem.template.association.domain.activity;

import java.util.*;
import org.springframework.stereotype.Service;

/**  Service class for all activities functionalities.
 *
 */
@Service
public class ActivityService {
    private final transient ActivityRepository activityRepository;

    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    /** gets a specific activity from the repository.
     *
     * @param activityId the id of the activity we want to get
     * @return Activity correspondingly
     */
    public Activity getActivity(int activityId)  {
        try {
            return activityRepository.findByActivityId(activityId).get();
        } catch (NoSuchElementException e) {
            return null;
        }

    }

    public List<Activity> getNoticeBoard(int associationId) {
        return activityRepository.findAllByAssociationId(associationId);
    }

    /** adds a new activity to the repository.
     *
     * @param eventName name of the event
     * @param description description of the event
     * @param startingDate starting date of the event
     * @param expirationDate expiration date of the event
     *
     */
    public void addActivity(String eventName, String description, Date startingDate,
                            Date expirationDate, int associationId, int publisherId) {
        activityRepository.save(new Activity(eventName, description, startingDate,
                expirationDate, associationId, publisherId));
    }

    /** Adds interested user to the activity, if he is already participating he is removed from the participating list.
     *
     * @param activityId Id of the activity
     * @param userId id of the user
     */
    public void addInterested(int activityId, int userId) {
        Activity  activity = activityRepository.findByActivityId(activityId).get();
        activity.addInterested(userId);
        activityRepository.save(activity);
    }

    /** Adds participating user to the activity, if he is already interested he is removed from the interested list.
     *
     * @param activityId Id of the activity
     * @param userId id of the user
     */
    public void addParticipating(int activityId, int userId) {
        Activity  activity = activityRepository.findByActivityId(activityId).get();
        activity.addParticipating(userId);
        activityRepository.save(activity);
    }

    /** removes interested user from the activity if he exists in the database.
     *
     * @param activityId Id of the activity
     * @param userId id of the user
     */
    public void removeInterested(int activityId, int userId) {
        Activity  activity = activityRepository.findByActivityId(activityId).get();
        activity.removeInterested(userId);
        activityRepository.save(activity);
    }

    /** removes participating user from the activity if he exists in the database.
     *
     * @param activityId id of the activity
     * @param userId id of the user
     */
    public void removeParticipating(int activityId, int userId) {
        Activity  activity = activityRepository.findByActivityId(activityId).get();
        activity.removeParticipating(userId);
        activityRepository.save(activity);
    }






}
