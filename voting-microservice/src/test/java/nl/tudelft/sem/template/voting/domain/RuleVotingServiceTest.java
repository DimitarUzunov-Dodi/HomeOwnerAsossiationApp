package nl.tudelft.sem.template.voting.domain;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
public class RuleVotingServiceTest {
    @Autowired
    private transient RuleVotingService ruleVotingService;
    private List<Integer> councilMembers;
    private int userId;

    /**
     * Initialize the councilMembers and userId variables before each test.
     */
    @BeforeEach
    public void setup() {
        this.councilMembers = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            this.councilMembers.add(i);
        }
        Collections.shuffle(this.councilMembers);
        this.userId = 10;

    }

    @Test
    public void verifyTrueTest() {
        this.councilMembers.add(userId);
        assertTrue(ruleVotingService.verify(this.userId, councilMembers));
    }

    @Test
    public void verifyFalseTest() {
        assertFalse(ruleVotingService.verify(this.userId, councilMembers));
    }

    @Test
    public void verifyNullTest() {
        assertFalse(ruleVotingService.verify(null, null));
    }

    @Test
    public void verifyUserNullTest() {
        assertFalse(ruleVotingService.verify(null, councilMembers));
    }

    @Test
    public void verifyCouncilNullTest() {
        assertFalse(ruleVotingService.verify(this.userId, null));
    }
}
