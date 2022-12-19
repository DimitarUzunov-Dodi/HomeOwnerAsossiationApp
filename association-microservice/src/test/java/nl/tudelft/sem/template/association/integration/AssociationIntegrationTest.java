package nl.tudelft.sem.template.association.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.*;
import nl.tudelft.sem.template.association.authentication.JwtTokenVerifier;
import nl.tudelft.sem.template.association.domain.association.Association;
import nl.tudelft.sem.template.association.domain.association.AssociationRepository;
import nl.tudelft.sem.template.association.integration.utils.JsonUtil;
import nl.tudelft.sem.template.association.models.RuleVerificationRequestModel;
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
    private HashSet<Integer> councilMembers;
    private Association association;
    private int userId;

    /**
     * Initialize the councilMembers and userId variables before each test.
     */
    @BeforeEach
    public void setup() {
        this.councilMembers = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            this.councilMembers.add(i);
        }

        this.userId = 10;
        this.association = new Association("test", "test", 10);
        this.association.setCouncilUserIds(this.councilMembers);
        mockAssociationRepository.save(this.association);

        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUserIdFromToken(anyString())).thenReturn("ExampleUser");
    }

    @Test
    public void verifyTrueTest() throws Exception {
        this.userId = 0;

        RuleVerificationRequestModel model = new RuleVerificationRequestModel();
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
        RuleVerificationRequestModel model = new RuleVerificationRequestModel();
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
        RuleVerificationRequestModel model = new RuleVerificationRequestModel();
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
        RuleVerificationRequestModel model = new RuleVerificationRequestModel();
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
        RuleVerificationRequestModel model = new RuleVerificationRequestModel();
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
}
