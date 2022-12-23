package nl.tudelft.sem.template.association.domain.membership;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import nl.tudelft.sem.template.association.domain.history.Notification;
import nl.tudelft.sem.template.association.models.RuleVoteResultRequestModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles({"test"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class MembershipTests {
    @Autowired
    private transient MembershipService membershipService;
    @Autowired
    private transient MembershipRepository membershipRepository;
    private Notification notification1;
    private Notification notification2;
    private Date date;
    private Membership membership;
    private Membership membership1;

    /**
     * Create two new members and add two notifications to their list of notifications.
     */
    @BeforeEach
    public void setup() {
        this.membership = new Membership("user", 2, "test", "test", "test", "test", "test");
        this.membership1 = new Membership("user2", 2, "test", "test", "test", "test", "test");

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -5);
        String amendmentDesc = "The rule: \"Bleep\"" + System.lineSeparator()
                + "was changed into: \"Bloop\".";
        String proposalDesc = "The rule: \"Bleep\" passed.";
        this.date = cal.getTime();
        this.notification1 = new Notification(amendmentDesc, this.date);
        this.notification2 = new Notification(proposalDesc, this.date);
        this.membership.addNotification(this.notification1);
        this.membership.addNotification(this.notification2);
        membershipRepository.save(this.membership);
        this.notification1.setRead(true);
        this.notification2.setRead(true);
        this.membership1.addNotification(this.notification1);
        this.membership1.addNotification(this.notification2);
        membershipRepository.save(this.membership1);
    }

    @Test
    public void displayNotifications() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        assertThat(membershipService
                .displayNotifications(this.membership.getUserId(), this.membership.getAssociationId()))
                .isEqualTo("On: " + sdf.format(this.date) + ", The rule: \"Bleep\"" + System.lineSeparator()
                        + "was changed into: \"Bloop\"." + System.lineSeparator() + "On: " + sdf.format(this.date)
                        + ", The rule: \"Bleep\" passed." + System.lineSeparator());
    }

    @Test
    public void displayMemberDoesNotExist() {
        assertThatThrownBy(() -> {
            membershipService.displayNotifications("test", 2);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void displayNoNewNotifications() {
        Membership membershipNone = new Membership("test", 2, "test", "test", "test", "test", "test");
        membershipRepository.save(membershipNone);
        assertThat(membershipService
                .displayNotifications(membershipNone.getUserId(), membershipNone.getAssociationId()))
                .isEqualTo("There are no new notifications.");
    }

    @Test
    public void dismissNotifications() {
        assertThat(this.membership1.getNotifications().stream().allMatch(Notification::isRead)).isTrue();

        membershipService.dismissNotifications(this.membership1.getUserId(), this.membership1.getAssociationId());
        Membership member = membershipRepository.findByUserIdAndAssociationId(
                this.membership1.getUserId(), this.membership1.getAssociationId()).orElse(null);

        assertThat(member.getNotifications().isEmpty()).isTrue();
    }

    @Test
    public void dismissMemberDoesNotExist() {
        assertThatThrownBy(() -> {
            membershipService.dismissNotifications("test", 2);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void dismissNoNewNotifications() {
        Membership membershipNone = new Membership("test", 2, "test", "test", "test", "test", "test");
        membershipRepository.save(membershipNone);
        assertThat(membershipService
                .dismissNotifications(membershipNone.getUserId(), membershipNone.getAssociationId()))
                .isEqualTo("All notification have already been dismissed.");
    }

    @Test
    public void addNotification() {
        String desc = "The rule: \"Blebopp\"" + System.lineSeparator()
                + "was changed into: \"Blooooop\".";
        Notification newNotification = new Notification(desc, this.date);
        assertThat(membershipService.addNotification(this.membership.getAssociationId(), newNotification))
                .isEqualTo(" and all members have been notified.");
        Membership member = membershipRepository.findByUserIdAndAssociationId(
                this.membership1.getUserId(), this.membership1.getAssociationId()).orElse(null);
        assertThat(member.getNotifications().size()).isEqualTo(3);
        member = membershipRepository.findByUserIdAndAssociationId(
                this.membership.getUserId(), this.membership.getAssociationId()).orElse(null);
        assertThat(member.getNotifications().size()).isEqualTo(3);
    }

    @Test
    public void addMemberDoesNotExist() {
        String desc = "The rule: \"Blebopp\"" + System.lineSeparator()
                + "was changed into: \"Blooooop\".";
        Notification newNotification = new Notification(desc, this.date);
        assertThatThrownBy(() -> {
            membershipService.addNotification(4, newNotification);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void createAmendmentDesc() {
        RuleVoteResultRequestModel model = new RuleVoteResultRequestModel();
        model.setProposal(true);
        model.setRule("Bleep");
        model.setAmendment("Bloop");
        model.setAssociationId(2);
        model.setDate(this.date);

        assertThat(membershipService.createNotificationDescription(model))
                .isEqualTo(" and all members have been notified.");
    }

}
