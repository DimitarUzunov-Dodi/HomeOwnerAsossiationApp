package nl.tudelft.sem.template.example.domain;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.NoArgsConstructor;
import org.springframework.data.util.Pair;

@Entity
@Table(name = "elections")
@NoArgsConstructor
public class Election extends Voting {

    @Column(name = "association_id", nullable = false)
    private int associationId;

    @Column(name = "candidates", nullable = false)
    @Convert(converter = CandidateAttributeConverter.class)
    private List<Integer> candidates;
    @Column(name = "votes", nullable = false)
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
        this.candidates = new ArrayList<>();
        this.votes = new ArrayList<>();
    }

    public int getAssociationId() {
        return associationId;
    }

    public List<Integer> getCandidates() {
        return candidates;
    }

    public List<Pair<Integer, Integer>> getVotes() {
        return votes;
    }

    public void addVote(Pair<Integer, Integer> vote) {
        votes.add(vote);
    }

}
