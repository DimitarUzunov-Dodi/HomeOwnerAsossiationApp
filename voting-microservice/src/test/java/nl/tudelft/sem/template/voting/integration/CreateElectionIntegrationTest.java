package nl.tudelft.sem.template.voting.integration;

import nl.tudelft.sem.template.voting.authentication.JwtTokenVerifier;
import nl.tudelft.sem.template.voting.domain.election.Election;
import nl.tudelft.sem.template.voting.domain.election.ElectionRepository;
import nl.tudelft.sem.template.voting.integration.utils.JsonUtil;
import nl.tudelft.sem.template.voting.models.AssociationRequestModel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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

import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles({"test", "mockTokenVerifier", "mockAuthenticationManager"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CreateElectionIntegrationTest {
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
    @BeforeAll
    public void setup() {
        associationId = 1;
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUserIdFromToken(anyString())).thenReturn("ExampleUser");
    }

    @Test
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
}