package nl.tudelft.sem.template.voting.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.*;
import nl.tudelft.sem.template.voting.authentication.JwtTokenVerifier;
import nl.tudelft.sem.template.voting.domain.VotingType;
import nl.tudelft.sem.template.voting.domain.rulevoting.RuleVoting;
import nl.tudelft.sem.template.voting.domain.rulevoting.RuleVotingRepository;
import nl.tudelft.sem.template.voting.integration.utils.JsonUtil;
import nl.tudelft.sem.template.voting.models.RuleVoteRequestModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles({"test", "mockTokenVerifier", "mockAuthenticationManager"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class CastRuleVoteVotingIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private transient JwtTokenVerifier mockJwtTokenVerifier;
    @Autowired
    private transient RuleVotingRepository ruleVotingRepository;
    private Long ruleVoteId;
    private int userId;
    private RuleVoting ruleVoting;


    /**
     * Create the stubs for authentication and initialize variables.
     */
    @BeforeEach
    public void setup() {
        this.ruleVoteId = 1L;
        this.userId = 10;
        this.ruleVoting = new RuleVoting(12, this.userId, "Bleep", null, VotingType.PROPOSAL);
        this.ruleVotingRepository.save(this.ruleVoting);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUserIdFromToken(anyString())).thenReturn("ExampleUser");
    }

    @Test
    public void castRuleVoteInFavourTest() throws Exception {
        this.ruleVoting = ruleVotingRepository.findById(this.ruleVoteId).orElseGet(null);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        this.ruleVoting.setEndDate(cal.getTime());
        ruleVotingRepository.save(this.ruleVoting);

        RuleVoteRequestModel model = new RuleVoteRequestModel();
        model.setRuleVoteId(this.ruleVoteId);
        model.setUserId(this.userId);
        model.setVote("for");

        ResultActions result = mockMvc.perform(post("/rule-voting/cast-vote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("The user with ID 10 voted in favour of the "
                + "proposal under consideration in rule vote: 1");
    }

    @Test
    public void castRuleVoteAbstainTest() throws Exception {
        this.ruleVoting = ruleVotingRepository.findById(this.ruleVoteId).orElseGet(null);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        this.ruleVoting.setEndDate(cal.getTime());
        ruleVotingRepository.save(this.ruleVoting);

        RuleVoteRequestModel model = new RuleVoteRequestModel();
        model.setRuleVoteId(this.ruleVoteId);
        model.setUserId(this.userId);
        model.setVote("abstain");

        ResultActions result = mockMvc.perform(post("/rule-voting/cast-vote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("The user with ID 10 abstains from voting for the "
                + "proposal under consideration in rule vote: 1");
    }

    @Test
    public void castRuleVoteAgainstTest() throws Exception {
        this.ruleVoting = ruleVotingRepository.findById(this.ruleVoteId).orElseGet(null);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        this.ruleVoting.setEndDate(cal.getTime());
        ruleVotingRepository.save(this.ruleVoting);

        RuleVoteRequestModel model = new RuleVoteRequestModel();
        model.setRuleVoteId(this.ruleVoteId);
        model.setUserId(this.userId);
        model.setVote("against");

        ResultActions result = mockMvc.perform(post("/rule-voting/cast-vote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("The user with ID 10 voted against the "
                + "proposal under consideration in rule vote: 1");
    }

    @Test
    public void voteInReviewingTest() throws Exception {
        RuleVoteRequestModel model = new RuleVoteRequestModel();
        model.setRuleVoteId(this.ruleVoteId);
        model.setUserId(this.userId);
        model.setVote("against");

        ResultActions result = mockMvc.perform(post("/rule-voting/cast-vote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isBadRequest());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("The rule vote is still in reviewing. It is too early to cast a vote.");
    }

    @Test
    public void voteEnded() throws Exception {
        this.ruleVoting = ruleVotingRepository.findById(this.ruleVoteId).orElseGet(null);
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(System.currentTimeMillis()));
        cal.add(Calendar.DAY_OF_MONTH, -2);
        this.ruleVoting.setEndDate(cal.getTime());
        ruleVotingRepository.save(this.ruleVoting);

        RuleVoteRequestModel model = new RuleVoteRequestModel();
        model.setRuleVoteId(this.ruleVoteId);
        model.setUserId(this.userId);
        model.setVote("for");

        ResultActions result = mockMvc.perform(post("/rule-voting/cast-vote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isBadRequest());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("The rule vote has ended.");
    }

    @Test
    public void voteNullTest() throws Exception {
        RuleVoteRequestModel model = new RuleVoteRequestModel();
        model.setRuleVoteId(this.ruleVoteId);
        model.setUserId(this.userId);
        model.setVote(null);

        ResultActions result = mockMvc.perform(post("/rule-voting/cast-vote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isBadRequest());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("The vote is not valid, please pick from: for/against/abstain.");
    }

    @Test
    public void invalidVoteTest() throws Exception {
        RuleVoteRequestModel model = new RuleVoteRequestModel();
        model.setRuleVoteId(this.ruleVoteId);
        model.setUserId(this.userId);
        model.setVote("fesfefs");

        ResultActions result = mockMvc.perform(post("/rule-voting/cast-vote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isBadRequest());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("The vote is not valid, please pick from: for/against/abstain.");
    }

    @Test
    public void ruleVoteIdNullTest() throws Exception {
        RuleVoteRequestModel model = new RuleVoteRequestModel();
        model.setRuleVoteId(null);
        model.setUserId(this.userId);
        model.setVote("for");

        ResultActions result = mockMvc.perform(post("/rule-voting/cast-vote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isBadRequest());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("The rule vote id is null.");
    }

    @Test
    public void ruleVoteDoesNotExistTest() throws Exception {
        RuleVoteRequestModel model = new RuleVoteRequestModel();
        model.setRuleVoteId(342L);
        model.setUserId(this.userId);
        model.setVote("for");

        ResultActions result = mockMvc.perform(post("/rule-voting/cast-vote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isBadRequest());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("There is no rule vote ongoing with the id: 342");
    }

    @Test
    public void changeVoteTest() throws Exception {
        this.ruleVoting = ruleVotingRepository.findById(this.ruleVoteId).orElseGet(null);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        this.ruleVoting.setEndDate(cal.getTime());
        ruleVotingRepository.save(this.ruleVoting);

        RuleVoteRequestModel model = new RuleVoteRequestModel();
        model.setRuleVoteId(this.ruleVoteId);
        model.setUserId(this.userId);
        model.setVote("for");

        ResultActions result = mockMvc.perform(post("/rule-voting/cast-vote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("The user with ID 10 voted in favour of the "
                + "proposal under consideration in rule vote: 1");

        model.setVote("against");

        result = mockMvc.perform(post("/rule-voting/cast-vote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("The user with ID 10 voted against the "
                + "proposal under consideration in rule vote: 1");

        this.ruleVoting = ruleVotingRepository.findById(this.ruleVoteId).orElseGet(null);
        List<Pair<Integer, String>> expected = new ArrayList<>();
        expected.add(Pair.of(this.userId, "against"));
        assertThat(this.ruleVoting.getVotes()).isEqualTo(expected);
    }

}