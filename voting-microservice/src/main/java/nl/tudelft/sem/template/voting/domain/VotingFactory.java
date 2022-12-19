package nl.tudelft.sem.template.voting.domain;

import nl.tudelft.sem.template.voting.domain.election.Election;
import nl.tudelft.sem.template.voting.domain.election.ElectionRepository;
import nl.tudelft.sem.template.voting.domain.rulevoting.RuleVoting;
import nl.tudelft.sem.template.voting.domain.rulevoting.RuleVotingRepository;

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
    public Voting createVoting(String type, int associationId, Integer userId, String rule, String amendment) {
        if (type.equals("Election")) {
            Election election = new Election(associationId);
            electionRepository.save(election);
            return election;
        } else {
            RuleVoting ruleVoting = new RuleVoting(userId, rule, amendment, type);
            ruleVotingRepository.save(ruleVoting);
            return ruleVoting;
        }
    }
}
