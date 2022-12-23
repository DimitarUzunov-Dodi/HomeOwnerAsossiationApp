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
import nl.tudelft.sem.template.association.domain.membership.Membership;
import nl.tudelft.sem.template.association.domain.membership.MembershipRepository;
import nl.tudelft.sem.template.association.integration.utils.JsonUtil;
import nl.tudelft.sem.template.association.models.CreateAssociationRequestModel;
import nl.tudelft.sem.template.association.models.JoinAssociationRequestModel;
import nl.tudelft.sem.template.association.models.UserAssociationRequestModel;
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
public class AssociationIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private transient JwtTokenVerifier mockJwtTokenVerifier;
    @Autowired
    private transient AssociationRepository mockAssociationRepository;
    @Autowired
    private transient MembershipRepository membershipRepository;
    private HashSet<String> councilMembers;
    private Association association;
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
        this.association.addMember("a");
        this.association.addMember("b");
        this.association.addMember("c");
        this.association.addMember("d");
        mockAssociationRepository.save(this.association);

        Membership membership1 = new Membership("a", association.getId(), "test", "test", "test", "test", "test");
        Membership membership2 = new Membership("b", association.getId(), "test", "test", "test", "test", "test");
        Membership membership3 = new Membership("c", association.getId(), "test", "test", "test", "test", "test");
        Membership membership4 = new Membership("d", association.getId(), "test", "test", "test", "test", "test");
        membershipRepository.save(membership1);
        membershipRepository.save(membership2);
        membershipRepository.save(membership3);
        membershipRepository.save(membership4);

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
    public void testCreateAssociation() throws Exception {
        CreateAssociationRequestModel model = new CreateAssociationRequestModel();
        model.setName("name");
        model.setCountry("country");
        model.setCity("city");
        model.setDescription("description");
        model.setCouncilNumber(5);

        ResultActions result = mockMvc.perform(post("/association/create-association")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).contains("Association was created:" + System.lineSeparator() + "ID: ");
        assertThat(response).contains(System.lineSeparator()
                + "Name: name" + System.lineSeparator() + "Country: country" + System.lineSeparator()
                + "Description: description" + System.lineSeparator() + "Max council members: 5");
    }

    @Test
    public void testJoinAssociation() throws Exception {
        JoinAssociationRequestModel model = new JoinAssociationRequestModel();
        model.setUserId("userID");
        model.setAssociationId(association.getId());
        model.setCountry("test");
        model.setCity("test");
        model.setStreet("test");
        model.setHouseNumber("test");
        model.setPostalCode("test");

        ResultActions result = mockMvc.perform(post("/association/join-association")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("User userID successfully joined association " + association.getId());
    }

    @Test
    public void testJoinAssociationWrongAssociationId() throws Exception {
        JoinAssociationRequestModel model = new JoinAssociationRequestModel();
        model.setUserId("userID");
        model.setAssociationId(500);
        model.setCountry("test");
        model.setCity("test");
        model.setStreet("test");
        model.setHouseNumber("test");
        model.setPostalCode("test");

        ResultActions result = mockMvc.perform(post("/association/join-association")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().is4xxClientError());

        String response = result.andReturn().getResponse().getErrorMessage();

        assertThat(response).isEqualTo("Association with ID 500 does not exist.");
    }

    @Test
    public void testLeaveAssociation() throws Exception {
        JoinAssociationRequestModel model = new JoinAssociationRequestModel();
        model.setUserId("a");
        model.setAssociationId(association.getId());
        model.setCountry("test");
        model.setCity("test");
        model.setStreet("test");
        model.setHouseNumber("test");
        model.setPostalCode("test");

        ResultActions result = mockMvc.perform(post("/association/leave-association")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("User a left association " + association.getId());
    }

    @Test
    public void testLeaveAssociationWrongAssociationId() throws Exception {
        JoinAssociationRequestModel model = new JoinAssociationRequestModel();
        model.setUserId("a");
        model.setAssociationId(500);
        model.setCountry("test");
        model.setCity("test");
        model.setStreet("test");
        model.setHouseNumber("test");
        model.setPostalCode("test");

        ResultActions result = mockMvc.perform(post("/association/leave-association")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().is4xxClientError());

        String response = result.andReturn().getResponse().getErrorMessage();

        assertThat(response).isEqualTo("Association/membership does not exist.");
    }

    @Test
    public void testVerifyCandidate() throws Exception {
        UserAssociationRequestModel model = new UserAssociationRequestModel();
        model.setAssociationId(association.getId());
        model.setUserId("d");

        ResultActions result = mockMvc.perform(post("/association/verify-candidate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().is4xxClientError());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("You can not be a candidate for the council.");
    }
}
