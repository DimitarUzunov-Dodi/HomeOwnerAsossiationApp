package nl.tudelft.sem.template.association;

import nl.tudelft.sem.template.association.domain.association.Association;
import nl.tudelft.sem.template.association.domain.association.AssociationRepository;
import nl.tudelft.sem.template.association.domain.association.AssociationService;
import nl.tudelft.sem.template.association.domain.membership.Membership;
import nl.tudelft.sem.template.association.domain.membership.MembershipRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class VerifyCandidateTest {
    @Autowired
    private transient AssociationService associationService;
    @Autowired
    private transient AssociationRepository associationRepository;
    @Autowired
    private transient MembershipRepository membershipRepository;
    private Association association;
    private String userId;

    /**
     * Initialize the councilMembers and userId variables before each test.
     */
    @BeforeEach
    public void setup() {
        userId = "abc";
        association = new Association("test", "test", "test", "test", 10);
        association = associationRepository.save(association);
        associationService.joinAssociation(userId, association.getId(), "test", "test", "test",
                "test", "test");

    }

    @Test
    public void eligibleTest() {
        Optional<Membership> optionalMembership = membershipRepository
                .findByUserIdAndAssociationIdAndLeaveDate(userId, association.getId(), null);
        assert optionalMembership.isPresent();
        Membership membership = optionalMembership.get();

        Calendar c = Calendar.getInstance();
        c.setTime(new Date(System.currentTimeMillis()));
        c.add(Calendar.YEAR, -4);
        membership.setJoinDate(new Date(c.getTime().getTime()));
        membershipRepository.save(membership);

        assertTrue(associationService.verifyCandidate(userId, association.getId()));
    }

    @Test
    public void userNullTest() {
        assertFalse(associationService.verifyCandidate(null, association.getId()));
    }

    @Test
    public void associationNullTest() {
        assertFalse(associationService.verifyCandidate(userId, null));
    }

    @Test
    public void alreadyInCouncilTest() {
        Association association2 = new Association("test2", "test2", "test2", "test2",
                10);
        association2 = associationRepository.save(association2);

        associationService.joinAssociation(userId, association2.getId(), "test2", "test2", "test2",
                "test2", "test2");

        association2.setCouncilUserIds(new HashSet<>(List.of(userId)));
        associationRepository.save(association2);

        assertFalse(associationService.verifyCandidate(userId, association2.getId()));
    }

    @Test
    public void wrongAssociationIdTest() {
        assertFalse(associationService.verifyCandidate(userId, 999));
    }

    @Test
    public void wrongUserIdTest() {
        assertFalse(associationService.verifyCandidate("xxx", association.getId()));
    }

    @Test
    public void membershipTooShortTest() {
        assertFalse(associationService.verifyCandidate(userId, association.getId()));
    }



}