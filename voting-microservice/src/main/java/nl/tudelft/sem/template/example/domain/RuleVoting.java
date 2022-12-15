package nl.tudelft.sem.template.example.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import lombok.NoArgsConstructor;
import org.springframework.data.util.Pair;

@Entity
@Table(name = "rule_votings")
@NoArgsConstructor
public class RuleVoting extends Voting {

    @Column(name = "rule", nullable = false)
    private String rule;
    @Column(name = "votes", nullable = false)
    @Convert(converter = RuleVotingVotesAttributeConverter.class)
    private List<Pair<Integer, String>> votes;

    /**
     * Constructor for the RuleVoting object.
     *
     * @param rule The rule on which will be voted.
     */
    public RuleVoting(String rule) {
        super();
        this.rule = rule;
        this.votes = new ArrayList<>();
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