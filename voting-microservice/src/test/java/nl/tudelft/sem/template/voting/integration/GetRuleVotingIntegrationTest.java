package nl.tudelft.sem.template.voting.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.*;
import nl.tudelft.sem.template.voting.authentication.JwtTokenVerifier;
import nl.tudelft.sem.template.voting.domain.rulevoting.RuleVoting;
import nl.tudelft.sem.template.voting.domain.rulevoting.RuleVotingRepository;
import nl.tudelft.sem.template.voting.integration.utils.JsonUtil;
import nl.tudelft.sem.template.voting.models.RuleVotingRequestModel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles({"test", "mockTokenVerifier", "mockAuthenticationManager"})
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GetRuleVotingIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private transient JwtTokenVerifier mockJwtTokenVerifier;
    @Autowired
    private transient RuleVotingRepository ruleVotingRepository;
    private Long proposalRuleVoteId;
    private RuleVoting proposalRuleVoting;
    private Long amendmentRuleVoteId;
    private RuleVoting amendmentRuleVoting;


    /**
     * Create the stubs for authentication and initialize variables.
     */
    @BeforeAll
    public void setup() {
        this.proposalRuleVoteId = 1L;
        this.proposalRuleVoting = new RuleVoting(11, 42, "Bleep", null, "Proposal");
        ruleVotingRepository.save(this.proposalRuleVoting);
        this.amendmentRuleVoteId = 2L;
        this.amendmentRuleVoting = new RuleVoting(11, 42, "Bleep", "Bloop", "Amendment");
        ruleVotingRepository.save(this.amendmentRuleVoting);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUserIdFromToken(anyString())).thenReturn("ExampleUser");
    }

    @Test
    public void getProposalRuleVoteTest() throws Exception {
        Calendar cal = Calendar.getInstance();
        this.proposalRuleVoting = ruleVotingRepository.findById(this.proposalRuleVoteId).orElseGet(null);
        cal.setTime(this.proposalRuleVoting.getCreationDate());
        cal.add(Calendar.DAY_OF_MONTH, 14);
        this.proposalRuleVoting.setEndDate(cal.getTime());
        ruleVotingRepository.save(this.proposalRuleVoting);

        RuleVotingRequestModel model = new RuleVotingRequestModel();
        model.setRuleVotingId(this.proposalRuleVoteId);

        ResultActions result = mockMvc.perform(get("/rule-voting/get-rule-vote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        Optional<RuleVoting> voting = ruleVotingRepository.findById(this.proposalRuleVoteId);

        if (voting.isPresent()) {
            Date date = voting.get().getEndDate();
            cal = Calendar.getInstance();
            cal.setTime(date);
        }

        assertThat(response).isEqualTo("The user: 42 proposes to add the rule:" + System.lineSeparator()
                + "\"Bleep\"." + System.lineSeparator() + "The voting procedure is still in reviewing."
                + System.lineSeparator() + "The voting will start on: " + cal.getTime());
    }

    @Test
    public void proposalCurrentEqualsVotingDateTest() throws Exception {
        this.proposalRuleVoting = ruleVotingRepository.findById(this.proposalRuleVoteId).orElseGet(null);
        this.proposalRuleVoting.setEndDate(new Date(System.currentTimeMillis()));
        ruleVotingRepository.save(this.proposalRuleVoting);

        RuleVotingRequestModel model = new RuleVotingRequestModel();
        model.setRuleVotingId(this.proposalRuleVoteId);

        ResultActions result = mockMvc.perform(get("/rule-voting/get-rule-vote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        Optional<RuleVoting> voting = ruleVotingRepository.findById(this.proposalRuleVoteId);
        Calendar cal = null;
        if (voting.isPresent()) {
            Date date = voting.get().getEndDate();
            cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.DAY_OF_MONTH, 2);
        }
        assert cal != null;

        assertThat(response).isEqualTo("The user: 42 proposes to add the rule:" + System.lineSeparator()
                + "\"Bleep\"." + System.lineSeparator() + "You can cast your vote now."
                + System.lineSeparator() + "The voting will end on: " + cal.getTime());
    }

    @Test
    public void proposalBetweenVotingEndDateTest() throws Exception {
        Calendar cal = Calendar.getInstance();
        this.proposalRuleVoting = ruleVotingRepository.findById(this.proposalRuleVoteId).orElseGet(null);
        cal.setTime(this.proposalRuleVoting.getCreationDate());
        cal.add(Calendar.DAY_OF_MONTH, -1);
        this.proposalRuleVoting.setEndDate(cal.getTime());
        ruleVotingRepository.save(this.proposalRuleVoting);

        RuleVotingRequestModel model = new RuleVotingRequestModel();
        model.setRuleVotingId(this.proposalRuleVoteId);

        ResultActions result = mockMvc.perform(get("/rule-voting/get-rule-vote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        Optional<RuleVoting> voting = ruleVotingRepository.findById(this.proposalRuleVoteId);

        if (voting.isPresent()) {
            Date date = voting.get().getEndDate();
            cal.setTime(date);
            cal.add(Calendar.DAY_OF_MONTH, 2);
        }

        assertThat(response).isEqualTo("The user: 42 proposes to add the rule:" + System.lineSeparator()
                + "\"Bleep\"." + System.lineSeparator() + "You can cast your vote now."
                + System.lineSeparator() + "The voting will end on: " + cal.getTime());
    }

    @Test
    public void proposalCurrentEqualEndDateTest() throws Exception {
        Calendar cal = Calendar.getInstance();
        this.proposalRuleVoting = ruleVotingRepository.findById(this.proposalRuleVoteId).orElseGet(null);
        cal.setTime(new Date(System.currentTimeMillis()));
        cal.add(Calendar.DAY_OF_MONTH, -2);
        this.proposalRuleVoting.setEndDate(cal.getTime());
        ruleVotingRepository.save(this.proposalRuleVoting);

        RuleVotingRequestModel model = new RuleVotingRequestModel();
        model.setRuleVotingId(this.proposalRuleVoteId);

        ResultActions result = mockMvc.perform(get("/rule-voting/get-rule-vote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response)
                .isEqualTo("The user: 42 proposes to add the rule:" + System.lineSeparator()
                        + "\"Bleep\"." + System.lineSeparator() + "Voting has ended." + System.lineSeparator()
                        + "The results can be accessed through the association.");
    }

    @Test
    public void proposalCurrentAfterEndDateTest() throws Exception {
        Calendar cal = Calendar.getInstance();
        this.proposalRuleVoting = ruleVotingRepository.findById(this.proposalRuleVoteId).orElseGet(null);
        cal.setTime(new Date(System.currentTimeMillis()));
        cal.add(Calendar.DAY_OF_MONTH, -4);
        this.proposalRuleVoting.setEndDate(cal.getTime());
        ruleVotingRepository.save(this.proposalRuleVoting);

        RuleVotingRequestModel model = new RuleVotingRequestModel();
        model.setRuleVotingId(this.proposalRuleVoteId);

        ResultActions result = mockMvc.perform(get("/rule-voting/get-rule-vote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response)
                .isEqualTo("The user: 42 proposes to add the rule:" + System.lineSeparator()
                        + "\"Bleep\"." + System.lineSeparator() + "Voting has ended." + System.lineSeparator()
                        + "The results can be accessed through the association.");
    }

    @Test
    public void getAmendmentRuleVoteTest() throws Exception {
        Calendar cal = Calendar.getInstance();
        this.amendmentRuleVoting = ruleVotingRepository.findById(this.amendmentRuleVoteId).orElseGet(null);
        cal.setTime(this.amendmentRuleVoting.getCreationDate());
        cal.add(Calendar.DAY_OF_MONTH, 14);
        this.amendmentRuleVoting.setEndDate(cal.getTime());
        ruleVotingRepository.save(this.amendmentRuleVoting);

        RuleVotingRequestModel model = new RuleVotingRequestModel();
        model.setRuleVotingId(this.amendmentRuleVoteId);

        ResultActions result = mockMvc.perform(get("/rule-voting/get-rule-vote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        Optional<RuleVoting> voting = ruleVotingRepository.findById(this.amendmentRuleVoteId);

        if (voting.isPresent()) {
            Date date = voting.get().getEndDate();
            cal = Calendar.getInstance();
            cal.setTime(date);
        }

        assertThat(response)
                .isEqualTo("The user: 42 proposes to change the rule:" + System.lineSeparator()
                        + "\"Bleep\"." + System.lineSeparator() + "into:" + System.lineSeparator()
                        + "\"Bloop\"." + System.lineSeparator() + "The voting procedure is still in reviewing."
                        + System.lineSeparator() + "The voting will start on: " + cal.getTime());
    }

    @Test
    public void amendmentCurrentEqualsVotingDateTest() throws Exception {
        this.amendmentRuleVoting = ruleVotingRepository.findById(this.amendmentRuleVoteId).orElseGet(null);
        this.amendmentRuleVoting.setEndDate(new Date(System.currentTimeMillis()));
        ruleVotingRepository.save(this.amendmentRuleVoting);

        RuleVotingRequestModel model = new RuleVotingRequestModel();
        model.setRuleVotingId(this.amendmentRuleVoteId);

        ResultActions result = mockMvc.perform(get("/rule-voting/get-rule-vote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        Optional<RuleVoting> voting = ruleVotingRepository.findById(this.amendmentRuleVoteId);
        Calendar cal = null;
        if (voting.isPresent()) {
            Date date = voting.get().getEndDate();
            cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.DAY_OF_MONTH, 2);
        }
        assert cal != null;

        assertThat(response)
                .isEqualTo("The user: 42 proposes to change the rule:" + System.lineSeparator()
                        + "\"Bleep\"." + System.lineSeparator() + "into:" + System.lineSeparator()
                        + "\"Bloop\"." + System.lineSeparator() + "You can cast your vote now."
                        + System.lineSeparator() + "The voting will end on: " + cal.getTime());
    }

    @Test
    public void amendmentBetweenVotingEndDateTest() throws Exception {
        Calendar cal = Calendar.getInstance();
        this.amendmentRuleVoting = ruleVotingRepository.findById(this.amendmentRuleVoteId).orElseGet(null);
        cal.setTime(this.amendmentRuleVoting.getCreationDate());
        cal.add(Calendar.DAY_OF_MONTH, -1);
        this.amendmentRuleVoting.setEndDate(cal.getTime());
        ruleVotingRepository.save(this.amendmentRuleVoting);

        RuleVotingRequestModel model = new RuleVotingRequestModel();
        model.setRuleVotingId(this.amendmentRuleVoteId);

        ResultActions result = mockMvc.perform(get("/rule-voting/get-rule-vote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        Optional<RuleVoting> voting = ruleVotingRepository.findById(this.amendmentRuleVoteId);

        if (voting.isPresent()) {
            Date date = voting.get().getEndDate();
            cal.setTime(date);
            cal.add(Calendar.DAY_OF_MONTH, 2);
        }

        assertThat(response)
                .isEqualTo("The user: 42 proposes to change the rule:" + System.lineSeparator()
                + "\"Bleep\"." + System.lineSeparator() + "into:" + System.lineSeparator()
                + "\"Bloop\"." + System.lineSeparator() + "You can cast your vote now."
                + System.lineSeparator() + "The voting will end on: " + cal.getTime());
    }

    @Test
    public void amendmentCurrentEqualEndDateTest() throws Exception {
        Calendar cal = Calendar.getInstance();
        this.amendmentRuleVoting = ruleVotingRepository.findById(this.amendmentRuleVoteId).orElseGet(null);
        cal.setTime(new Date(System.currentTimeMillis()));
        cal.add(Calendar.DAY_OF_MONTH, -2);
        this.amendmentRuleVoting.setEndDate(cal.getTime());
        ruleVotingRepository.save(this.amendmentRuleVoting);

        RuleVotingRequestModel model = new RuleVotingRequestModel();
        model.setRuleVotingId(this.amendmentRuleVoteId);

        ResultActions result = mockMvc.perform(get("/rule-voting/get-rule-vote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response)
                .isEqualTo("The user: 42 proposes to change the rule:" + System.lineSeparator()
                        + "\"Bleep\"." + System.lineSeparator() + "into:" + System.lineSeparator()
                        + "\"Bloop\"." + System.lineSeparator() + "Voting has ended." + System.lineSeparator()
                        + "The results can be accessed through the association.");
    }

    @Test
    public void amendmentCurrentAfterEndDateTest() throws Exception {
        Calendar cal = Calendar.getInstance();
        this.amendmentRuleVoting = ruleVotingRepository.findById(this.amendmentRuleVoteId).orElseGet(null);
        cal.setTime(new Date(System.currentTimeMillis()));
        cal.add(Calendar.DAY_OF_MONTH, -4);
        this.amendmentRuleVoting.setEndDate(cal.getTime());
        ruleVotingRepository.save(this.amendmentRuleVoting);

        RuleVotingRequestModel model = new RuleVotingRequestModel();
        model.setRuleVotingId(this.amendmentRuleVoteId);

        ResultActions result = mockMvc.perform(get("/rule-voting/get-rule-vote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response)
                .isEqualTo("The user: 42 proposes to change the rule:" + System.lineSeparator()
                        + "\"Bleep\"." + System.lineSeparator() + "into:" + System.lineSeparator()
                        + "\"Bloop\"." + System.lineSeparator() + "Voting has ended." + System.lineSeparator()
                        + "The results can be accessed through the association.");
    }

    @Test
    public void nullRuleVoteIdTest() throws Exception {
        RuleVotingRequestModel model = new RuleVotingRequestModel();
        model.setRuleVotingId(null);

        ResultActions result = mockMvc.perform(get("/rule-voting/get-rule-vote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isBadRequest());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response)
                .isEqualTo("The rule vote id is null.");
    }

    @Test
    public void noMatchingRuleVoteIdTest() throws Exception {
        RuleVotingRequestModel model = new RuleVotingRequestModel();
        model.setRuleVotingId(4L);

        ResultActions result = mockMvc.perform(get("/rule-voting/get-rule-vote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isBadRequest());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response)
                .isEqualTo("There is no open rule vote with the provided ID.");
    }
}