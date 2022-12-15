package nl.tudelft.sem.template.example.domain;

public abstract class VotingService {
    public VotingService() {
    }

    public abstract void createVote();

    public abstract boolean verify();

    public abstract String castVote();

    public abstract String getResults();
}
