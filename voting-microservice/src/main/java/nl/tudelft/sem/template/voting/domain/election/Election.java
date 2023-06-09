package nl.tudelft.sem.template.voting.domain.election;

import java.util.*;
import javax.persistence.*;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.voting.domain.Voting;
import org.springframework.data.util.Pair;

@Entity
@Table(name = "elections")
@NoArgsConstructor
public class Election extends Voting {

    @Column(name = "association_id", nullable = false)
    private int associationId;

    @Column(name = "candidates")
    @Convert(converter = CandidateAttributeConverter.class)
    private Set<String> candidateIds;

    /**
     * Vote is a Pair of voterId, candidateId in this order.
     */
    @Column(name = "votes")
    @Convert(converter = ElectionVotesAttributeConverter.class)
    private List<Pair<String, String>> votes;

    /**
     * Constructor for the election object.
     *
     * @param associationId The association id of the association the election is in.
     */
    public Election(int associationId) {
        super();
        this.associationId = associationId;
        this.candidateIds = new HashSet<>();
        this.votes = new ArrayList<>();

        Calendar c = Calendar.getInstance();
        c.setTime(this.getCreationDate());
        c.add(Calendar.YEAR, 1);
        this.setEndDate(new Date(c.getTime().getTime()));
    }

    public int getAssociationId() {
        return associationId;
    }

    public Set<String> getCandidateIds() {
        return candidateIds;
    }

    public void addCandidate(String userId) {
        this.candidateIds.add(userId);
    }

    public List<Pair<String, String>> getVotes() {
        return votes;
    }

    public void addVote(Pair<String, String> vote) {
        votes.add(vote);
    }

    /**
     * Gets the result of this election then turns them into readable string.
     *
     * @return election results
     */
    public String getResults() {
        HashMap<String, Integer> hm = this.tallyVotes();
        String str = hm.toString();
        return str.substring(1, str.length() - 1);
    }


    /**
     * Tallies the votes from this election.
     *
     * @return HashMap containing each candidateId along with its respective number of votes
     */
    public HashMap<String, Integer> tallyVotes() {
        HashMap<String, Integer> res = new HashMap<>();

        for (String candidateId : this.candidateIds) {
            res.put(candidateId, 0);
        }

        for (Pair pair : this.votes) {
            String candidateId = (String) pair.getSecond();
            int candidateTally = (Integer) res.get(candidateId);
            res.put(candidateId, candidateTally + 1);
        }

        return res;
    }

}
