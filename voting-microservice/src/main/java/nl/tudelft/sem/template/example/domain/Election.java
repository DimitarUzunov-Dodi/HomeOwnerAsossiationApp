package nl.tudelft.sem.template.example.domain;

import lombok.NoArgsConstructor;
import org.springframework.data.util.Pair;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "elections")
@NoArgsConstructor
public class Election extends Voting {

    @Column(name = "association_id", nullable = false)
    private int associationID;

    @Column(name = "candidates", nullable = false)
    @Convert(converter = CandidateAttributeConverter.class)
    private List<Integer> candidates;
    @Column(name = "votes", nullable = false)
    @Convert(converter = ElectionVotesAttributeConverter.class)
    private List<Pair<Integer, Integer>> votes;

    public Election(int associationID) {
        super();
        this.associationID = associationID;
        this.candidates = new ArrayList<>();
        this.votes = new ArrayList<>();
    }

    public int getAssociationID() {
        return associationID;
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
