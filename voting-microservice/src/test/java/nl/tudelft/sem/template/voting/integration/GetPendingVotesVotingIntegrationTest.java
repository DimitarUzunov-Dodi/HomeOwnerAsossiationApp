package nl.tudelft.sem.template.voting.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import nl.tudelft.sem.template.voting.authentication.JwtTokenVerifier;
import nl.tudelft.sem.template.voting.domain.Voting;
import nl.tudelft.sem.template.voting.domain.VotingType;
import nl.tudelft.sem.template.voting.domain.rulevoting.RuleVoting;
import nl.tudelft.sem.template.voting.domain.rulevoting.RuleVotingRepository;
import nl.tudelft.sem.template.voting.integration.utils.JsonUtil;
import nl.tudelft.sem.template.voting.models.UserAssociationRequestModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
public class GetPendingVotesVotingIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private transient JwtTokenVerifier mockJwtTokenVerifier;
    @Autowired
    private transient RuleVotingRepository ruleVotingRepository;
    private RuleVoting ruleVoting;

    /**
     * Create the stubs for authentication.
     */
    @BeforeEach
    public void setup() {
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUserIdFromToken(anyString())).thenReturn("ExampleUser");
    }

    @Test
    public void typeProposalTest() throws Exception {
        this.ruleVoting = new RuleVoting(1, 42, "Bleep", null, VotingType.PROPOSAL);
        this.ruleVoting.addVote(Pair.of(1, "for"));
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        this.ruleVoting.setEndDate(cal.getTime());
        ruleVotingRepository.save(this.ruleVoting);
        this.ruleVoting = ruleVotingRepository.findByAssociationId(1).get(0);

        UserAssociationRequestModel model = new UserAssociationRequestModel();
        model.setAssociationId(1);
        model.setUserId(1);

        ResultActions result = mockMvc.perform(get("/rule-voting/get-pending-votes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("ID: " + this.ruleVoting.getId()
                + ", Type: Proposal, Status: Voting, Vote: for" + System.lineSeparator());
    }

    @Test
    public void typeAmendmentTest() throws Exception {
        this.ruleVoting = new RuleVoting(1, 42, "Bleep", "fesfse", VotingType.AMENDMENT);
        ruleVotingRepository.save(this.ruleVoting);
        this.ruleVoting = ruleVotingRepository.findByAssociationId(1).get(0);

        UserAssociationRequestModel model = new UserAssociationRequestModel();
        model.setAssociationId(1);
        model.setUserId(1);

        ResultActions result = mockMvc.perform(get("/rule-voting/get-pending-votes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("ID: " + this.ruleVoting.getId()
                + ", Type: Amendment, Status: Reviewing" + System.lineSeparator());
    }

    @Test
    public void statusEndedTest() throws Exception {
        this.ruleVoting = new RuleVoting(1, 42, "Bleep", null, VotingType.PROPOSAL);
        this.ruleVoting.addVote(Pair.of(1, "against"));
        this.ruleVoting.setEndDate(new Date(System.currentTimeMillis()));
        ruleVotingRepository.save(this.ruleVoting);
        this.ruleVoting = ruleVotingRepository.findByAssociationId(1).get(0);

        UserAssociationRequestModel model = new UserAssociationRequestModel();
        model.setAssociationId(1);
        model.setUserId(1);

        ResultActions result = mockMvc.perform(get("/rule-voting/get-pending-votes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("ID: " + this.ruleVoting.getId()
                + ", Type: Proposal, Status: Ended, Vote: against" + System.lineSeparator());
    }

    @Test
    public void pendingVotesEmptyTest() throws Exception {
        UserAssociationRequestModel model = new UserAssociationRequestModel();
        model.setAssociationId(1);
        model.setUserId(1);

        ResultActions result = mockMvc.perform(get("/rule-voting/get-pending-votes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response)
                .isEqualTo("There are no ongoing rule votes corresponding to the association ID: 1.");
    }

    @Test
    public void pendingVotesMultipleTest() throws Exception {
        this.ruleVoting = new RuleVoting(1, 42, "Bleep", null, VotingType.PROPOSAL);
        this.ruleVoting.addVote(Pair.of(1, "against"));
        this.ruleVoting.setEndDate(new Date(System.currentTimeMillis()));
        ruleVotingRepository.save(this.ruleVoting);

        this.ruleVoting = new RuleVoting(1, 42, "shaboop", "fessf", VotingType.AMENDMENT);
        this.ruleVoting.addVote(Pair.of(1, "for"));
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        this.ruleVoting.setEndDate(cal.getTime());
        ruleVotingRepository.save(this.ruleVoting);

        this.ruleVoting = new RuleVoting(1, 42, "scoop", null, VotingType.PROPOSAL);
        ruleVotingRepository.save(this.ruleVoting);

        UserAssociationRequestModel model = new UserAssociationRequestModel();
        model.setAssociationId(1);
        model.setUserId(1);

        ResultActions result = mockMvc.perform(get("/rule-voting/get-pending-votes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        List<Long> ruleVoteIds = ruleVotingRepository.findByAssociationId(1).stream()
                .map(Voting::getId).collect(Collectors.toList());

        assertThat(response).isEqualTo("ID: " + ruleVoteIds.get(0)
                + ", Type: Proposal, Status: Ended, Vote: against" + System.lineSeparator()
                + "ID: " + ruleVoteIds.get(1) + ", Type: Amendment, Status: Voting, Vote: for" + System.lineSeparator()
                + "ID: " + ruleVoteIds.get(2) + ", Type: Proposal, Status: Reviewing" + System.lineSeparator());
    }

    @Test
    public void noVoteTest() throws Exception {
        this.ruleVoting = new RuleVoting(1, 42, "Bleep", null, VotingType.PROPOSAL);
        this.ruleVoting.setEndDate(new Date(System.currentTimeMillis()));
        ruleVotingRepository.save(this.ruleVoting);
        this.ruleVoting = ruleVotingRepository.findByAssociationId(1).get(0);

        UserAssociationRequestModel model = new UserAssociationRequestModel();
        model.setAssociationId(1);
        model.setUserId(1);

        ResultActions result = mockMvc.perform(get("/rule-voting/get-pending-votes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("ID: " + this.ruleVoting.getId()
                + ", Type: Proposal, Status: Ended, Vote: No vote (abstain)" + System.lineSeparator());
    }

    @Test
    public void abstainVoteTest() throws Exception {
        this.ruleVoting = new RuleVoting(1, 42, "Bleep", null, VotingType.PROPOSAL);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        this.ruleVoting.setEndDate(cal.getTime());
        this.ruleVoting.addVote(Pair.of(1, "abstain"));
        ruleVotingRepository.save(this.ruleVoting);
        this.ruleVoting = ruleVotingRepository.findByAssociationId(1).get(0);

        UserAssociationRequestModel model = new UserAssociationRequestModel();
        model.setAssociationId(1);
        model.setUserId(1);

        ResultActions result = mockMvc.perform(get("/rule-voting/get-pending-votes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("ID: " + this.ruleVoting.getId()
                + ", Type: Proposal, Status: Voting, Vote: abstain" + System.lineSeparator());
    }

    @Test
    public void associationIdNullTest() throws Exception {
        UserAssociationRequestModel model = new UserAssociationRequestModel();
        model.setAssociationId(null);
        model.setUserId(1);

        ResultActions result = mockMvc.perform(get("/rule-voting/get-pending-votes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isBadRequest());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("The association ID is null.");
    }


}
