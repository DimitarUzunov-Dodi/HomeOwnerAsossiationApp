package nl.tudelft.sem.template.association.domain.association;

import java.time.temporal.ChronoUnit;
import java.util.*;
import nl.tudelft.sem.template.association.domain.membership.Membership;
import nl.tudelft.sem.template.association.domain.membership.MembershipRepository;
import nl.tudelft.sem.template.association.models.ElectionResultRequestModel;
import nl.tudelft.sem.template.association.models.RuleVoteResultRequestModel;
import org.springframework.stereotype.Service;

@Service
public class AssociationService {
    private final transient AssociationRepository associationRepository;
    private final transient MembershipRepository membershipRepository;

    public AssociationService(AssociationRepository associationRepository, MembershipRepository membershipRepository) {
        this.associationRepository = associationRepository;
        this.membershipRepository = membershipRepository;
    }

    /**
     * Creates an association.
     *
     * @return a message confirming the creation.
     */
    public String createAssociation(String name, String country, String city, String description,
                                    int councilNumber) {
        Association association = new Association(name, country, city, description, councilNumber);
        int associationId = associationRepository.save(association).getId();
        return "Association was created:" + System.lineSeparator() + "ID: " + associationId + System.lineSeparator()
                + "Name: " + name + System.lineSeparator() + "Country: " + country + System.lineSeparator()
                + "Description: " + description + System.lineSeparator() + "Max council members: " + councilNumber;
    }

    /**getter.
     *
     * @param associationId the id of the association
     * @return Association correspondingly
     */
    public Association getAssociationById(int associationId) {
        Optional<Association> optionalAssociation = associationRepository.findById(associationId);
        if (optionalAssociation.isPresent()) {
            return optionalAssociation.get();
        } else {
            throw new IllegalArgumentException("Association with ID "
                    + associationId + " does not exist.");
        }
    }

    /**
     * User joins an association.
     *
     * @return a message confirming the join.
     */
    public String joinAssociation(String userId, int associationId, String country, String city, String street,
                                  String houseNumber, String postalCode) {
        Optional<Association> optionalAssociation = associationRepository.findById(associationId);
        if (optionalAssociation.isEmpty()) {
            throw new IllegalArgumentException("Association with ID " + associationId + " does not exist.");
        }
        Association association = optionalAssociation.get();

        //Check if user has an address in the right city and country
        if (!association.getCity().equals(city) || !association.getCountry().equals(country)) {
            throw new IllegalArgumentException("You don't live in the right city or country to join this association.");
        }

        association.addMember(userId);
        Membership membership = new Membership(userId, associationId, country, city, street, houseNumber, postalCode);
        associationRepository.save(association);
        membershipRepository.save(membership);

        return "User " + userId + " successfully joined association " + associationId;
    }

    /**
     * User leaves an association.
     *
     * @return a message confirming the leave.
     */
    public String leaveAssociation(String userId, int associationId) {
        Optional<Association> optionalAssociation = associationRepository.findById(associationId);
        Optional<Membership> optionalMembership = membershipRepository
                .findByUserIdAndAssociationIdAndLeaveDate(userId, associationId, null);
        if (optionalAssociation.isEmpty() || optionalMembership.isEmpty()) {
            throw new IllegalArgumentException("Association/membership does not exist.");
        }

        Association association = optionalAssociation.get();
        association.removeMember(userId);
        Membership membership = optionalMembership.get();
        membership.leave();
        associationRepository.save(association);
        membershipRepository.save(membership);
        return "User " + userId + " left association " + associationId;
    }

    /**
     * Updates the council in a specific association.
     *
     * <p>Checks for the following things:
     *  - the Association exists
     *  - the council size is allows
     *  - each council member is part of the association
     *
     * @param council the new council
     * @param associationId the id of the association
     */
    public void updateCouncil(Set<String> council, int associationId) {
        Optional<Association> optionalAssociation = associationRepository.findById(associationId);
        if (optionalAssociation.isEmpty()) {
            throw new IllegalArgumentException("Association does not exist");
        }

        Association association = optionalAssociation.get();

        if (council.size() > association.getCouncilNumber()) {
            throw new IllegalArgumentException("Council is bigger than allowed");
        }

        for (String councilMember : council) {
            if (!association.getMemberUserIds().contains(councilMember)) {
                throw new IllegalArgumentException("A Council member is not part of the association");
            }
        }

        association.setCouncilUserIds(council);
        associationRepository.save(association);
    }

    /**
     * Returns the council for a specific association.
     *
     * @param associationId the id of the association
     * @return the council
     */
    public Set<String> getCouncil(int associationId) {
        Optional<Association> optionalAssociation = associationRepository.findById(associationId);
        if (optionalAssociation.isEmpty()) {
            throw new IllegalArgumentException("Association does not exist");
        }

        Association association = optionalAssociation.get();

        return association.getCouncilUserIds();
    }

