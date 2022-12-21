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
import nl.tudelft.sem.template.voting.models.RuleProposalRequestModel;
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
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@AutoConfigureMockMvc
public class ProposeRuleVotingIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private transient JwtTokenVerifier mockJwtTokenVerifier;
    @Autowired
    private transient RuleVotingRepository ruleVotingRepository;
    private int associationId;
    private int userId;
    private String rule;

    /**
     * Create the stubs for authentication.
     */
    @BeforeEach
    public void setup() {
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUserIdFromToken(anyString())).thenReturn("ExampleUser");
    }

    @Test
    public void proposeRuleTest() throws Exception {
        this.associationId = 11;
        this.userId = 42;
        this.rule = "One should not murder the other members!";
        RuleProposalRequestModel model = new RuleProposalRequestModel();
        model.setUserId(this.userId);
        model.setAssociationId(this.associationId);
        model.setRule(this.rule);

        ResultActions result = mockMvc.perform(post("/rule-voting/propose-rule")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        Optional<RuleVoting> voting = ruleVotingRepository.findById(1L);
        Calendar cal = null;
        if (voting.isPresent()) {
            Date date = voting.get().getEndDate();
            cal = Calendar.getInstance();
            cal.setTime(date);
        }

        assert cal != null;
        assertThat(response).isEqualTo("Rule: \"One should not murder the other members!\" "
                + "has been proposed by: 42." + System.lineSeparator() + "The vote will be held on: " + cal.getTime());
    }

    @Test
    public void nullAssociationIdTest() throws Exception {
        this.userId = 42;
        this.rule = "One should not murder the other members!";
        RuleProposalRequestModel model = new RuleProposalRequestModel();
        model.setUserId(this.userId);
        model.setAssociationId(null);
        model.setRule(this.rule);

        ResultActions result = mockMvc.perform(post("/rule-voting/propose-rule")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isBadRequest());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("The associationID is invalid.");
    }

    @Test
    public void nullUserIdTest() throws Exception {
        this.associationId = 11;
        this.rule = "One should not murder the other members!";

        RuleProposalRequestModel model = new RuleProposalRequestModel();
        model.setUserId(null);
        model.setAssociationId(this.associationId);
        model.setRule(this.rule);

        ResultActions result = mockMvc.perform(post("/rule-voting/propose-rule")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isBadRequest());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("The userID is invalid.");
    }

    @Test
    public void nullRuleTest() throws Exception {
        this.associationId = 11;
        this.userId = 42;

        RuleProposalRequestModel model = new RuleProposalRequestModel();
        model.setUserId(this.userId);
        model.setAssociationId(this.associationId);
        model.setRule(null);

        ResultActions result = mockMvc.perform(post("/rule-voting/propose-rule")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isBadRequest());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("The rule is null.");
    }

    @Test
    public void emptyRuleTest() throws Exception {
        this.associationId = 11;
        this.userId = 42;
        this.rule = "";

        RuleProposalRequestModel model = new RuleProposalRequestModel();
        model.setUserId(this.userId);
        model.setAssociationId(this.associationId);
        model.setRule(this.rule);

        ResultActions result = mockMvc.perform(post("/rule-voting/propose-rule")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isBadRequest());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("The rule's description is empty.");
    }

    @Test
    public void ruleTooLongTest() throws Exception {
        this.associationId = 11;
        this.userId = 42;
        this.rule = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

        RuleProposalRequestModel model = new RuleProposalRequestModel();
        model.setUserId(this.userId);
        model.setAssociationId(this.associationId);
        model.setRule(this.rule);

        ResultActions result = mockMvc.perform(post("/rule-voting/propose-rule")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isBadRequest());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response)
                .isEqualTo("The rule description exceeds the maximum length of 100 characters.");
    }
}