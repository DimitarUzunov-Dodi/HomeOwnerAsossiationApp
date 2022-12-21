package nl.tudelft.sem.template.association.domain.rules;

import java.io.IOException;
import java.util.NoSuchElementException;
import javax.servlet.http.HttpServletRequest;
import nl.tudelft.sem.template.association.domain.association.AssociationService;
import nl.tudelft.sem.template.association.domain.membership.MembershipService;
import nl.tudelft.sem.template.association.domain.user.UserService;
import nl.tudelft.sem.template.association.models.RuleVoteRequestModel;
import nl.tudelft.sem.template.association.models.RuleVoteRequestModelInternal;
import nl.tudelft.sem.template.association.utils.RequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class RuleService {

    private final transient AssociationService associationService;

    private final transient MembershipService membershipService;

    private final transient UserService userService;

    private final transient RequestUtil requestUtil;

    private static final int PORT = 8083;

    /**
     * RuleService constructor, what autowire it's dependencies.
     *
     * @param associationService the association service
     * @param membershipService the membership service
     * @param userService the user service
     * @param requestUtil the request util
     */
    @Autowired
    public RuleService(AssociationService associationService,
                       MembershipService membershipService,
                       UserService userService,
                       RequestUtil requestUtil) {
        this.associationService = associationService;
        this.membershipService = membershipService;
        this.userService = userService;
        this.requestUtil = requestUtil;
    }

    /**
     * Checks the input if the user is in the association, and if the user is a council member,
     * If so then it sends a request to the voting microservice.
     *
     * @param request the request made from the user
     * @return the response made by the voting microservice
     * @throws IOException If HttpRequest given does not contain the correct information
     * @throws NoSuchElementException If the database does not contain the user
     */
    public ResponseEntity<String> voteOnRule(HttpServletRequest request) throws IOException, NoSuchElementException {
        RuleVoteRequestModel model = requestUtil.convertToModel(request, RuleVoteRequestModel.class);
        membershipService.isInAssociation(model.getUserId(), model.getAssociationId());

        RuleVoteRequestModelInternal internalModel = new RuleVoteRequestModelInternal();
        internalModel.setAssociationId(model.getAssociationId());
        internalModel.setUserId(userService.getUserById(model.getUserId()).get().getId());
        internalModel.setRule(model.getRule());

        associationService.verifyCouncilMember(internalModel.getUserId(), model.getAssociationId());

        return requestUtil.postRequest(internalModel, String.class,
                requestUtil.getToken(request), PORT, "/rule-voting/vote-rule");
    }

    public void proposeRule() {
        // TODO
    }

    public void amendRule() {
        // TODO
    }

    public void getRules() {
        // TODO
    }
}
