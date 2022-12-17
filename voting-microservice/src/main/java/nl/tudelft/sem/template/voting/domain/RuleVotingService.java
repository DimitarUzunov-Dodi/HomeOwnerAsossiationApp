package nl.tudelft.sem.template.voting.domain;

public class RuleVotingService extends VotingService {

    private final transient RuleVotingRepository ruleVotingRepository;

    public RuleVotingService(RuleVotingRepository ruleVotingRepository) {
        this.ruleVotingRepository = ruleVotingRepository;
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
