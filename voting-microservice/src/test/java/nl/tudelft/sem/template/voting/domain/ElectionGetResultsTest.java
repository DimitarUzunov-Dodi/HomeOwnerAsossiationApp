package nl.tudelft.sem.template.voting.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.HashMap;
import nl.tudelft.sem.template.voting.domain.election.Election;
import nl.tudelft.sem.template.voting.domain.models.ElectionResultRequestModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class ElectionGetResultsTest {

    Election election;

    /**
     * Set up the tests.
     */
    @BeforeEach
    public void setup() {
        election = new Election(0);
        election.addVote(Pair.of(1, 2));
        election.addVote(Pair.of(2, 3));
        election.addVote(Pair.of(3, 1));
        election.addVote(Pair.of(4, 2));
        election.addVote(Pair.of(5, 2));

        for (int i = 1; i <= 5; i++) {
            election.addCandidate(i);
        }
    }

    @Test
    public void tallyVotesTest() {
        HashMap<Integer, Integer> hm = election.tallyVotes();

        assertThat(hm.get(2)).isEqualTo(3);
        assertThat(hm.get(3)).isEqualTo(1);
    }

    @Test
    public void getResultsTest() {
        assertThat(election.getResults()).isEqualTo("1=1, 2=3, 3=1, 4=0, 5=0");
    }

    /*@Test
    public void manuallyTestConnection() {
        Election election = new Election(0);
        election.addVote(Pair.of(1, 2));
        election.addVote(Pair.of(2, 3));
        election.addVote(Pair.of(3, 1));
        election.addVote(Pair.of(4, 2));
        election.addVote(Pair.of(5, 2));

        for (int i = 1; i <= 5; i++) {
            election.addCandidate(i);
        }

        System.out.println("Manual test executed at : " + new Date());

        ElectionResultRequestModel model = new ElectionResultRequestModel();
        model.setDate(new Date());
        model.setResult(election.getResults());

        final String url = "http://localhost:8084/association/update-council-dummy";
        RestTemplate restTemplate = new RestTemplate();

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer {your-access-token}");
            HttpEntity<ElectionResultRequestModel> request = new HttpEntity<>(model, headers);
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, request, String.class);

            if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
                System.out.println("Valid response! :)");
            } else {
                System.out.println("Invalid response! :(");
            }
        } catch (Exception e) {
            System.out.println("ERORR!!!!!!");
            System.out.println(e.getMessage());
        }
    }*/
}
