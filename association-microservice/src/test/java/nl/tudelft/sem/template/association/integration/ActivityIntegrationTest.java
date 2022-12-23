package nl.tudelft.sem.template.association.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;
import java.util.HashSet;
import nl.tudelft.sem.template.association.authentication.AuthManager;
import nl.tudelft.sem.template.association.authentication.JwtTokenVerifier;
import nl.tudelft.sem.template.association.domain.activity.ActivityService;
import nl.tudelft.sem.template.association.domain.association.Association;
import nl.tudelft.sem.template.association.domain.association.AssociationRepository;
import nl.tudelft.sem.template.association.integration.utils.JsonUtil;
import nl.tudelft.sem.template.association.models.ActivityRequestModel;
import nl.tudelft.sem.template.association.models.AddActivityRequestModel;
import nl.tudelft.sem.template.association.models.AssociationRequestModel;
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
    @Autowired
    private transient AuthManager authManager;

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
        when(mockJwtTokenVerifier.getUserIdFromToken(anyString())).thenReturn("a");
        when(authManager.getUserId()).thenReturn("a");
    }

    @Test
    public void testDisplayNoticeBoard() throws Exception {
        AssociationRequestModel model = new AssociationRequestModel();
        model.setAssociationId(association.getId());

        ResultActions result = mockMvc.perform(get("/activities/display-noticeboard")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());
    }

    @Test
    public void testDisplayNoticeBoardWrongAssociationId() throws Exception {
        AssociationRequestModel model = new AssociationRequestModel();
        model.setAssociationId(500);

        ResultActions result = mockMvc.perform(get("/activities/display-noticeboard")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().is4xxClientError());
    }

    @Test
    public void testAddActivity() throws Exception {
        AddActivityRequestModel model = new AddActivityRequestModel();
        model.setDescription("Description");
        model.setAssociationId(association.getId());
        model.setEventName("Event Name");
        model.setExpirationDate(new Date(System.currentTimeMillis() + 10000));
        model.setStartingDate(new Date(System.currentTimeMillis()));

        ResultActions result = mockMvc.perform(post("/activities/add-activity")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("Activity added");
    }

    @Test
    public void testAddActivityNotMember() throws Exception {
        AddActivityRequestModel model = new AddActivityRequestModel();
        model.setDescription("Description");
        model.setAssociationId(association.getId());
        model.setEventName("Event Name");
        model.setExpirationDate(new Date(System.currentTimeMillis() + 10000));
        model.setStartingDate(new Date(System.currentTimeMillis()));

        when(authManager.getUserId()).thenReturn("notMember");

        ResultActions result = mockMvc.perform(post("/activities/add-activity")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().is4xxClientError());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("You are not a member of this association.");
    }

    @Test
    public void testAddActivityNonCompleteModel() throws Exception {
        AddActivityRequestModel model = new AddActivityRequestModel();
        model.setDescription(null);
        model.setAssociationId(association.getId());
        model.setEventName("Event Name");
        model.setExpirationDate(new Date(System.currentTimeMillis() + 10000));
        model.setStartingDate(new Date(System.currentTimeMillis()));

        ResultActions result = mockMvc.perform(post("/activities/add-activity")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().is4xxClientError());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("Request not full");
    }

    @Test
    public void testAddActivityAssociationDoesNotExist() throws Exception {
        AddActivityRequestModel model = new AddActivityRequestModel();
        model.setDescription("description");
        model.setAssociationId(500);
        model.setEventName("Event Name");
        model.setExpirationDate(new Date(System.currentTimeMillis() + 10000));
        model.setStartingDate(new Date(System.currentTimeMillis()));

        ResultActions result = mockMvc.perform(post("/activities/add-activity")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().is4xxClientError());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("Association does not exist.");
    }

    @Test
    public void testGetActivity() throws Exception {
        ActivityRequestModel model = new ActivityRequestModel();
        model.setActivityId(activityId);

        ResultActions result = mockMvc.perform(get("/activities/get-activity")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());
    }

    @Test
    public void testGetActivityWrongId() throws Exception {
        ActivityRequestModel model = new ActivityRequestModel();
        model.setActivityId(-2850385);

        ResultActions result = mockMvc.perform(get("/activities/get-activity")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().is4xxClientError());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("That activity does not exits.");
    }

    @Test
    public void testAddInterested() throws Exception {
        ActivityRequestModel model = new ActivityRequestModel();
        model.setActivityId(activityId);

        ResultActions result = mockMvc.perform(post("/activities/add-interest")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("Interested in activity name");
    }

    @Test
    public void testAddInterestedNoActivity() throws Exception {
        ActivityRequestModel model = new ActivityRequestModel();
        model.setActivityId(-39503859);

        ResultActions result = mockMvc.perform(post("/activities/add-interest")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));


        result.andExpect(status().is4xxClientError());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("That activity does not exits for you to be interested in it.");
    }

    @Test
    public void testAddInterestedIsNotMember() throws Exception {
        ActivityRequestModel model = new ActivityRequestModel();
        model.setActivityId(activityId);

        when(authManager.getUserId()).thenReturn("notAMember");

        ResultActions result = mockMvc.perform(post("/activities/add-interest")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().is4xxClientError());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("You have to be a member of the association to be interested in the event.");
    }

    @Test
    public void testAddParticipating() throws Exception {
        ActivityRequestModel model = new ActivityRequestModel();
        model.setActivityId(activityId);

        ResultActions result = mockMvc.perform(post("/activities/add-participating")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("Participating in activity name");
    }

    @Test
    public void testAddParticipatingNoActivity() throws Exception {
        ActivityRequestModel model = new ActivityRequestModel();
        model.setActivityId(-92054839);

        ResultActions result = mockMvc.perform(post("/activities/add-participating")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().is4xxClientError());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("That activity does not exits for you to be participating in it.");
    }

    @Test
    public void testAddParticipatingIsNotMember() throws Exception {
        ActivityRequestModel model = new ActivityRequestModel();
        model.setActivityId(activityId);

        when(authManager.getUserId()).thenReturn("notAMember");

        ResultActions result = mockMvc.perform(post("/activities/add-participating")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().is4xxClientError());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("You have to be a member of the association for participating in the event.");
    }

    @Test
    public void testRemoveInterested() throws Exception {
        ActivityRequestModel model = new ActivityRequestModel();
        model.setActivityId(activityId);

        ResultActions result = mockMvc.perform(post("/activities/remove-interested")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("Not interested in activity name");
    }

    @Test
    public void testRemoveInterestedNoActivity() throws Exception {
        ActivityRequestModel model = new ActivityRequestModel();
        model.setActivityId(-3940385);

        ResultActions result = mockMvc.perform(post("/activities/remove-interested")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().is4xxClientError());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("That activity does not exits, for you to remove your interested reaction.");
    }

    @Test
    public void testRemoveInterestedNotMember() throws Exception {
        ActivityRequestModel model = new ActivityRequestModel();
        model.setActivityId(activityId);

        when(authManager.getUserId()).thenReturn("notAMember");

        ResultActions result = mockMvc.perform(post("/activities/remove-interested")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().is4xxClientError());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response)
                .isEqualTo("You have to be a member of this association, to be able to remove your interested reaction.");
    }

    @Test
    public void testRemoveParticipating() throws Exception {
        ActivityRequestModel model = new ActivityRequestModel();
        model.setActivityId(activityId);

        ResultActions result = mockMvc.perform(post("/activities/remove-participating")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("Not participating in activity name");
    }

    @Test
    public void testRemoveParticipatingNoActivity() throws Exception {
        ActivityRequestModel model = new ActivityRequestModel();
        model.setActivityId(-285938);

        ResultActions result = mockMvc.perform(post("/activities/remove-participating")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().is4xxClientError());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("That activity does not exits, for you to remove your participating reaction.");
    }

    @Test
    public void testRemoveParticipatingNotMember() throws Exception {
        ActivityRequestModel model = new ActivityRequestModel();
        model.setActivityId(activityId);

        when(authManager.getUserId()).thenReturn("notAMember");

        ResultActions result = mockMvc.perform(post("/activities/remove-participating")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().is4xxClientError());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response)
                .isEqualTo("You have to be a member of this association to be able to remove your interested reaction.");
    }
}
