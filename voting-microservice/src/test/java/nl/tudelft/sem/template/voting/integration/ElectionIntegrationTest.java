package nl.tudelft.sem.template.voting.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;
import nl.tudelft.sem.template.voting.authentication.JwtTokenVerifier;
import nl.tudelft.sem.template.voting.domain.election.Election;
import nl.tudelft.sem.template.voting.domain.election.ElectionRepository;
import nl.tudelft.sem.template.voting.integration.utils.JsonUtil;
import nl.tudelft.sem.template.voting.models.AssociationRequestModel;
import nl.tudelft.sem.template.voting.models.ElectionVoteRequestModel;
import nl.tudelft.sem.template.voting.models.UserAssociationRequestModel;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
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
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ActiveProfiles({"test", "mockTokenVerifier", "mockAuthenticationManager"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ElectionIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private transient JwtTokenVerifier mockJwtTokenVerifier;
    @Autowired
    private transient ElectionRepository electionRepository;
    private int associationId;
    private Election election;
    long dayInMs = 1000 * 60 * 60 * 24;

    /**
     * Create the stubs for authentication and initialize variables.
     */
    @BeforeEach
    public void setup() {
        associationId = 1;
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUserIdFromToken(anyString())).thenReturn("ExampleUser");
    }

    /**
     * Tests the apply-for-candidate endpoint.
     */
    public void applyForCandidateNoElection() throws Exception {
        UserAssociationRequestModel model = new UserAssociationRequestModel();
        model.setAssociationId(associationId);
        model.setUserId("ExampleUser");

        ResultActions result = mockMvc.perform(post("/election/apply-for-candidate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().is4xxClientError());

        String response = result.andReturn().getResponse().getContentAsString();

        Optional<Election> optElection = electionRepository.findByAssociationId(associationId);

        assertThat(optElection.isPresent()).isFalse();

        assertThat(response).contains("Association with ID " + associationId + " does not have an active election.");
    }

    /**
     * tests the apply-for-candidate endpoint.
     */
    public void applyForCandidateNoAuthentication() throws Exception {
        UserAssociationRequestModel model = new UserAssociationRequestModel();
        model.setAssociationId(associationId);
        model.setUserId("user2");

        ResultActions result = mockMvc.perform(post("/election/apply-for-candidate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().is4xxClientError());

        String response = result.andReturn().getResponse().getErrorMessage();

        assertThat(response).isEqualTo("401 UNAUTHORIZED \"INVALID_CREDENTIALS\"");
    }

    /**
     * Tests the cast-vote endpoint.
     */
    public void voteNoElection() throws Exception {
        ElectionVoteRequestModel model = new ElectionVoteRequestModel();
        model.setAssociationId(associationId);
        model.setVoterId("ExampleUser");
        model.setCandidateId("user2");

        ResultActions result = mockMvc.perform(post("/election/cast-vote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().is4xxClientError());

        String response = result.andReturn().getResponse().getErrorMessage();

        Optional<Election> optElection = electionRepository.findByAssociationId(associationId);

        assertThat(optElection.isPresent()).isFalse();

        assertThat(response).isEqualTo("Association with ID " + associationId + " does not have an active election.");
    }

    /**
     * Tests the cast-vote endpoint.
     */
    public void voteNoAuthentication() throws Exception {
        ElectionVoteRequestModel model = new ElectionVoteRequestModel();
        model.setAssociationId(associationId);
        model.setVoterId("user1");
        model.setCandidateId("user2");

        ResultActions result = mockMvc.perform(post("/election/cast-vote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().is4xxClientError());

        String response = result.andReturn().getResponse().getErrorMessage();

        assertThat(response).isEqualTo("401 UNAUTHORIZED \"INVALID_CREDENTIALS\"");
    }

    /**
     * Tests the create-election endpoint.
     */
    public void createElectionTest() throws Exception {
        AssociationRequestModel model = new AssociationRequestModel();
        model.setAssociationId(associationId);

        ResultActions result = mockMvc.perform(post("/election/create-election")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        Optional<Election> optElection = electionRepository.findByAssociationId(associationId);

        assert optElection.isPresent();
        assert optElection.get().getAssociationId() == associationId;
        assertThat(response).contains("Voting was created for association " + associationId + " and will be held on ");
    }

    /**
     * Tests the apply-for-candidate endpoint.
     */
    public void applyForCandidate() throws Exception {
        UserAssociationRequestModel model = new UserAssociationRequestModel();
        model.setAssociationId(associationId);
        model.setUserId("ExampleUser");

        ResultActions result = mockMvc.perform(post("/election/apply-for-candidate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        Optional<Election> optElection = electionRepository.findByAssociationId(associationId);

        assert optElection.isPresent();
        assertThat(optElection.get().getCandidateIds()).contains("ExampleUser");

        assertThat(response).contains("The candidate with ID ExampleUser has been added.");
    }

    @Test
    public void orderedTest() throws Exception {
        applyForCandidateNoElection();
        applyForCandidateNoAuthentication();
        voteNoElection();
        voteNoAuthentication();
        createElectionTest();
        applyForCandidate();
    }
}