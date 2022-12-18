package nl.tudelft.sem.template.voting.models;

import java.util.List;
import lombok.Data;

/**
 * Model representing a rule voting verification request.
 */
@Data
public class RuleVerificationRequestModel {
    private Integer userId;
    private List<Integer> councilMembers;
}
