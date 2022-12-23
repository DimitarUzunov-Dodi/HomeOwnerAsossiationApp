package nl.tudelft.sem.template.association.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import nl.tudelft.sem.template.association.authentication.JwtTokenVerifier;
import nl.tudelft.sem.template.association.domain.association.Association;
import nl.tudelft.sem.template.association.domain.association.AssociationRepository;
import nl.tudelft.sem.template.association.domain.history.History;
import nl.tudelft.sem.template.association.domain.history.HistoryRepository;
import nl.tudelft.sem.template.association.integration.utils.JsonUtil;
import nl.tudelft.sem.template.association.models.ElectionResultRequestModel;
import nl.tudelft.sem.template.association.models.RuleVoteResultRequestModel;
import nl.tudelft.sem.template.association.models.UserAssociationRequestModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
public class AssociationIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private transient JwtTokenVerifier mockJwtTokenVerifier;
    @Autowired
    private transient AssociationRepository mockAssociationRepository;
    @Autowired
    private transient HistoryRepository mockHistoryRepository;
    private HashSet<String> councilMembers;
    private Association association;
    private History history;
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
        this.association = new Association("test", "test", "test", "test", 10);
        this.association.setCouncilUserIds(this.councilMembers);
        mockAssociationRepository.save(this.association);

        history = new History(association.getId());
        mockHistoryRepository.save(history);

        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUserIdFromToken(anyString())).thenReturn("ExampleUser");
    }

    @Test
    public void verifyTrueTest() throws Exception {
        this.userId = "a";

        UserAssociationRequestModel model = new UserAssociationRequestModel();
        model.setUserId(this.userId);
        model.setAssociationId(1);

        ResultActions result = mockMvc.perform(post("/association/verify-council-member")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("Passed council member check!");
    }

    @Test
    public void verifyFalseTest() throws Exception {
        UserAssociationRequestModel model = new UserAssociationRequestModel();
        model.setUserId(this.userId);
        model.setAssociationId(1);

        ResultActions result = mockMvc.perform(post("/association/verify-council-member")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isUnauthorized());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("You are not a member of this association's council!");
    }

    @Test
    public void verifyNullTest() throws Exception {
        UserAssociationRequestModel model = new UserAssociationRequestModel();
        model.setUserId(null);
        model.setAssociationId(null);

        ResultActions result = mockMvc.perform(post("/association/verify-council-member")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isUnauthorized());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("You are not a member of this association's council!");
    }

    @Test
    public void verifyUserIdNullTest() throws Exception {
        UserAssociationRequestModel model = new UserAssociationRequestModel();
        model.setUserId(null);
        model.setAssociationId(1);

        ResultActions result = mockMvc.perform(post("/association/verify-council-member")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isUnauthorized());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("You are not a member of this association's council!");
    }

    @Test
    public void verifyCouncilIdNullTest() throws Exception {
        UserAssociationRequestModel model = new UserAssociationRequestModel();
        model.setUserId(this.userId);
        model.setAssociationId(null);

        ResultActions result = mockMvc.perform(post("/association/verify-council-member")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isUnauthorized());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("You are not a member of this association's council!");
    }

    @Test
    public void updateCouncilTest() throws Exception {
        this.association.setCouncilNumber(3);
        mockAssociationRepository.save(association);

        HashMap<String, Integer> hm = new HashMap<>();
        hm.put("a", 4);
        hm.put("b", 3);
        hm.put("d", 5);
        hm.put("e", 6);
        hm.put("f", 10);

        ElectionResultRequestModel model = new ElectionResultRequestModel();

        model.setStandings(hm);
        model.setResult("---");
        model.setDate(new Date());
        model.setAssociationId(association.getId());

        ResultActions result = mockMvc.perform(post("/association/update-council")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();
        assertThat(response).isEqualTo("Council updated!");

        Optional<Association> optionalTestAssociation = mockAssociationRepository.findById(association.getId());

        Association testAssociation = optionalTestAssociation.get();

        assertThat(testAssociation.getCouncilUserIds()).containsExactlyInAnyOrder("f", "e", "d");
    }

    @Test
    public void addRulePassTest() throws Exception {
        RuleVoteResultRequestModel model = new RuleVoteResultRequestModel();

        model.setPassed(true);
        model.setRule("Epic rule. HAH!");
        model.setResult("Epic rule passedddd");
        model.setType("");
        model.setAmendment("");
        model.setAnAmendment(false);
        model.setDate(new Date());
        model.setAssociationId(association.getId());

        ResultActions result = mockMvc.perform(post("/association/update-rules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();
        assertThat(response).isEqualTo("Rules updated!");

        Optional<Association> optionalTestAssociation = mockAssociationRepository.findById(association.getId());

        Association testAssociation = optionalTestAssociation.get();

        assertThat(testAssociation.getRules()).contains("Epic rule. HAH!");
    }

    @Test
    public void changeRulePassTest() throws Exception {
        RuleVoteResultRequestModel model = new RuleVoteResultRequestModel();

        model.setPassed(true);
        model.setRule("Epic rule. HAH!");
        model.setResult("Epic rule passedddd");
        model.setType("");
        model.setAmendment("");
        model.setAnAmendment(false);
        model.setDate(new Date());
        model.setAssociationId(association.getId());

        ResultActions result = mockMvc.perform(post("/association/update-rules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        model.setPassed(true);
        model.setRule("Epic rule. HAH!");
        model.setResult("Epic rule passedddd");
        model.setType("");
        model.setAmendment("WAAAAAAAAAGHHHH!");
        model.setAnAmendment(true);
        model.setDate(new Date());
        model.setAssociationId(association.getId());

        result = mockMvc.perform(post("/association/update-rules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();
        assertThat(response).isEqualTo("Rules updated!");

        Optional<Association> optionalTestAssociation = mockAssociationRepository.findById(association.getId());

        Association testAssociation = optionalTestAssociation.get();

        assertThat(testAssociation.getRules()).contains("WAAAAAAAAAGHHHH!");
    }

    @Test
    public void addRuleFailTest() throws Exception {
        RuleVoteResultRequestModel model = new RuleVoteResultRequestModel();

        model.setPassed(false);
        model.setRule("Epic rule. HAH!");
        model.setResult("Epic rule passedddd");
        model.setType("");
        model.setAmendment("");
        model.setAnAmendment(false);
        model.setDate(new Date());
        model.setAssociationId(association.getId());

        ResultActions result = mockMvc.perform(post("/association/update-rules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();
        assertThat(response).isEqualTo("Rules updated!");

        Optional<Association> optionalTestAssociation = mockAssociationRepository.findById(association.getId());

        Association testAssociation = optionalTestAssociation.get();

        assertThat(testAssociation.getRules()).containsExactly("");
    }

    @Test
    public void changeRuleFailTest() throws Exception {
        RuleVoteResultRequestModel model = new RuleVoteResultRequestModel();

        model.setPassed(true);
        model.setRule("Epic rule. HAH!");
        model.setResult("Epic rule passedddd");
        model.setType("");
        model.setAmendment("");
        model.setAnAmendment(false);
        model.setDate(new Date());
        model.setAssociationId(association.getId());

        ResultActions result = mockMvc.perform(post("/association/update-rules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        model.setPassed(false);
        model.setRule("Epic rule. HAH!");
        model.setResult("Epic rule passedddd");
        model.setType("");
        model.setAmendment("WAAAAAAAAAGHHHH!");
        model.setAnAmendment(true);
        model.setDate(new Date());
        model.setAssociationId(association.getId());

        result = mockMvc.perform(post("/association/update-rules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();
        assertThat(response).isEqualTo("Rules updated!");

        Optional<Association> optionalTestAssociation = mockAssociationRepository.findById(association.getId());

        Association testAssociation = optionalTestAssociation.get();

        assertThat(testAssociation.getRules()).containsExactly("Epic rule. HAH!");
    }

}
