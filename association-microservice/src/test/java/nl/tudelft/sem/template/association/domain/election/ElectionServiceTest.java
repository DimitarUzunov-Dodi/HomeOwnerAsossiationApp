package nl.tudelft.sem.template.association.domain.election;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

import java.io.IOException;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import nl.tudelft.sem.template.association.domain.association.AssociationService;
import nl.tudelft.sem.template.association.models.ElectionVoteRequestModel;
import nl.tudelft.sem.template.association.models.RuleVoteRequestModel;
import nl.tudelft.sem.template.association.models.UserAssociationRequestModel;
import nl.tudelft.sem.template.association.utils.RequestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

@SpringBootTest
class ElectionServiceTest {

    @SpyBean
    public AssociationService associationService;

    public RequestUtil requestUtil;

    public ElectionService electionService;

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

    public void setUpElectionService() {
        electionService = new ElectionService(associationService, requestUtil);
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
    public void testApplyForCandidate() throws IOException {
        final HttpServletRequest request = new MockHttpServletRequest();

        UserAssociationRequestModel model = new UserAssociationRequestModel();
        model.setAssociationId(associationId);
        model.setUserId("user1");

        doReturn(true).when(associationService).verifyCandidate("user1", associationId);
        doReturn(false).when(associationService).verifyCandidate("user2", associationId);

        setUpPostRequest(request, "apply-for-candidate", model, UserAssociationRequestModel.class);
        setUpElectionService();

        assertThat(electionService.applyForCandidate(request)).isEqualTo(ResponseEntity.ok("test"));

        verifyPostRequest("apply-for-candidate", UserAssociationRequestModel.class);
    }

    @Test
    public void testApplyForCandidateNotMember() {
        final HttpServletRequest request = new MockHttpServletRequest();

        UserAssociationRequestModel model = new UserAssociationRequestModel();
        model.setAssociationId(associationId);
        model.setUserId("notAMember");

        doReturn(true).when(associationService).verifyCandidate("user1", associationId);
        doReturn(false).when(associationService).verifyCandidate("user2", associationId);

        setUpPostRequest(request, "apply-for-candidate", model, UserAssociationRequestModel.class);
        setUpElectionService();

        assertThatThrownBy(() -> electionService.applyForCandidate(request)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testApplyForCandidateNotCouncil() {
        final HttpServletRequest request = new MockHttpServletRequest();

        UserAssociationRequestModel model = new UserAssociationRequestModel();
        model.setAssociationId(associationId);
        model.setUserId("user2");

        doReturn(true).when(associationService).verifyCandidate("user1", associationId);
        doReturn(false).when(associationService).verifyCandidate("user2", associationId);

        setUpPostRequest(request, "apply-for-candidate", model, UserAssociationRequestModel.class);
        setUpElectionService();

        assertThatThrownBy(() -> electionService.applyForCandidate(request)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testCastVote() throws IOException {
        final HttpServletRequest request = new MockHttpServletRequest();

        ElectionVoteRequestModel model = new ElectionVoteRequestModel();
        model.setVoterId("user1");
        model.setAssociationId(associationId);
        model.setCandidateId("user2");

        setUpPostRequest(request, "apply-for-candidate", model, ElectionVoteRequestModel.class);
        setUpElectionService();

        assertThat(electionService.castVote(request)).isEqualTo(ResponseEntity.ok("test"));

        verifyPostRequest("apply-for-candidate", ElectionVoteRequestModel.class);
    }

    @Test
    public void testCastVoteVoterNotMember() {
        final HttpServletRequest request = new MockHttpServletRequest();

        ElectionVoteRequestModel model = new ElectionVoteRequestModel();
        model.setVoterId("notMember");
        model.setAssociationId(associationId);
        model.setCandidateId("user2");

        setUpPostRequest(request, "apply-for-candidate", model, ElectionVoteRequestModel.class);
        setUpElectionService();

        assertThatThrownBy(() -> electionService.castVote(request)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testCastVoteCandidateNotMember() {
        final HttpServletRequest request = new MockHttpServletRequest();

        ElectionVoteRequestModel model = new ElectionVoteRequestModel();
        model.setVoterId("user1");
        model.setAssociationId(associationId);
        model.setCandidateId("notMember");

        setUpPostRequest(request, "apply-for-candidate", model, ElectionVoteRequestModel.class);
        setUpElectionService();

        assertThatThrownBy(() -> electionService.castVote(request)).isInstanceOf(IllegalArgumentException.class);
    }

}