package nl.tudelft.sem.template.voting.domain;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
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

    /**
     * Checks whether a certain user is part of the council.
     *
     * @param userId            The user's id.
     * @param councilMembers    The list of council members.
     * @return                  True if the user is part of the council.
     */
    public boolean verify(Integer userId, List<Integer> councilMembers) {
        if (userId == null || councilMembers == null) {
            return false;
        }
        for (Integer i : councilMembers) {
            if (i.equals(userId)) {
                return true;
            }
        }
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
