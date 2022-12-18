package nl.tudelft.sem.template.voting.domain;

public abstract class VotingService {
    public VotingService() {
    }

    public abstract void createVote();

    public abstract String castVote();

    public abstract String getResults();
}
