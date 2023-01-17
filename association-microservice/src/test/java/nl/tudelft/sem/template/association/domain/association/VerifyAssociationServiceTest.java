package nl.tudelft.sem.template.association.domain.association;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.*;
import nl.tudelft.sem.template.association.domain.association.Association;
import nl.tudelft.sem.template.association.domain.association.AssociationRepository;
import nl.tudelft.sem.template.association.domain.association.AssociationService;
import nl.tudelft.sem.template.association.domain.location.Location;
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
public class VerifyAssociationServiceTest {
    @Autowired
    private transient AssociationService associationService;
    @Autowired
    private transient AssociationRepository mockAssociationRepository;
    private HashSet<String> councilMembers;
    private Association association;
    private String userId;

    /**
     * Initialize the councilMembers and userId variables before each test.
     */
    @BeforeEach
    public void setup() {
        this.councilMembers = new HashSet<>();
        this.councilMembers.add("a");
        this.councilMembers.add("b");
        this.councilMembers.add("c");

        this.userId = "d";
        this.association = new Association("test", new Location("test", "test"),
                "test", 10);
        this.association.setCouncilUserIds(this.councilMembers);
        mockAssociationRepository.save(this.association);
    }

    @Test
    public void verifyTrueTest() {
        this.userId = "a";
        assertTrue(associationService.verifyCouncilMember(this.userId, 1));
    }

    @Test
    public void verifyFalseTest() {
        assertFalse(associationService.verifyCouncilMember(this.userId, 1));
    }

    @Test
    public void verifyNullTest() {
        assertFalse(associationService.verifyCouncilMember(null, null));
    }

    @Test
    public void verifyUserNullTest() {
        assertFalse(associationService.verifyCouncilMember(null, 1));
    }

    @Test
    public void verifyCouncilNullTest() {
        assertFalse(associationService.verifyCouncilMember(this.userId, null));
    }
}
