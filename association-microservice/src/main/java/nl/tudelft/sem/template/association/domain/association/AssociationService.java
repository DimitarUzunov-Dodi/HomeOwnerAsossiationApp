package nl.tudelft.sem.template.association.domain.association;

import java.time.temporal.ChronoUnit;
import java.util.*;
import nl.tudelft.sem.template.association.domain.membership.Membership;
import nl.tudelft.sem.template.association.domain.membership.MembershipRepository;
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
     * Verify whether the provided user can be a candidate for the board.
     *
     * @param userId            The user's id.
     * @param associationId     The association id.
     * @return                  True if the user can be a candidate.
     */
    public boolean verifyCandidate(Integer userId, Integer associationId) {
        if (userId == null || associationId == null) {
            return false;
        }

        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return false;
        }
        String userIdString = optionalUser.get().getUserId();

        List<Membership> memberships = membershipRepository.findAllByUserId(userIdString);

        //Check for council membership in all of user's associations
        for (Membership m : memberships) {
            Optional<Association> optionalAssociation = associationRepository.findById(m.getAssociationId());
            if (optionalAssociation.isEmpty() || optionalAssociation.get().getCouncilUserIds().contains(userId)) {
                return false;
            }
        }

        //Check if the member has been in the HOA for 3 years
        Optional<Membership> optionalMembership = membershipRepository
                .findByUserIdAndAssociationIdAndLeaveDate(userIdString, associationId, null);
        if (optionalMembership.isEmpty()) {
            return false;
        }

        Date currentDate = new Date(System.currentTimeMillis());
        Date joinDate = optionalMembership.get().getJoinDate();
        Long candidateYearLimit = 3L;
        if (ChronoUnit.YEARS.between(joinDate.toInstant(), currentDate.toInstant()) < candidateYearLimit) {
            return false;
        }

        //TODO: Check for 10 year board membership

        return true;
    }

}
