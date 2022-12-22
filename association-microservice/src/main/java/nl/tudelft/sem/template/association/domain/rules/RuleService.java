package nl.tudelft.sem.template.association.domain.rules;

import java.io.IOException;
import java.util.NoSuchElementException;
import javax.servlet.http.HttpServletRequest;
import nl.tudelft.sem.template.association.domain.association.AssociationService;
import nl.tudelft.sem.template.association.models.*;
import nl.tudelft.sem.template.association.utils.RequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class RuleService {

    private final transient AssociationService associationService;

    private final transient RequestUtil requestUtil;

    private static final int PORT = 8083;

    /**
     * RuleService constructor, which autowire it's dependencies.
     *
     * @param associationService the association service
     * @param requestUtil the request util
     */
    @Autowired
    public RuleService(AssociationService associationService,
                       RequestUtil requestUtil) {
        this.associationService = associationService;
        this.requestUtil = requestUtil;
    }

    /**
     * Votes on a rule, by calling the voting microservice
     *
     * <p>Checks for the input whether the user is in the association, and whether the user is a council member,
     * if so then the request wil be forwarded to the voting microservice.
     *
     * @param request the request made from the user
     * @return the response made by the voting microservice
     * @throws IOException If the HttpServletRequest given does not contain the correct information
     * @throws NoSuchElementException If the database does not contain the user
     * @throws IllegalArgumentException If the user is not a part of the association
     */
    public ResponseEntity<String> voteOnRule(HttpServletRequest request)
            throws IOException, NoSuchElementException, IllegalArgumentException {
        RuleVoteRequestModel model = requestUtil.convertToModel(request, RuleVoteRequestModel.class);

        if (!associationService.isMember(model.getUserId(), model.getAssociationId())) {
            throw new IllegalArgumentException("User was not part of that association");
        }

        associationService.verifyCouncilMember(model.getUserId(), model.getAssociationId());

        return requestUtil.postRequest(model, String.class,
                requestUtil.getToken(request), PORT, "rule-voting/vote-rule");
    }


    /**
     * Proposes a rule by calling the voting microservice.
     *
     * <p>Checks for the input whether the user is in the association,
     * if so then the request wil be forwarded to the voting microservice.
     *
     * @param request the request made from the user
     * @return the response made by the voting microservice
     * @throws IOException If the HttpServletRequest given does not contain the correct information
     * @throws NoSuchElementException If the database does not contain the user
     * @throws IllegalArgumentException If the user is not a part of the association
     */
    public ResponseEntity<String> proposeRule(HttpServletRequest request)
            throws IOException, NoSuchElementException, IllegalArgumentException {
        RuleProposalRequestModel model = requestUtil.convertToModel(request, RuleProposalRequestModel.class);

        if (!associationService.isMember(model.getUserId(), model.getAssociationId())) {
            throw new IllegalArgumentException("User was not part of that association");
        }

        return requestUtil.postRequest(model, String.class,
                requestUtil.getToken(request), PORT, "rule-voting/propose-rule");
    }

    /**
     * Amends a rule by calling the voting microservice.
     *
     * <p>Checks the input if the user is in the association,
     * if so then the request wil be forwarded to the voting microservice.
     *
     * @param request the request made from the user
     * @return the response made by the voting microservice
     * @throws IOException If the HttpServletRequest given does not contain the correct information
     * @throws NoSuchElementException If the database does not contain the user
     * @throws IllegalArgumentException If the user is not a part of the association
     */
    public ResponseEntity<String> amendRule(HttpServletRequest request)
            throws IOException, NoSuchElementException, IllegalArgumentException {
        RuleAmendRequestModel model = requestUtil.convertToModel(request, RuleAmendRequestModel.class);

        if (!associationService.isMember(model.getUserId(), model.getAssociationId())) {
            throw new IllegalArgumentException("User was not part of that association");
        }

        return requestUtil.postRequest(model, String.class,
                requestUtil.getToken(request), PORT, "rule-voting/amend-rule");
    }

    /**
     * Gets the rule based on the request the user gave us,
     * the request wil be forwarded to the voting microservice.
     *
     * @param request the request made from the user
     * @return the response made by the voting microservice
     * @throws IOException If the HttpServletRequest given does not contain the correct information
     * @throws NoSuchElementException If the database does not contain the user
     * @throws IllegalArgumentException If the user is not a part of the association
     */
    public ResponseEntity<String> getRules(HttpServletRequest request)
            throws IOException, NoSuchElementException, IllegalArgumentException {
        RuleGetRequestModel model = requestUtil.convertToModel(request, RuleGetRequestModel.class);

        return requestUtil.getRequest(model, String.class,
                requestUtil.getToken(request), PORT, "rule-voting/get-rule-vote");
    }
}
