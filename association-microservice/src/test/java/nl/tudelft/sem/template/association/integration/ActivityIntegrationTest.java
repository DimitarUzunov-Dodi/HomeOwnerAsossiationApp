package nl.tudelft.sem.template.association.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;
import java.util.HashSet;
import nl.tudelft.sem.template.association.authentication.JwtTokenVerifier;
import nl.tudelft.sem.template.association.domain.activity.ActivityService;
import nl.tudelft.sem.template.association.domain.association.Association;
import nl.tudelft.sem.template.association.domain.association.AssociationRepository;
import nl.tudelft.sem.template.association.integration.utils.JsonUtil;
import nl.tudelft.sem.template.association.models.ActivityRequestModel;
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
public class ActivityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private transient JwtTokenVerifier mockJwtTokenVerifier;
    @Autowired
    private transient AssociationRepository associationRepository;
    @Autowired
    private transient ActivityService activityService;

    private HashSet<String> councilMembers;
    private Association association;
    private String userId;
    private int activityId;

    /**
     * Initialize the variables before each test.
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
        associationRepository.save(association);

        activityService.addActivity("name", "description", new Date(200), new Date(500), association.getId(), "a");
        activityService.addInterested(activityService.getNoticeBoard(association.getId()).get(0).getActivityId(), "a");
        activityService.addParticipating(activityService.getNoticeBoard(association.getId()).get(0).getActivityId(), "a");

        activityId = activityService.getNoticeBoard(association.getId()).get(0).getActivityId();

        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUserIdFromToken(anyString())).thenReturn("ExampleUser");
    }

    @Test
    public void testDisplayNoticeBoard() throws Exception {
        ResultActions result = mockMvc.perform(get("/activities/" + association.getId() + "/noticeboard")
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());
    }

    @Test
    public void testDisplayNoticeBoardWrongAssociationId() throws Exception {
        ResultActions result = mockMvc.perform(get("/activities/" + 500 + "/noticeboard")
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().is4xxClientError());
    }

    @Test
    public void testAddActivity() throws Exception {
        ActivityRequestModel model = new ActivityRequestModel("name", "description",
                new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis() + 10000L));

        ResultActions result = mockMvc.perform(post("/activities/" + association.getId() + "/b")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("Activity added");
    }

    @Test
    public void testAddActivityNotMember() throws Exception {
        ActivityRequestModel model = new ActivityRequestModel("name", "description",
                new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis() + 10000L));

        ResultActions result = mockMvc.perform(post("/activities/" + association.getId() + "/asdfasdf")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().is4xxClientError());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("You are not a member of this association.");
    }

    @Test
    public void testAddActivityNonCompleteModel() throws Exception {
        ActivityRequestModel model = new ActivityRequestModel(null, "description",
                new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis() + 10000L));

        ResultActions result = mockMvc.perform(post("/activities/" + association.getId() + "/b")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().is4xxClientError());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("Request not full");
    }

    @Test
    public void testAddActivityAssociationDoesNotExist() throws Exception {
        ActivityRequestModel model = new ActivityRequestModel("name", "description",
                new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis() + 10000L));

        ResultActions result = mockMvc.perform(post("/activities/" + 500 + "/b")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().is4xxClientError());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("Association does not exist.");
    }

    @Test
    public void testGetActivity() throws Exception {
        ResultActions result = mockMvc.perform(get("/activities/noticeboard/" + activityId)
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());
    }

    @Test
    public void testGetActivityWrongId() throws Exception {
        ResultActions result = mockMvc.perform(get("/activities/noticeboard/" + -294830)
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().is4xxClientError());
    }

    @Test
    public void testAddInterested() throws Exception {
        ResultActions result = mockMvc.perform(post("/activities/addInterested/" + activityId + "/c")
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("");
    }

    @Test
    public void testAddInterestedNoActivity() throws Exception {
        ResultActions result = mockMvc.perform(post("/activities/addInterested/" + -295038503 + "/c")
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().is4xxClientError());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("That activity does not exits for you to be interested in it.");
    }

    @Test
    public void testAddInterestedIsNotMember() throws Exception {
        ResultActions result = mockMvc.perform(post("/activities/addInterested/" + activityId + "/notMember")
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().is4xxClientError());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("You have to be a member of the association to be interested in the event.");
    }

    @Test
    public void testAddParticipating() throws Exception {
        ResultActions result = mockMvc.perform(post("/activities/addParticipating/" + activityId + "/c")
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("");
    }

    @Test
    public void testAddParticipatingNoActivity() throws Exception {
        ResultActions result = mockMvc.perform(post("/activities/addParticipating/" + -39593095 + "/c")
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().is4xxClientError());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("That activity does not exits for you to be participating in it.");
    }

    @Test
    public void testAddParticipatingIsNotMember() throws Exception {
        ResultActions result = mockMvc.perform(post("/activities/addParticipating/" + activityId + "/notMember")
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().is4xxClientError());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("You have to be a member of the association for participating in the event.");
    }

    @Test
    public void testRemoveInterested() throws Exception {
        ResultActions result = mockMvc.perform(post("/activities/removeInterested/" + activityId + "/a")
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("");
    }

    @Test
    public void testRemoveInterestedNoActivity() throws Exception {
        ResultActions result = mockMvc.perform(post("/activities/removeInterested/" + -653256456 + "/a")
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().is4xxClientError());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("That activity does not exits, for you to remove your interested reaction.");
    }

    @Test
    public void testRemoveInterestedNotMember() throws Exception {
        ResultActions result = mockMvc.perform(post("/activities/removeInterested/" + activityId + "/notMember")
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().is4xxClientError());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response)
                .isEqualTo("You have to be a member of this association, to be able to remove your interested reaction.");
    }

    @Test
    public void testRemoveParticipating() throws Exception {
        ResultActions result = mockMvc.perform(post("/activities/removeParticipating/" + activityId + "/a")
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("");
    }

    @Test
    public void testRemoveParticipatingNoActivity() throws Exception {
        ResultActions result = mockMvc.perform(post("/activities/removeParticipating/" + -653256456 + "/a")
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().is4xxClientError());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("That activity does not exits, for you to remove your participating reaction.");
    }

    @Test
    public void testRemoveParticipatingNotMember() throws Exception {
        ResultActions result = mockMvc.perform(post("/activities/removeParticipating/" + activityId + "/notMember")
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().is4xxClientError());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response)
                .isEqualTo("You have to be a member of this association to be able to remove your interested reaction.");
    }
}