    /**
     * Checks whether a certain user is part of the association's council.
     *
     * @param userId            The user's id.
     * @param associationId     The association id.
     * @return                  True if the user is part of the association's council.
     */
    public boolean verifyCouncilMember(String userId, Integer associationId) {
        if (userId == null || associationId == null) {
            return false;
        }

        Optional<Association> optionalAssociation = associationRepository.findById(associationId);
        if (optionalAssociation.isPresent()) {
            Set<String> councilMembers = optionalAssociation.get().getCouncilUserIds();
            for (String s : councilMembers) {
                if (s.equals(userId)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Processes all the information received about a past election
     * and updates the association's council.
     *
     * @param model model containing all important info pertaining
     *              to a past election
     */
    public void processElection(ElectionResultRequestModel model) {
        Optional<Association> optionalAssociation = associationRepository.findById(model.getAssociationId());

        if (optionalAssociation.isPresent()) {
            Association association = optionalAssociation.get();

            HashMap hm = model.getStandings();

            List<Map.Entry<String, Integer>> list = new LinkedList<>(hm.entrySet());

            Collections.sort(list, (e1, e2) -> -(e1.getValue().compareTo(e2.getValue())));

            Set<String> council = new HashSet<>();

            for (int i = 0; i < list.size() && i < association.getCouncilNumber(); i++) {
                council.add(list.get(i).getKey());
            }

            association.setCouncilUserIds(council);
            associationRepository.save(association);
        }
    }

    /**
     * Processes all the information received about a past rule vote
     * and updates the association's rule set by either updating a rule
     * or adding it.
     *
     * @param model model containing all important info pertaining
     *              to a past rule vote
     */
    public void processRuleVote(RuleVoteResultRequestModel model) {
        Optional<Association> optionalAssociation = associationRepository.findById(model.getAssociationId());

        if (optionalAssociation.isPresent() && model.isPassed()) {
            Association association = optionalAssociation.get();

            List<String> rulesCopy = association.getRules();
            List<String> rules = new ArrayList<>();

            for (String str : rulesCopy) {
                if (!str.isEmpty()) {
                    rules.add(str);
                }
            }

            if (model.isAnAmendment()) {
                int index = rules.indexOf(model.getRule());
                rules.set(index, model.getAmendment());
            } else {
                rules.add(model.getRule());
            }

            association.setRules(rules);
            associationRepository.save(association);
        }
    }

    /**
     * Return a string consisting of all the association's rules.
     *
     * @param userId                the user's ID
     * @param associationId         the association's ID
     * @return                      the association's rules
     */
    public String getAssociationRules(String userId, int associationId) {
        Optional<Association> optionalAssociation = associationRepository.findById(associationId);
        Optional<Membership> optionalMembership = membershipRepository
                .findByUserIdAndAssociationIdAndLeaveDate(userId, associationId, null);
        if (optionalAssociation.isEmpty() || optionalMembership.isEmpty()) {
            throw new IllegalArgumentException("Association/membership does not exist.");
        }

        Association association = optionalAssociation.get();
        List<String> rules = association.getRules();

        StringBuilder sb = new StringBuilder();

        for (String str : rules) {
            sb.append(str).append(System.lineSeparator());
        }

        return sb.toString();
    }

    /**
     * Verify whether the provided user can be a candidate for the board.
     *
     * @param userId            The user's id.
     * @param associationId     The association id.
     * @return                  True if the user can be a candidate.
     */
    public boolean verifyCandidate(String userId, Integer associationId) {
        if (userId == null || associationId == null) {
            return false;
        }

        List<Membership> memberships = membershipRepository.findAllByUserId(userId);

        //Check for council membership in all of user's associations
        for (Membership m : memberships) {
            Optional<Association> optionalAssociation = associationRepository.findById(m.getAssociationId());
            if (optionalAssociation.isEmpty() || optionalAssociation.get().getCouncilUserIds().contains(userId)) {
                return false;
            }
        }

        //Check if the member has been in the HOA for 3 years
        Optional<Membership> optionalMembership = membershipRepository
                .findByUserIdAndAssociationIdAndLeaveDate(userId, associationId, null);
        if (optionalMembership.isEmpty()) {
            return false;
        }

        Date joinDate = optionalMembership.get().getJoinDate();
        int candidateYearLimit = -3;
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(System.currentTimeMillis()));
        c.add(Calendar.YEAR, candidateYearLimit);
        Date limitDate = new Date(c.getTime().getTime());

        if (ChronoUnit.SECONDS.between(joinDate.toInstant(), limitDate.toInstant()) < 0) {
            return false;
        }

        //TODO: Check for 10 year board membership

        return true;
    }

    /**
     * Returns whether the proposal does not exist in the existing rules.
     *
     * @param associationId The association this proposal is for.
     * @param proposal      The proposal.
     * @return              True if the proposal is unique, otherwise false
     */
    public boolean verifyProposal(Integer associationId, String proposal) {
        // This method should be called in the propose rule by checking if this method returns true
        // which means it is unique. This method should be called in the amend rule by checking if this method
        // returns FALSE for THE ORIGINAL rule AND TRUE for the amendment.
        return associationRepository.findById(associationId).orElse(null)
                .getRules().stream().noneMatch(x -> x.equals(proposal));
    }

}
