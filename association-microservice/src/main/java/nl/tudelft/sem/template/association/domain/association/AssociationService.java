package nl.tudelft.sem.template.association.domain.association;

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

            Collections.sort(list, Map.Entry.comparingByValue());

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
            List<String> rules = association.getRules();

            if (model.isAmendment()) {
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

}
