package nl.tudelft.sem.template.association.domain.report;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import nl.tudelft.sem.template.association.domain.association.Association;
import nl.tudelft.sem.template.association.domain.association.AssociationRepository;
import nl.tudelft.sem.template.association.domain.location.Address;
import nl.tudelft.sem.template.association.domain.location.Location;
import nl.tudelft.sem.template.association.domain.membership.Membership;
import nl.tudelft.sem.template.association.domain.membership.MembershipRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;



@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ReportServiceTest {

    @Autowired
    private transient ReportService reportService;

    @Autowired
    private transient ReportRepository reportRepository;

    @Autowired
    public transient MembershipRepository membershipRepository;

    @Autowired
    public transient AssociationRepository associationRepository;

    private HashSet<String> councilMembers;
    private Association association;
    private String userId;

    @BeforeEach
    public void setUp() {
        this.councilMembers = new HashSet<>();
        this.councilMembers.add("a");
        this.councilMembers.add("b");
        this.councilMembers.add("c");

        this.userId = "d";
        this.association = new Association("name", new Location("country", "city"), "test", 10);
        this.association.setCouncilUserIds(this.councilMembers);
        //this.association

        associationRepository.save(this.association);

        Membership membership1 = new Membership("a", association.getId(),
                new Address(new Location("country", "city"), "street", "number", "postalCode"));
        Membership membership2 = new Membership("b", association.getId(),
                new Address(new Location("country", "city"), "street", "number", "postalCode"));
        Membership membership3 = new Membership("c", association.getId(),
                new Address(new Location("country", "city"), "street", "number", "postalCode"));
        Membership membership4 = new Membership("d", association.getId(),
                new Address(new Location("country", "city"), "street", "number", "postalCode"));

        membershipRepository.save(membership1);
        membershipRepository.save(membership2);
        membershipRepository.save(membership3);
        membershipRepository.save(membership4);
    }
}