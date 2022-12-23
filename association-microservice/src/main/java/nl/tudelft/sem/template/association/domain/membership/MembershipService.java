package nl.tudelft.sem.template.association.domain.membership;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import nl.tudelft.sem.template.association.domain.history.Notification;
import nl.tudelft.sem.template.association.models.RuleVoteResultRequestModel;
import org.springframework.stereotype.Service;

@Service
public class MembershipService {
    private final transient MembershipRepository membershipRepository;

    public MembershipService(MembershipRepository membershipRepository) {
        this.membershipRepository = membershipRepository;
    }

    /**
     * This method creates a message that contains all notifications and sets them to read.
     *
     * @param userId            The user for whom to dismiss.
     * @param associationId     The association the user is affiliated with.
     * @return                  The message containing the notifications that need to be displayed.
     */
    public String displayNotifications(String userId, int associationId) {
        Membership member = membershipRepository.findByUserIdAndAssociationId(userId, associationId).orElse(null);
        if (member == null) {
            throw new IllegalArgumentException("The member does not exist.");
        }

        List<Notification> notificationList = member.getNotifications();
        if (notificationList.isEmpty()) {
            return "There are no new notifications.";
        }

        StringBuilder res = new StringBuilder();
        for (Notification n : notificationList) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            res.append("On: ")
                    .append(sdf.format(n.getDate()))
                    .append(", ")
                    .append(n.getDescription())
                    .append(System.lineSeparator());
            n.setRead(true);
        }
        member.setNotifications(notificationList);
        membershipRepository.save(member);
        return res.toString();
    }

    /**
     * Dismisses read notifications.
     *
     * @param userId            The user for whom to dismiss.
     * @param associationId     The association the user is affiliated with.
     * @return                  The status after dismissing.
     */
    public String dismissNotifications(String userId, int associationId) {
        Membership member = membershipRepository.findByUserIdAndAssociationId(userId, associationId).orElse(null);
        if (member == null) {
            throw new IllegalArgumentException("The member does not exist.");
        }

        List<Notification> notificationList = member.getNotifications();
        if (notificationList.isEmpty()) {
            return "All notification have already been dismissed.";
        }

        notificationList.removeIf(Notification::isRead);
        member.setNotifications(notificationList);
        membershipRepository.save(member);
        return "All read notifications have been dismissed.";
    }

    /**
     * Notifies all members when a proposal has been accepted.
     *
     * @param associationId The association id where the proposal was accepted.
     * @param notification  The notification that has to be sent.
     * @return              A message indicating the status of notifying.
     */
    public String addNotification(int associationId, Notification notification) {
        List<Membership> members = membershipRepository.findAllByAssociationId(associationId);
        if (members == null || members.isEmpty()) {
            throw new IllegalArgumentException("The members do not exist.");
        }
        for (Membership m : members) {
            m.addNotification(notification);
            membershipRepository.save(m);
        }

        return " and all members have been notified.";
    }

    /**
     * Creates the description for the notification from vote results.
     *
     * @param request   The results of the vote.
     * @return          The status message for notifying all members.
     */
    public String createNotificationDescription(RuleVoteResultRequestModel request) {
        String desc;
        if (request.isAmendment()) {
            desc = "The rule: " + request.getRule() + System.lineSeparator()
                    + "was changed into: " + request.getAmendment() + ".";
        } else {
            desc = "The rule: " + request.getRule() + " passed.";
        }
        Notification notification = new Notification(desc, request.getDate());
        return addNotification(request.getAssociationId(), notification);
    }
}
