package nl.tudelft.sem.template.association.domain.report;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import nl.tudelft.sem.template.association.domain.association.Association;
import nl.tudelft.sem.template.association.domain.association.AssociationRepository;
import nl.tudelft.sem.template.association.domain.location.Address;
import nl.tudelft.sem.template.association.domain.location.Location;
import nl.tudelft.sem.template.association.domain.membership.FieldNoNullException;
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
public class ReportServiceTest {
    @Autowired
    private transient ReportService reportService;
    @Autowired
    private transient ReportRepository mockReportRepository;
    @Autowired
    private transient AssociationRepository mockAssociationRepository;
    @Autowired
    private transient MembershipRepository mockMembershipRepository;
    private Association association;
    private List<String> rules;
    private Membership reporterMembership;
    private Membership violatorMembership;
    private String reporterId;
    private String violatorId;

    /**
     * Initialize the association, member and rules before each test.
     */
    @BeforeEach
    public void setup() {
        reporterId = "a";
        violatorId = "b";
        association = new Association("test", new Location("test", "test"),
                "test", 100);
        association = mockAssociationRepository.save(association);
        association.addMember(reporterId);
        association.addMember(violatorId);
        reporterMembership = new Membership(reporterId, association.getId(), new Address(
                new Location("test", "test"), "test", "test", "test"));
        reporterMembership = mockMembershipRepository.save(reporterMembership);
        violatorMembership = new Membership(violatorId, association.getId(), new Address(
                new Location("test", "test"), "test", "test", "test"));
        violatorMembership = mockMembershipRepository.save(violatorMembership);
        rules = new ArrayList<>();
        rules.add("test rule");
        association.setRules(rules);
        association = mockAssociationRepository.save(association);

    }

    @Test
    public void addReportSuccessfulTest() throws NoSuchRuleException, FieldNoNullException, ReportInconsistentException {
        reportService.addReport(association.getId(), reporterId, violatorId, "test rule");
        assertTrue(reportService.reportsInAssociation(association.getId()).size() == 1);
    }

    @Test
    public void nonexistantReportTest() {
        assertThrows(ReportInconsistentException.class, () -> {
            reportService.addReport(association.getId(), "c", "d", "test rule");
        });
    }
}