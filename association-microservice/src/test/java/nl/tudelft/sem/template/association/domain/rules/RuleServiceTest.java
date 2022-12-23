package nl.tudelft.sem.template.association.domain.rules;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import nl.tudelft.sem.template.association.domain.association.AssociationService;
import nl.tudelft.sem.template.association.models.RuleAmendRequestModel;
import nl.tudelft.sem.template.association.models.RuleGetRequestModel;
import nl.tudelft.sem.template.association.models.RuleProposalRequestModel;
import nl.tudelft.sem.template.association.models.RuleVoteRequestModel;
import nl.tudelft.sem.template.association.utils.RequestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;


@SpringBootTest
class RuleServiceTest {

    @Autowired
    public AssociationService associationService;

    public RequestUtil requestUtil;

    public RuleService ruleService;

    String association;
    int associationId;
    String user1;
    String user2;

    @BeforeEach
    public void setUp() {
        association = associationService.createAssociation("name", "country", "city", "description", 10);

        associationId = Integer.parseInt(association.split(System.lineSeparator())[1].split(" ")[1]);

        user1 = associationService.joinAssociation("user1", associationId, "country", "city", "street", "number", "code");
        user2 = associationService.joinAssociation("user2", associationId, "country", "city", "street", "number", "code");

        associationService.updateCouncil(Set.of("user1"), associationId);
    }

    public void setUpRuleService() {
        ruleService = new RuleService(associationService, requestUtil);
    }

    public <T> void setUpPostRequest(HttpServletRequest request, String parameter, T model, Class<T> modelClass) {
        requestUtil = mock(RequestUtil.class);

        when(requestUtil.getToken(request)).thenReturn("token");

        try {
            when(requestUtil.convertToModel(eq(request), eq(modelClass))).thenReturn(model);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }

        when(requestUtil.postRequest(any(modelClass), eq(String.class), eq("token"), eq(8083), eq(parameter)))
                .thenReturn(ResponseEntity.ok("test"));
    }

    public void verifyPostRequest(String parameter, Class<?> modelClass) {
        verify(requestUtil, times(1)).postRequest(any(modelClass), eq(String.class), eq("token"), eq(8083), eq(parameter));
    }

    public <T> void setUpGetRequest(HttpServletRequest request, String parameter, T model, Class<T> modelClass) {
        requestUtil = mock(RequestUtil.class);

        when(requestUtil.getToken(request)).thenReturn("token");

        try {
            when(requestUtil.convertToModel(eq(request), eq(modelClass))).thenReturn(model);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }

        when(requestUtil.getRequest(any(modelClass), eq(String.class), eq("token"), eq(8083), eq(parameter)))
                .thenReturn(ResponseEntity.ok("withBody"));
        when(requestUtil.getRequest(eq(String.class), eq("token"), eq(8083), eq(parameter)))
                .thenReturn(ResponseEntity.ok("withoutBody"));
    }

    public void verifyGetRequest(boolean hasBody, String parameter, Class<?> modelClass) {
        if (hasBody) {
            verify(requestUtil, times(1))
                    .getRequest(any(modelClass), eq(String.class), eq("token"), eq(8083), eq(parameter));
        } else {
            verify(requestUtil, times(1))
                    .getRequest(eq(String.class), eq("token"), eq(8083), eq(parameter));
        }
    }


    @Test
    public void testVoteOn() throws IOException {
        final HttpServletRequest request = new MockHttpServletRequest();

        RuleVoteRequestModel model = new RuleVoteRequestModel();
        model.setAssociationId(associationId);
        model.setUserId("user1");
        model.setRule("testRule");

        setUpPostRequest(request, "rule-voting/vote-rule", model, RuleVoteRequestModel.class);
        setUpRuleService();

        assertThat(ruleService.voteOnRule(request)).isEqualTo(ResponseEntity.ok("test"));

        verifyPostRequest("rule-voting/vote-rule", RuleVoteRequestModel.class);
    }

