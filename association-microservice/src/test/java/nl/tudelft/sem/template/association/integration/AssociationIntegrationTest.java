package nl.tudelft.sem.template.association.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.*;
import nl.tudelft.sem.template.association.authentication.AuthManager;
import nl.tudelft.sem.template.association.authentication.JwtTokenVerifier;
import nl.tudelft.sem.template.association.domain.association.Association;
import nl.tudelft.sem.template.association.domain.association.AssociationRepository;
import nl.tudelft.sem.template.association.domain.history.Event;
import nl.tudelft.sem.template.association.domain.history.History;
import nl.tudelft.sem.template.association.domain.history.HistoryRepository;
import nl.tudelft.sem.template.association.domain.location.Address;
import nl.tudelft.sem.template.association.domain.location.Location;
import nl.tudelft.sem.template.association.domain.membership.Membership;
import nl.tudelft.sem.template.association.domain.membership.MembershipRepository;
import nl.tudelft.sem.template.association.integration.utils.JsonUtil;
import nl.tudelft.sem.template.association.models.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles({"test", "mockTokenVerifier", "mockAuthenticationManager"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class AssociationIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private transient AuthManager authManager;
    @Autowired
    private transient JwtTokenVerifier mockJwtTokenVerifier;
    @Autowired
    private transient AssociationRepository mockAssociationRepository;
    @Autowired
    private transient MembershipRepository membershipRepository;
    @Autowired
    private transient HistoryRepository mockHistoryRepository;

    private static MockedConstruction<RestTemplate> restTemplateConstruction;

    private HashSet<String> councilMembers;
    private Association association;
    private History history;
    private String userId;

    /**
     * Setup for the static mocking.
     */
    @BeforeAll
    public static void setUpAll() {
        AuthenticationResponseModel authModel = new AuthenticationResponseModel();
        authModel.setToken("mockedToken");

        ResponseEntity authResponse = mock(ResponseEntity.class);
        when(authResponse.getBody()).thenReturn(authModel);

        setUpRestTemplateConstruction((mock, context) -> {
            when(mock.postForEntity(eq("http://localhost:8081/authenticate"), any(HttpEntity.class), eq(AuthenticationResponseModel.class))).thenReturn(authResponse);
            when(mock.postForEntity(eq("http://localhost:8083/election/create-election"), any(HttpEntity.class), eq(String.class))).thenReturn(ResponseEntity.ok("Some return string"));
        });
    }

    /**
     * Break down of the static mocks.
     */
    @AfterAll
    public static void tearDownAll() {
        if (restTemplateConstruction != null) {
            restTemplateConstruction.close();
            restTemplateConstruction = null;
        }
    }

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
        this.association = new Association("test", new Location("test", "test"), "test", 10);
        this.association.setCouncilNumber(3);
        this.association.setCouncilUserIds(this.councilMembers);
        this.association.addMember("a");
        this.association.addMember("b");
        this.association.addMember("c");
        this.association.addMember("d");
        mockAssociationRepository.save(this.association);

        Date date = new Date(0);

        Membership membership1 = new Membership("a", association.getId(),
                new Address(new Location("test", "test"), "test", "test", "test"));
        membership1.setJoinDate(date);
        membershipRepository.save(membership1);

        Membership membership2 = new Membership("b", association.getId(),
                new Address(new Location("test", "test"), "test", "test", "test"));
        membership2.setJoinDate(date);
        membershipRepository.save(membership2);

        Membership membership3 = new Membership("c", association.getId(),
                new Address(new Location("test", "test"), "test", "test", "test"));
        membership3.setTimesCouncil(10);
        membership3.setJoinDate(date);
        membershipRepository.save(membership3);

        Membership membership4 = new Membership("d", association.getId(),
                new Address(new Location("test", "test"), "test", "test", "test"));
        membershipRepository.save(membership4);

        history = new History(association.getId());
        mockHistoryRepository.save(history);

        when(authManager.validateRequestUser(userId)).thenReturn(true);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUserIdFromToken(anyString())).thenReturn(userId);
        when(authManager.validateRequestUser("d")).thenReturn(true);
    }

    public static void setUpRestTemplateConstruction(MockedConstruction.MockInitializer<RestTemplate>
                                                             restTemplateInitializer) {
        restTemplateConstruction = mockConstruction(RestTemplate.class, restTemplateInitializer);
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

        assertThat(response).isEqualTo("User passed council member check!");
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

        assertThat(response).isEqualTo("User is not a member of this association's council!");
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

        assertThat(response).isEqualTo("User is not a member of this association's council!");
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

        assertThat(response).isEqualTo("User is not a member of this association's council!");
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

        assertThat(response).isEqualTo("User is not a member of this association's council!");
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
                + "Name: name" + System.lineSeparator()
                + "Country: country" + System.lineSeparator()
                + "City: city" + System.lineSeparator()
                + "Description: description" + System.lineSeparator() + "Max council members: 5");
    }

    @Test
    public void testJoinAssociation() throws Exception {
        JoinAssociationRequestModel model = new JoinAssociationRequestModel();
        model.setUserId("d");
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

        assertThat(response).isEqualTo("User d successfully joined association " + association.getId());
    }

    @Test
    public void testJoinAssociationWrongAssociationId() throws Exception {
        JoinAssociationRequestModel model = new JoinAssociationRequestModel();
        model.setUserId("d");
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
        model.setUserId("d");
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

        assertThat(response).isEqualTo("User d left association " + association.getId());
    }

    @Test
    public void testLeaveAssociationWrongAssociationId() throws Exception {
        JoinAssociationRequestModel model = new JoinAssociationRequestModel();
        model.setUserId("d");
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
        model.setUserId("a");

        ResultActions result = mockMvc.perform(post("/association/verify-candidate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("User can apply for a candidate!");
    }

    @Test
    public void testVerifyCandidateNot() throws Exception {
        UserAssociationRequestModel model = new UserAssociationRequestModel();
        model.setAssociationId(association.getId());
        model.setUserId("d");

        ResultActions result = mockMvc.perform(post("/association/verify-candidate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().is4xxClientError());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("User cannot be a candidate for the council.");
    }

    @Test
    public void verifyAuthenticationJoinAssociation() throws Exception {
        JoinAssociationRequestModel model = new JoinAssociationRequestModel();

        model.setUserId("evilUser");
        model.setAssociationId(0);

        ResultActions result = mockMvc.perform(post("/association/join-association")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isUnauthorized());

        String response = result.andReturn().getResponse().getErrorMessage();

        assertThat(response).isEqualTo("401 UNAUTHORIZED \"INVALID_CREDENTIALS\"");

    }

    @Test
    public void verifyAuthenticationJoinAssociationSuccess() throws Exception {
        JoinAssociationRequestModel model = new JoinAssociationRequestModel();

        model.setUserId("d");
        model.setAssociationId(1);
        model.setCity(association.getLocation().getCity());
        model.setCountry(association.getLocation().getCountry());
        model.setStreet("mine");
        model.setHouseNumber("0");
        model.setPostalCode("0");

        ResultActions result = mockMvc.perform(post("/association/join-association")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("User d successfully joined association 1");

    }


    @Test
    public void verifyAuthenticationLeaveAssociation() throws Exception {
        UserAssociationRequestModel model = new UserAssociationRequestModel();

        model.setUserId("evilUser");
        model.setAssociationId(0);

        ResultActions result = mockMvc.perform(post("/association/leave-association")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isUnauthorized());

        String response = result.andReturn().getResponse().getErrorMessage();

        assertThat(response).isEqualTo("401 UNAUTHORIZED \"INVALID_CREDENTIALS\"");
    }

    @Test
    public void verifyAuthenticationReport() throws Exception {
        ReportModel model = new ReportModel();

        model.setReporterId("evilUser");
        model.setAssociationId(0);

        ResultActions result = mockMvc.perform(post("/association/report")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isUnauthorized());

        String response = result.andReturn().getResponse().getErrorMessage();

        assertThat(response).isEqualTo("401 UNAUTHORIZED \"INVALID_CREDENTIALS\"");

    }

    @Test
    public void verifyReport() throws Exception {
        ReportModel model = new ReportModel();

        model.setReporterId("evilUser");
        model.setAssociationId(0);

        ResultActions result = mockMvc.perform(post("/association/report")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isUnauthorized());

        String response = result.andReturn().getResponse().getErrorMessage();

        assertThat(response).isEqualTo("401 UNAUTHORIZED \"INVALID_CREDENTIALS\"");

    }

    @Test
    public void updateCouncilTest() throws Exception {
        Location location = new Location("test", "test");
        Address address = new Address(location, "test", "test", "test");

        Membership member = new Membership("f", association.getId(), address);
        member.setJoinDate(new Date(0));
        membershipRepository.save(member);

        HashMap<String, Integer> hm = new HashMap<>();
        hm.put("a", 7);
        hm.put("b", 5);
        hm.put("c", 25);
        hm.put("d", 1);
        hm.put("e", 15);
        hm.put("f", 20);

        // c and e shouldn't be council members
        // therefore, f, a, b

        ElectionResultRequestModel model = new ElectionResultRequestModel();

        String resultString = "-TestResult-";

        model.setStandings(hm);
        model.setResult(resultString);
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

        assertThat(testAssociation.getCouncilUserIds()).containsExactlyInAnyOrder("a", "b", "f");

        Optional<History> history = mockHistoryRepository.findByAssociationId(association.getId());
        List<Event> events = history.get().getEvents();
        assertThat(events.get(1).getDescription()).isEqualTo(resultString);
    }

    @Test
    public void updateCouncilTestWrongAssociationId() throws Exception {
        Location location = new Location("test", "test");
        Address address = new Address(location, "test", "test", "test");

        Membership member = new Membership("f", association.getId(), address);
        member.setJoinDate(new Date(0));
        membershipRepository.save(member);

        HashMap<String, Integer> hm = new HashMap<>();
        hm.put("a", 7);
        hm.put("b", 5);
        hm.put("c", 25);
        hm.put("d", 1);
        hm.put("e", 15);
        hm.put("f", 20);

        // c and e shouldn't be council members
        // therefore, f, a, b

        ElectionResultRequestModel model = new ElectionResultRequestModel();

        String resultString = "-TestResult-";

        model.setStandings(hm);
        model.setResult(resultString);
        model.setDate(new Date());
        model.setAssociationId(-53562);

        ResultActions result = mockMvc.perform(post("/association/update-council")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().is4xxClientError());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("Adding the log in history failed!");

    }

    @Test
    public void addRulePassTest() throws Exception {
        Location location = new Location("test", "test");
        Address address = new Address(location, "test", "test", "test");
        Membership member = new Membership("test", association.getId(), address);
        membershipRepository.save(member);

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
        assertThat(response).isEqualTo("Rules updated and all members have been notified!");

        Optional<Association> optionalTestAssociation = mockAssociationRepository.findById(association.getId());

        Association testAssociation = optionalTestAssociation.get();

        assertThat(testAssociation.getRules()).contains("Epic rule. HAH!");
    }

    @Test
    public void changeRulePassTest() throws Exception {
        Location location = new Location("test", "test");
        Address address = new Address(location, "test", "test", "test");
        Membership member = new Membership("test", association.getId(), address);
        membershipRepository.save(member);

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
        assertThat(response).isEqualTo("Rules updated and all members have been notified!");

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
        Location location = new Location("test", "test");
        Address address = new Address(location, "test", "test", "test");
        Membership member = new Membership("test", association.getId(), address);
        membershipRepository.save(member);

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

    @Test
    public void getAssociationIds() throws Exception {
        ResultActions result = mockMvc.perform(get("/association/get-association-ids")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("[" + association.getId() + "]");
    }

    @Test
    public void getAssociation() throws Exception {
        AssociationRequestModel model = new AssociationRequestModel();
        model.setAssociationId(association.getId());

        ResultActions result = mockMvc.perform(get("/association/get-association")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).contains(association.getDescription());
        assertThat(response).contains(association.getCouncilUserIds());
        assertThat(response).contains(association.getName());
        assertThat(response).contains(association.getLocation().getCity());
        assertThat(response).contains(association.getLocation().getCountry());
    }

    @Test
    public void getAssociationWrongId() throws Exception {
        AssociationRequestModel model = new AssociationRequestModel();
        model.setAssociationId(-4532);

        ResultActions result = mockMvc.perform(get("/association/get-association")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().is4xxClientError());

        String response = result.andReturn().getResponse().getErrorMessage();

        assertThat(response).isEqualTo("Association with ID -4532 does not exist.");
    }

    @Test
    public void verifyProposal() throws Exception {
        this.userId = "a";

        AssociationProposalRequestModel model = new AssociationProposalRequestModel();
        model.setAssociationId(association.getId());
        model.setProposal("Test proposal");

        ResultActions result = mockMvc.perform(post("/association/verify-proposal")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("true");
    }

    @Test
    public void verifyProposalWrongAssociationId() throws Exception {
        this.userId = "a";

        AssociationProposalRequestModel model = new AssociationProposalRequestModel();
        model.setAssociationId(-545255);
        model.setProposal("Test proposal");

        ResultActions result = mockMvc.perform(post("/association/verify-proposal")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().is4xxClientError());

        String response = result.andReturn().getResponse().getErrorMessage();

        assertThat(response).isEqualTo("Could not verify the proposal.");
    }

}
