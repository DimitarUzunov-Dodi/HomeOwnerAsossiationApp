package nl.tudelft.sem.template.voting.domain.rulevoting;

import java.util.*;
import javax.persistence.*;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.voting.domain.Voting;
import nl.tudelft.sem.template.voting.domain.VotingType;
import org.springframework.data.util.Pair;

@Entity
@Table(name = "rule_votings")
@NoArgsConstructor
public class RuleVoting extends Voting {
    @Column(name = "association_id", nullable = false)
    private int associationId;
    @Column(name = "user_id", nullable = false)
    private int userId;
    @Column(name = "rule", nullable = false)
    private String rule;
    @Column(name = "amendment")
    private String amendment;
    @Column(name = "type", nullable = false)
    private VotingType type;
    @Column(name = "votes")
    @Convert(converter = RuleVotingVotesAttributeConverter.class)
    private List<Pair<Integer, String>> votes;

    /**
     * Constructor for the RuleVoting object.
     *
     * @param rule The rule on which will be voted.
     */
    public RuleVoting(int associationId, int userId, String rule, String amendment, VotingType type) {
        super();
        this.associationId = associationId;
        this.userId = userId;
        this.rule = rule;
        this.amendment = amendment;
        this.type = type;
        this.votes = new ArrayList<>();

        Calendar c = Calendar.getInstance();
        c.setTime(this.getCreationDate());
        c.add(Calendar.DATE, 14);
        this.setEndDate(new Date(c.getTime().getTime()));
    }

    public int getAssociationId() {
        return associationId;
    }

    public void setAssociationId(int associationId) {
        this.associationId = associationId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public String getAmendment() {
        return amendment;
    }

    public void setAmendment(String amendment) {
        this.amendment = amendment;
    }

    public VotingType getType() {
        return type;
    }

    public void setType(VotingType type) {
        this.type = type;
    }

    public List<Pair<Integer, String>> getVotes() {
        return votes;
    }

    public void addVote(Pair<Integer, String> vote) {
        votes.add(vote);
    }

}
