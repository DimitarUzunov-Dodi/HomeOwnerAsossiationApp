package nl.tudelft.sem.template.association.domain.election;

import java.io.IOException;
import java.util.NoSuchElementException;
import javax.servlet.http.HttpServletRequest;
import nl.tudelft.sem.template.association.domain.association.AssociationService;
import nl.tudelft.sem.template.association.domain.user.UserService;
import nl.tudelft.sem.template.association.models.*;
import nl.tudelft.sem.template.association.utils.RequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Service
public class ElectionService {

    private final transient UserService userService;

    private final transient AssociationService associationService;

    private final transient RequestUtil requestUtil;

    private static final int PORT = 8083;

    /**
     * Constructs the ElectionService, which autowires the dependencies.
     *
     * @param userService the user service
     * @param associationService the association service
     * @param requestUtil the request util
     */
    @Autowired
    public ElectionService(UserService userService, AssociationService associationService, RequestUtil requestUtil) {
        this.userService = userService;
        this.associationService = associationService;
        this.requestUtil = requestUtil;
    }

    /**
     * Creates an election by calling the voting microservice.
     *
     * @param request The request of the user
     * @return The response of the voting microservice
     * @throws IOException If the request given does not contain the correct information
     */
    public ResponseEntity<String> createElection(HttpServletRequest request) throws IOException {
        AssociationRequestModel model = requestUtil.convertToModel(request, AssociationRequestModel.class);

        return requestUtil.postRequest(model, String.class,
                requestUtil.getToken(request), PORT, "create-election");
    }

    /**
     * Gets the candidates of an election of a specific association, by calling the voting microservice.
     *
     * @param request The request of the user
     * @return The response of the voting microservice
     * @throws IOException If the request given does not contain the correct information
     */
    public ResponseEntity<String> getCandidates(HttpServletRequest request) throws IOException {
        AssociationRequestModel model = requestUtil.convertToModel(request, AssociationRequestModel.class);

        return requestUtil.postRequest(model, String.class,
                requestUtil.getToken(request), PORT, "get-candidates");
    }

    /**
     * Applying to the election of an association by calling the voting microservice.
     *
     * <p>First checks if the user is part of the association,
     * and after that if the user is actually able to be a candidate
     *
     * @param request the request made from the user
     * @return the response made by the voting microservice
     * @throws IOException If the HttpServletRequest given does not contain the correct information
     * @throws NoSuchElementException If the database does not contain the user
     * @throws IllegalArgumentException If the user is not a part of the association
     */
    public ResponseEntity<String> applyForCandidate(HttpServletRequest request)
            throws IOException, NoSuchElementException, IllegalArgumentException {
        UserAssociationRequestModel model = requestUtil.convertToModel(request, UserAssociationRequestModel.class);

        if (!associationService.isMember(model.getUserId(), model.getAssociationId())) {
            throw new IllegalArgumentException("User was not part of that association");
        }

        //TODO: use verifyCandidate from AssociationService to check it

        return requestUtil.postRequest(model, String.class,
                requestUtil.getToken(request), PORT, "apply-for-candidate");
    }

    /**
     * Casting a vote on a specific candidate, by calling the voting microservice.
     *
     * <p>Checks if the user is part of the association,
     * and if the candidate is also part of the association.
     *
     * @param request the request made from the user
     * @return the response made by the voting microservice
     * @throws IOException If the HttpServletRequest given does not contain the correct information
     * @throws NoSuchElementException If the database does not contain the user
     * @throws IllegalArgumentException If the user is not a part of the association
     */
    public ResponseEntity<String> castVote(HttpServletRequest request)
            throws IOException, NoSuchElementException, IllegalArgumentException {
        ElectionVoteRequestModel model = requestUtil.convertToModel(request, ElectionVoteRequestModel.class);

        if (!associationService.isMember(model.getVoterId(), model.getAssociationId())) {
            throw new IllegalArgumentException("Voter was not part of that association");
        }

        if (!associationService.isMember(model.getCandidateId(), model.getAssociationId())) {
            throw new IllegalArgumentException("Candidate was not part of that association");
        }

        return requestUtil.postRequest(model, String.class,
                requestUtil.getToken(request), PORT, "apply-for-candidate");
    }

}
