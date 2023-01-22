package nl.tudelft.sem.template.association.domain.association;

import static org.junit.jupiter.api.Assertions.*;

import nl.tudelft.sem.template.association.domain.location.Address;
import nl.tudelft.sem.template.association.domain.location.Location;
import nl.tudelft.sem.template.association.domain.membership.Membership;
import nl.tudelft.sem.template.association.domain.membership.MembershipRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class LeaveAssociationTest {
    @Autowired
    private transient AssociationService associationService;
    @Autowired
    private transient AssociationRepository mockAssociationRepository;
    @Autowired
    private transient MembershipRepository mockMembershipRepository;
    private Association association;
    private Membership membership;
    private String userId;

    /**
     * Initialize the councilMembers and userId variables before each test.
     */
    @BeforeEach
    public void setup() {
        userId = "a";
        association = new Association("test", new Location("test", "test"),
                "test", 10);
        association = mockAssociationRepository.save(association);
        association.addMember(userId);
        membership = new Membership(userId, association.getId(), new Address(
                new Location("test", "test"), "test", "test", "test"));
        membership = mockMembershipRepository.save(membership);
        association = mockAssociationRepository.save(association);

    }

    @Test
    public void leaveSuccessfulTest() {
        assertEquals(associationService.leaveAssociation(userId, association.getId()),
                "User " + userId + " left association " + association.getId());
    }

    @Test
    public void notaMemberLeaveTest() {
        assertThrows(IllegalArgumentException.class, () -> {
            associationService.leaveAssociation("b", association.getId());
        });
    }
}
