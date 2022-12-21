package nl.tudelft.sem.template.association.domain.membership;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.association.domain.association.AssociationRepository;
import nl.tudelft.sem.template.association.domain.user.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class MembershipService {
    private final transient MembershipRepository membershipRepository;

    private final transient AssociationRepository associationRepository;

    private final transient UserRepository userRepository;

    /**
     * constructor.
     *
     * @param membershipRepository  membership repo
     * @param associationRepository association repo
     * @param userRepository user repo
     */
    public MembershipService(MembershipRepository membershipRepository,
                             AssociationRepository associationRepository, UserRepository userRepository) {
        this.membershipRepository = membershipRepository;
        this.associationRepository = associationRepository;
        this.userRepository = userRepository;
    }

    /**getter.
     *
     * @param associationId the association
     * @return members in the association
     * @throws NoSuchMembershipException if there is an invalid id
     */
    public List<Membership> getMembers(int associationId) throws NoSuchMembershipException {
        List<Membership> memberships = membershipRepository.findByAssociationId(associationId);
        if (memberships.size() == 0) {
            throw new NoSuchMembershipException();
        }
        return memberships;
    }

    /**getter.
     *
     * @param userId user id
     * @param associationId association id
     * @return if the membership exists
     */
    public boolean isInAssociation(int userId, int associationId) {
        return membershipRepository.existsByUserIdAndAssociationId(userId, associationId);
    }

    /**getter.
     *
     * @param userId user id
     * @param associationId association id
     * @return the corresponding membership
     * @throws NoSuchMembershipException if there is an invalid id
     */
    public Membership getMembership(int userId, int associationId) throws NoSuchMembershipException {
        Optional<Membership> membership = membershipRepository.findByUserIdAndAssociationId(userId, associationId);
        if (membership.isEmpty()) {
            throw new NoSuchMembershipException();
        }
        return membership.get();
    }


    /**update membership info.
     *
     * @param userId user id
     * @param assoId association id
     *
     * @return if successfully updated or not
     * @throws FieldNoNullException if there is an invalid input
     */
    public boolean updateMembership(int userId, int assoId) throws FieldNoNullException {
        if (!membershipRepository.existsByUserIdAndAssociationId(userId, assoId)) {
            return false;
        }
        Membership membership = membershipRepository.findByUserIdAndAssociationId(userId, assoId).get();
        membershipRepository.save(membership);
        return true;
    }

    /**delete a membership.
     *
     * @param userId user id
     * @param associationId association id
     * @return if the deletion is successful
     */
    public boolean deleteMembership(int userId, int associationId) {
        if (!membershipRepository.existsByUserIdAndAssociationId(userId, associationId)) {
            return false;
        }
        Membership membership = membershipRepository.findByUserIdAndAssociationId(userId, associationId).get();
        membershipRepository.delete(membership);
        return true;
    }
}
