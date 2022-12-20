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

    /**
     * Constructor for the election object, with the option to set the end date X days from today.
     *
     * @param associationId The association id of the association the election is in.
     * @param dayOffset no. of days to move the end date from today.
     */
    public Election(int associationId, double dayOffset) {
        super();
        this.associationId = associationId;
        this.candidateIds = new HashSet<>();
        this.votes = new ArrayList<>();

        Calendar c = Calendar.getInstance();
        long dayInMs = 1000 * 60 * 60 * 24;
        this.setEndDate(new Date(System.currentTimeMillis() + (int) (dayOffset * dayInMs)));
        c.setTime(this.getEndDate());
        c.add(Calendar.YEAR, -1);
        this.setCreationDate(new Date(c.getTime().getTime()));
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
