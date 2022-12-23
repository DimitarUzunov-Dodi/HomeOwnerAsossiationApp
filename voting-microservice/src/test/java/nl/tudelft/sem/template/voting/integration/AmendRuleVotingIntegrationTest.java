package nl.tudelft.sem.template.voting.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.*;
import nl.tudelft.sem.template.voting.authentication.JwtTokenVerifier;
import nl.tudelft.sem.template.voting.domain.rulevoting.RuleVoting;
import nl.tudelft.sem.template.voting.domain.rulevoting.RuleVotingRepository;
import nl.tudelft.sem.template.voting.integration.utils.JsonUtil;
import nl.tudelft.sem.template.voting.models.RuleAmendmentRequestModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
public class AmendRuleVotingIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private transient JwtTokenVerifier mockJwtTokenVerifier;
    @Autowired
    private transient RuleVotingRepository ruleVotingRepository;
    private int associationId;
    private String userId;
    private String rule;
    private String amendment;

    /**
     * Create the stubs for authentication.
     */
    @BeforeEach
    public void setup() {
        this.associationId = 11;
        this.userId = "42";
        this.rule = "One should not murder the other members!";
        this.amendment = "One should be allowed to murder the other members!";
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUserIdFromToken(anyString())).thenReturn("ExampleUser");
    }

    @Test
    public void proposeRuleTest() throws Exception {
        RuleAmendmentRequestModel model = new RuleAmendmentRequestModel();
        model.setUserId(this.userId);
        model.setAssociationId(this.associationId);
        model.setRule(this.rule);
        model.setAmendment(this.amendment);

        ResultActions result = mockMvc.perform(post("/rule-voting/amend-rule")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        Optional<RuleVoting> voting = ruleVotingRepository.findById(1L);
        Calendar cal = Calendar.getInstance();
        if (voting.isPresent()) {
            Date date = voting.get().getEndDate();
            cal.setTime(date);
            cal.add(Calendar.DAY_OF_MONTH, -2);
        }

        assert cal != null;
        assertThat(response).isEqualTo("The user: 42 proposes to change the rule: "
                + "\"One should not murder the other members!\"" + System.lineSeparator()
                + "to: \"One should be allowed to murder the other members!\"" + System.lineSeparator()
                + "The vote will be held on: " + cal.getTime());
    }

    @Test
    public void nullAmendmentTest() throws Exception {
        RuleAmendmentRequestModel model = new RuleAmendmentRequestModel();
        model.setUserId(this.userId);
        model.setAssociationId(this.associationId);
        model.setRule(this.rule);
        model.setAmendment(null);

        ResultActions result = mockMvc.perform(post("/rule-voting/amend-rule")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isBadRequest());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("The amendment is null.");
    }

    @Test
    public void emptyAmendmentTest() throws Exception {
        RuleAmendmentRequestModel model = new RuleAmendmentRequestModel();
        model.setUserId(this.userId);
        model.setAssociationId(this.associationId);
        model.setRule(this.rule);
        model.setAmendment("");

        ResultActions result = mockMvc.perform(post("/rule-voting/amend-rule")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        Optional<RuleVoting> voting = ruleVotingRepository.findById(1L);
        Calendar cal = Calendar.getInstance();
        if (voting.isPresent()) {
            Date date = voting.get().getEndDate();
            cal.setTime(date);
            cal.add(Calendar.DAY_OF_MONTH, -2);
        }

        assertThat(response)
                .isEqualTo("The user: 42 proposes to remove the rule: \"One should not murder the other members!\""
                        + System.lineSeparator() + "The vote will be held on: " + cal.getTime());
    }

    @Test
    public void ruleTooLongTest() throws Exception {
        this.amendment = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

        RuleAmendmentRequestModel model = new RuleAmendmentRequestModel();
        model.setUserId(this.userId);
        model.setAssociationId(this.associationId);
        model.setRule(this.rule);
        model.setAmendment(this.amendment);

        ResultActions result = mockMvc.perform(post("/rule-voting/amend-rule")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isBadRequest());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response)
                .isEqualTo("The amendment's description exceeds the maximum length of 100 characters.");
    }

    @Test
    public void ruleAlreadyInAnotherVote() throws Exception {
        RuleAmendmentRequestModel model = new RuleAmendmentRequestModel();
        model.setUserId(this.userId);
        model.setAssociationId(this.associationId);
        model.setRule(this.rule);
        model.setAmendment(this.amendment);

        mockMvc.perform(post("/rule-voting/amend-rule")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        ResultActions result = mockMvc.perform(post("/rule-voting/amend-rule")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isBadRequest());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response)
                .isEqualTo("The rule is already under evaluation.");
    }

    @Test
    public void amendmentAlreadyInAnotherVote() throws Exception {
        RuleAmendmentRequestModel model = new RuleAmendmentRequestModel();
        model.setUserId(this.userId);
        model.setAssociationId(this.associationId);
        model.setRule(this.rule);
        model.setAmendment(this.amendment);

        mockMvc.perform(post("/rule-voting/amend-rule")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        model.setRule("Something");

        ResultActions result = mockMvc.perform(post("/rule-voting/amend-rule")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isBadRequest());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response)
                .isEqualTo("The amendment already exists in another vote.");
    }
}