    @Test
    public void testVoteOnNotMember() {
        final HttpServletRequest request = new MockHttpServletRequest();

        RuleVoteRequestModel model = new RuleVoteRequestModel();
        model.setAssociationId(associationId);
        model.setUserId("notAMember");
        model.setRule("testRule");

        setUpPostRequest(request, "rule-voting/vote-rule", model, RuleVoteRequestModel.class);
        setUpRuleService();

        assertThatThrownBy(() -> ruleService.voteOnRule(request)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testVoteOnNotCouncil() {
        final HttpServletRequest request = new MockHttpServletRequest();

        RuleVoteRequestModel model = new RuleVoteRequestModel();
        model.setAssociationId(associationId);
        model.setUserId("user2");
        model.setRule("testRule");

        setUpPostRequest(request, "rule-voting/vote-rule", model, RuleVoteRequestModel.class);
        setUpRuleService();

        assertThatThrownBy(() -> ruleService.voteOnRule(request)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testProposeRule() throws IOException {
        final HttpServletRequest request = new MockHttpServletRequest();

        RuleProposalRequestModel model = new RuleProposalRequestModel();
        model.setAssociationId(associationId);
        model.setUserId("user1");
        model.setRule("testRule");

        setUpPostRequest(request, "rule-voting/propose-rule", model, RuleProposalRequestModel.class);
        setUpRuleService();

        assertThat(ruleService.proposeRule(request)).isEqualTo(ResponseEntity.ok("test"));

        verifyPostRequest("rule-voting/propose-rule", RuleProposalRequestModel.class);
    }

    @Test
    public void testProposeRuleNotMember() {
        final HttpServletRequest request = new MockHttpServletRequest();

        RuleProposalRequestModel model = new RuleProposalRequestModel();
        model.setAssociationId(associationId);
        model.setUserId("notMember");
        model.setRule("testRule");

        setUpPostRequest(request, "rule-voting/propose-rule", model, RuleProposalRequestModel.class);
        setUpRuleService();

        assertThatThrownBy(() -> ruleService.proposeRule(request)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testProposeRuleNotCouncil() {
        final HttpServletRequest request = new MockHttpServletRequest();

        RuleProposalRequestModel model = new RuleProposalRequestModel();
        model.setAssociationId(associationId);
        model.setUserId("notCouncil");
        model.setRule("testRule");

        setUpPostRequest(request, "rule-voting/propose-rule", model, RuleProposalRequestModel.class);
        setUpRuleService();

        assertThatThrownBy(() -> ruleService.proposeRule(request)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testAmendRule() throws IOException {
        final HttpServletRequest request = new MockHttpServletRequest();

        RuleAmendRequestModel model = new RuleAmendRequestModel();
        model.setAssociationId(associationId);
        model.setUserId("user1");
        model.setRule("testRule");
        model.setAmendment("extra");

        setUpPostRequest(request, "rule-voting/amend-rule", model, RuleAmendRequestModel.class);
        setUpRuleService();

        assertThat(ruleService.amendRule(request)).isEqualTo(ResponseEntity.ok("test"));

        verifyPostRequest("rule-voting/amend-rule", RuleAmendRequestModel.class);
    }

    @Test
    public void testAmendRuleNotMember() {
        final HttpServletRequest request = new MockHttpServletRequest();

        RuleAmendRequestModel model = new RuleAmendRequestModel();
        model.setAssociationId(associationId);
        model.setUserId("notMember");
        model.setRule("testRule");
        model.setAmendment("extra");

        setUpPostRequest(request, "rule-voting/amend-rule", model, RuleAmendRequestModel.class);
        setUpRuleService();

        assertThatThrownBy(() -> ruleService.amendRule(request)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testAmendRuleNotCouncil() {
        final HttpServletRequest request = new MockHttpServletRequest();

        RuleAmendRequestModel model = new RuleAmendRequestModel();
        model.setAssociationId(associationId);
        model.setUserId("user2");
        model.setRule("testRule");
        model.setAmendment("extra");

        setUpPostRequest(request, "rule-voting/amend-rule", model, RuleAmendRequestModel.class);
        setUpRuleService();

        assertThatThrownBy(() -> ruleService.amendRule(request)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testGetRules() throws IOException {
        final HttpServletRequest request = new MockHttpServletRequest();

        RuleGetRequestModel model = new RuleGetRequestModel();
        model.setRuleVotingId(2958305830528593L);

        setUpGetRequest(request, "rule-voting/get-rule-vote", model, RuleGetRequestModel.class);
        setUpRuleService();

        assertThat(ruleService.getRules(request)).isEqualTo(ResponseEntity.ok("withBody"));

        verifyGetRequest(true, "rule-voting/get-rule-vote", RuleGetRequestModel.class);
    }

}