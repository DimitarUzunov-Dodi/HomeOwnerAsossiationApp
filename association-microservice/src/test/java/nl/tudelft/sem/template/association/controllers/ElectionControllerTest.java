package nl.tudelft.sem.template.association.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.io.IOException;
import nl.tudelft.sem.template.association.domain.election.ElectionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

@ExtendWith(MockitoExtension.class)
class ElectionControllerTest {

    @Mock
    private ElectionService electionService;

    @InjectMocks
    private ElectionController electionController;

    public void testCreateElection(ResponseEntity<String> response) throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();

        when(electionService.createElection(request)).thenReturn(response);

        assertThat(electionController.createElection(request)).isEqualTo(response);

        verify(electionService, times(1)).createElection(request);
    }

    @Test
    public void normalCreateElection() throws IOException {
        testCreateElection(ResponseEntity.ok("test message"));
    }

    @Test
    public void badCreateElection() throws IOException {
        testCreateElection(ResponseEntity.badRequest().body("bad message"));
    }



    public void testGetCandidates(ResponseEntity<String> response) throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();

        when(electionService.getCandidates(request)).thenReturn(response);

        assertThat(electionController.getCandidates(request)).isEqualTo(response);

        verify(electionService, times(1)).getCandidates(request);
    }

    @Test
    public void normalGetCandidates() throws IOException {
        testGetCandidates(ResponseEntity.ok("test message"));
    }

    @Test
    public void badGetCandidates() throws IOException {
        testGetCandidates(ResponseEntity.badRequest().body("bad message"));
    }



    public void testApplyForCandidate(ResponseEntity<String> response) throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();

        when(electionService.applyForCandidate(request)).thenReturn(response);

        assertThat(electionController.applyForCandidate(request)).isEqualTo(response);

        verify(electionService, times(1)).applyForCandidate(request);
    }

    @Test
    public void normalApplyForCandidate() throws IOException {
        testApplyForCandidate(ResponseEntity.ok("test message"));
    }

    @Test
    public void badApplyForCandidate() throws IOException {
        testApplyForCandidate(ResponseEntity.badRequest().body("bad message"));
    }


    public void testCastVote(ResponseEntity<String> response) throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();

        when(electionService.castVote(request)).thenReturn(response);

        assertThat(electionController.castVote(request)).isEqualTo(response);

        verify(electionService, times(1)).castVote(request);
    }

    @Test
    public void normalCastVote() throws IOException {
        testCastVote(ResponseEntity.ok("test message"));
    }

    @Test
    public void badCastVote() throws IOException {
        testCastVote(ResponseEntity.badRequest().body("bad message"));
    }

}