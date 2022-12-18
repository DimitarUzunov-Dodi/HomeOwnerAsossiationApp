package nl.tudelft.sem.template.voting.domain;


import java.util.*;
import javax.persistence.*;
import lombok.NoArgsConstructor;
import org.springframework.data.util.Pair;

@Entity
@Table(name = "elections")
@NoArgsConstructor
public class Election extends Voting {

    @Column(name = "association_id", nullable = false)
    private int associationId;

    @Column(name = "candidates")
    @Convert(converter = CandidateAttributeConverter.class)
    private Set<Integer> candidateIds;

    @Column(name = "votes")
    @Convert(converter = ElectionVotesAttributeConverter.class)
    private List<Pair<Integer, Integer>> votes;

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

    public Set<Integer> getCandidateIds() {
        return candidateIds;
    }

    public void addCandidate(int userId) {
        this.candidateIds.add(userId);
    }

    public List<Pair<Integer, Integer>> getVotes() {
        return votes;
    }

    public void addVote(Pair<Integer, Integer> vote) {
        votes.add(vote);
    }

}
