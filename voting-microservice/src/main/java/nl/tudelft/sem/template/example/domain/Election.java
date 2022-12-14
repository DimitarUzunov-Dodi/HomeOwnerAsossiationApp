package nl.tudelft.sem.template.example.domain;

import org.springframework.data.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class Election extends Voting {

    private int associationID;
    private List<Integer> candidates;
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

    public boolean applyForCandidate() {
        return false;
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
