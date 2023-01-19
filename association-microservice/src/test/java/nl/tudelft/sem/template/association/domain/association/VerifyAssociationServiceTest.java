package nl.tudelft.sem.template.association.domain.association;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    @Test
    public void createAssociationTest() {
        assertThat(associationService.createAssociation("name", "country", "city", "description", 5)).isNotNull();
    }

    @Test
    public void testGetAssociation() {
        assertThat(associationService.getAssociationById(association.getId()).getCity()).isEqualTo("test");
    }

    @Test
    public void testGetAssociationNotThere() {
        assertThatThrownBy(() -> associationService.getAssociationById(500)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testJoinAssociationNotExist() {
        assertThatThrownBy(() -> associationService.joinAssociation("someUser", 500, "test", "test", "test", "test", "test"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testJoinAssociationDifferentCity() {
        assertThatThrownBy(() -> associationService.joinAssociation("someUser", association.getId(),
                "test", "otherCity", "test", "test", "test"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testJoinAssociationDifferentCountry() {
        assertThatThrownBy(() -> associationService.joinAssociation("someUser", association.getId(),
                "otherCountry", "test", "test", "test", "test"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testLeaveAssociation() {
        associationService.joinAssociation("someUser", association.getId(),
                "test", "test", "test", "test", "test");

        assertThat(associationService.leaveAssociation("someUser", association.getId()))
                .isEqualTo("User someUser left association " + association.getId());
    }

    @Test
    public void testLeaveAssociationWrongAssociation() {
        associationService.joinAssociation("someUser", association.getId(),
                "test", "test", "test", "test", "test");

        assertThatThrownBy(() -> associationService.leaveAssociation("someUser", 500))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testLeaveAssociationWrongUser() {
        associationService.joinAssociation("someUser", association.getId(), "test", "test", "test", "test", "test");

        assertThatThrownBy(() -> associationService.leaveAssociation("asdfasdf", association.getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testUpdateCouncil() {
        associationService.joinAssociation("someUser", association.getId(), "test", "test", "test", "test", "test");

        associationService.updateCouncil(Set.of("someUser"), association.getId());
    }

    @Test
    public void testUpdateCouncilWrongAssociationId() {
        associationService.joinAssociation("someUser", association.getId(), "test", "test", "test", "test", "test");

        assertThatThrownBy(() -> associationService.updateCouncil(Set.of("someUser"), 500))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testUpdateCouncilLargerCouncil() {
        associationService.joinAssociation("someUser", association.getId(), "test", "test", "test", "test", "test");

        assertThatThrownBy(() -> associationService
                .updateCouncil(Set.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"), association.getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testUpdateCouncilIsNotMember() {
        assertThatThrownBy(() -> associationService.updateCouncil(Set.of("someUser"), association.getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void getCouncil() {
        assertThat(associationService.getCouncil(association.getId())).containsExactly("a", "b", "c");
    }

    @Test
    public void getCouncilWrondAssociationId() {
        assertThatThrownBy(() -> associationService.getCouncil(500)).isInstanceOf(IllegalArgumentException.class);
    }
}
