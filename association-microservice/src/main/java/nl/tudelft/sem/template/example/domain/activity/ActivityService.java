package nl.tudelft.sem.template.example.domain.activity;


import java.util.Date;
import java.util.List;
import java.util.Optional;
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
    public Optional<Activity> getActivity(int activityId) {
        return activityRepository.findByActivityId(activityId);
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

    public void addInterested(int activityId, int memberId) {
        activityRepository.findByActivityId(activityId).get().addInterested(memberId);
    }

    public void addGoingTo(int activityId, int memberId) {
        activityRepository.findByActivityId(activityId).get().addGoingTo(memberId);
    }

    public void removeInterested(int activityId, int memberId) {
        activityRepository.findByActivityId(activityId).get().removeInterested(memberId);
    }

    public void removeGoingTo(int activityId, int memberId) {
        activityRepository.findByActivityId(activityId).get().removeGoingTo(memberId);
    }






}
