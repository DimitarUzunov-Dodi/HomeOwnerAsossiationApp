package nl.tudelft.sem.template.association.domain.association;

import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import nl.tudelft.sem.template.association.domain.location.Address;
import nl.tudelft.sem.template.association.domain.location.Location;
import nl.tudelft.sem.template.association.domain.membership.Membership;
import nl.tudelft.sem.template.association.domain.membership.MembershipRepository;
import nl.tudelft.sem.template.association.models.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
    public String createAssociation(String name, Location location, String description,
                                    int councilNumber) {
        Association association = new Association(name, location, description, councilNumber);
        int associationId = associationRepository.save(association).getId();

        String electionString = createElection(associationId);

        return "Association was created:" + System.lineSeparator()
                + "ID: " + associationId + System.lineSeparator()
                + "Name: " + name + System.lineSeparator()
                + "Country: " + location.getCountry() + System.lineSeparator()
                + "City: " + location.getCity() + System.lineSeparator()
                + "Description: " + description + System.lineSeparator()
                + "Max council members: " + councilNumber + System.lineSeparator() + electionString;
    }


    /**
     * Gets the existing association IDs.
     *
     * @return  A list with the ids.
     */
    public List<Integer> getAssociationIds() {
        List<Association> associationList = associationRepository.findAll();
        List<Integer> ids = associationList.stream().map(Association::getId).collect(Collectors.toList());
        return ids;
    }

    /**getter.
     *
     * @param associationId the id of the association
     * @return Association correspondingly
     */
    public String getAssociationInfo(int associationId) {
        Association association = getAssociationById(associationId);
        return "Association information:" + System.lineSeparator()
                + "ID: " + associationId + System.lineSeparator()
                + "Name: " + association.getName() + System.lineSeparator()
                + "Country: " + association.getLocation().getCountry() + System.lineSeparator()
                + "City: " + association.getLocation().getCity() + System.lineSeparator()
                + "Description: " + association.getDescription() + System.lineSeparator()
                + "Max council members: " + association.getCouncilNumber();
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
    public String joinAssociation(String userId, int associationId, Address address) {
        Optional<Association> optionalAssociation = associationRepository.findById(associationId);
        if (optionalAssociation.isEmpty()) {
            throw new IllegalArgumentException("Association with ID " + associationId + " does not exist.");
        }
        Association association = optionalAssociation.get();

        //Check if user has an address in the right city and country
        if (!address.getLocation().equals(association.getLocation())) {
            throw new IllegalArgumentException("You don't live in the right city or country to join this association.");
        }

        association.addMember(userId);
        Membership membership = new Membership(userId, associationId, address);
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
            return councilMembers.contains(userId);
        }
        return false;
    }

    /**
     * returns true if the member is part of the association, otherwise false.
     *
     * @param userId the userid to check
     * @param associationId the association to check
     * @return if the user is part of that association
     */
    public boolean isMember(String userId, Integer associationId) {
        if (userId == null || associationId == null) {
            return false;
        }

        Optional<Membership> optionalMembership = membershipRepository
                .findByUserIdAndAssociationIdAndLeaveDate(userId, associationId, null);

        return optionalMembership.isPresent();
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
            HashMap<String, Integer> hm = model.getStandings();
            List<Map.Entry<String, Integer>> list = new LinkedList<>(hm.entrySet());
            Collections.sort(list, (e1, e2) -> -(e1.getValue().compareTo(e2.getValue())));
            Set<String> council = new HashSet<>();

            int j = 0; //NOPMD

            for (Map.Entry<String, Integer> pair : list) {
                if (j < association.getCouncilNumber() && verifyCandidate(pair.getKey(), model.getAssociationId())) {
                    Membership membership =
                            membershipRepository.findByUserIdAndAssociationId(pair.getKey(), association.getId()).get();
                    membership.setTimesCouncil(membership.getTimesCouncil() + 1);
                    membershipRepository.save(membership);
                    council.add(pair.getKey());
                    j++; //NOPMD
                }
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
     * Verify whether the join date has been at least 3 years ago.
     *
     * @param joinDate          The join date.
     * @return                  True if the join date has been 3+ years ago.
     */
    public boolean verifyJoinDate(Date joinDate) {
        int candidateYearLimit = -3;
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(System.currentTimeMillis()));
        c.add(Calendar.YEAR, candidateYearLimit);
        Date limitDate = new Date(c.getTime().getTime());
        return ChronoUnit.SECONDS.between(joinDate.toInstant(), limitDate.toInstant()) >= 0;
    }

    /**
     * Verify whether the provided user can be a candidate for the board.
     *
     * @param userId            The user's id.
     * @param associationId     The association id.
     * @return                  True if the user can be a candidate.
     */
    public boolean verifyCandidate(String userId, Integer associationId) {
        // Check if the member is in the HOA (includes null checks)
        if (!isMember(userId, associationId)) {
            return false;
        }

        Optional<Membership> optionalMembership = membershipRepository
                .findByUserIdAndAssociationIdAndLeaveDate(userId, associationId, null);
        List<Membership> memberships = membershipRepository.findAllByUserId(userId);
        memberships.remove(optionalMembership.get());
        // Check if the user is a councilman in any other association
        for (Membership m : memberships) {
            Association association = associationRepository.findById(m.getAssociationId()).get();
            if (association.getCouncilUserIds().contains(userId)) {
                return false;
            }
        }

        //Check if the user has been in the association for at least 3 years

        if (!verifyJoinDate(optionalMembership.get().getJoinDate())) {
            return false;
        }

        //Check if the member has been a councilman 10 times
        return optionalMembership.get().getTimesCouncil() < 10;
    }


    /**
     * Creates the first election after association creation, and after each election.
     *
     * @return a message confirming the creation.
     */
    public String createElection(int associationId) {
        final String url = "http://localhost:8083/election/create-election";

        AssociationRequestModel model = new AssociationRequestModel();
        model.setAssociationId(associationId);

        String token = authenticateService("VotingService", "SuperSecretPassword");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "
                + token);
        HttpEntity<AssociationRequestModel> request = new HttpEntity<>(model, headers);

        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, request, String.class);

            if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
                return "Board election was created and will be held in 1 year.";
            } else {
                throw new IllegalArgumentException("Board election was not created.");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Board election was not created.");
        }
    }

    /**
     * Generates the token for server to server communication.
     *
     * @param serviceUsername   The username the service is registered with.
     * @param servicePassword   The password the service is registered with.
     * @return                  The bearer token generated through the authentication service.
     */
    public String authenticateService(String serviceUsername, String servicePassword) {
        RegistrationRequestModel regModel = new RegistrationRequestModel();
        regModel.setUserId(serviceUsername);
        regModel.setPassword(servicePassword);
        HttpEntity<RegistrationRequestModel> authRequest = new HttpEntity<>(regModel);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<AuthenticationResponseModel> authenticationResponse = restTemplate
                .postForEntity("http://localhost:8081/authenticate", authRequest, AuthenticationResponseModel.class);
        return Objects.requireNonNull(authenticationResponse.getBody()).getToken();
    }

    /**
     * Returns whether the proposal does not exist in the existing rules.
     *
     * @param associationId The association this proposal is for.
     * @param proposal      The proposal.
     * @return              True if the proposal is unique, otherwise false
     */
    public boolean verifyProposal(Integer associationId, String proposal) {
        return associationRepository.findById(associationId).orElse(null)
                .getRules().stream().noneMatch(x -> x.equals(proposal));
    }

}
