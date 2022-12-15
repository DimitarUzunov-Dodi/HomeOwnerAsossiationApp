package nl.tudelft.sem.template.example.domain;

import lombok.NoArgsConstructor;
import org.springframework.data.util.Pair;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rule_votings")
@NoArgsConstructor
public class RuleVoting extends Voting {

    @Column(name = "rule", nullable = false)
    private String rule;
    @Column(name = "votes", nullable = false)
    @Convert(converter = RuleVoteVotesAttributeConverter.class)
    private List<Pair<Integer, String>> votes;

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

    public String proposeRule() {
        return null;
    }

    @Override
    public void createVote() {

    }

    @Override
    public boolean verify() {
        return false;
    }

    @Override
    public String castVote() {
        return null;
    }

    @Override
    public String getResults() {
        return null;
    }
}
