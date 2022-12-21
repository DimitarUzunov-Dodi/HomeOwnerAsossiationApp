package nl.tudelft.sem.template.association.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.io.IOException;
import nl.tudelft.sem.template.association.domain.rules.RuleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

@ExtendWith(MockitoExtension.class)
class RuleControllerTest {

    @Mock
    private RuleService ruleService;

    @InjectMocks
    private RuleController ruleController;



    public void testVoteOnRule(ResponseEntity<String> response) throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();

        when(ruleService.voteOnRule(request)).thenReturn(response);

        assertThat(ruleController.voteOnRule(request)).isEqualTo(response);

        verify(ruleService, times(1)).voteOnRule(request);
    }

    @Test
    public void normalTestVoteOnRule() throws IOException {
        ResponseEntity<String> response = ResponseEntity.ok("Test message");
        testVoteOnRule(response);
    }

    @Test
    public void badTestVoteOnRule() throws IOException {
        ResponseEntity<String> response = ResponseEntity.badRequest().body("Test message");
        testVoteOnRule(response);
    }



    public void testProposeRule(ResponseEntity<String> response) throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();

        when(ruleService.proposeRule(request)).thenReturn(response);

        assertThat(ruleController.proposeRule(request)).isEqualTo(response);

        verify(ruleService, times(1)).proposeRule(request);
    }

    @Test
    public void normalProposeRule() throws IOException {
        ResponseEntity<String> response = ResponseEntity.ok("Test message");
        testProposeRule(response);
    }

    @Test
    public void badProposeRule() throws IOException {
        ResponseEntity<String> response = ResponseEntity.badRequest().body("Test message");
        testProposeRule(response);
    }



    public void testAmendRule(ResponseEntity<String> response) throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();

        when(ruleService.amendRule(request)).thenReturn(response);

        assertThat(ruleController.amendRule(request)).isEqualTo(response);

        verify(ruleService, times(1)).amendRule(request);
    }

    @Test
    public void normalAmdendRule() throws IOException {
        ResponseEntity<String> response = ResponseEntity.ok("Test message");
        testAmendRule(response);
    }

    @Test
    public void badAmendRule() throws IOException {
        ResponseEntity<String> response = ResponseEntity.badRequest().body("Test message");
        testAmendRule(response);
    }



    public void testGetRules(ResponseEntity<String> response) throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();

        when(ruleService.getRules(request)).thenReturn(response);

        assertThat(ruleController.getRules(request)).isEqualTo(response);

        verify(ruleService, times(1)).getRules(request);
    }

    @Test
    public void normalGetRules() throws IOException {
        ResponseEntity<String> response = ResponseEntity.ok("Test message");
        testGetRules(response);
    }

    @Test
    public void badGetRules() throws IOException {
        ResponseEntity<String> response = ResponseEntity.badRequest().body("Test message");
        testGetRules(response);
    }
}