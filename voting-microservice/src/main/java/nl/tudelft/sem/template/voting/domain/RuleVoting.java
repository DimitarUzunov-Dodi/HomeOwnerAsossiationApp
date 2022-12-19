package nl.tudelft.sem.template.voting.domain;

import java.util.*;
import javax.persistence.*;
import lombok.NoArgsConstructor;
import org.springframework.data.util.Pair;

@Entity
@Table(name = "rule_votings")
@NoArgsConstructor
public class RuleVoting extends Voting {

    @Column(name = "rule", nullable = false)
    private String rule;
    private String amendment;
    @Column(name = "votes")
    @Convert(converter = RuleVotingVotesAttributeConverter.class)
    private List<Pair<Integer, String>> votes;

    /**
     * Constructor for the RuleVoting object.
     *
     * @param rule The rule on which will be voted.
     */
    public RuleVoting(String rule, String amendment) {
        super();
        this.rule = rule;
        this.amendment = amendment;
        this.votes = new ArrayList<>();

        Calendar c = Calendar.getInstance();
        c.setTime(this.getCreationDate());
        c.add(Calendar.DATE, 14);
        this.setEndDate(new Date(c.getTime().getTime()));
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public List<Pair<Integer, String>> getVotes() {
        return votes;
    }

    public void addVote(Pair<Integer, String> vote) {
        votes.add(vote);
    }

}
