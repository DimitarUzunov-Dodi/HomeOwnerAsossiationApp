package nl.tudelft.sem.template.voting.domain;

public class VotingFactory {
    private final transient ElectionRepository electionRepository;
    private final transient RuleVotingRepository ruleVotingRepository;

    public VotingFactory(ElectionRepository electionRepository, RuleVotingRepository ruleVotingRepository) {
        this.electionRepository = electionRepository;
        this.ruleVotingRepository = ruleVotingRepository;
    }

    /**
     * Creates an Election or a Rule Voting for an association based on the parameters.
     * Saves the Voting to the database.
     *
     * @return the created Voting object.
     */
    public Voting createVoting(boolean isElection, int associationId, String rule, String amendment) {
        if (isElection) {
            Election election = new Election(associationId);
            electionRepository.save(election);
            return election;
        } else {
            RuleVoting ruleVoting = new RuleVoting(rule, amendment);
            ruleVotingRepository.save(ruleVoting);
            return ruleVoting;
        }
    }
}
