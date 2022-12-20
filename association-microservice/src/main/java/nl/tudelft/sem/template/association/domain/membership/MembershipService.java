package nl.tudelft.sem.template.association.domain.membership;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class MembershipService {
    private final transient MembershipRepository membershipRepository;

    /**constructor.
     *
     * @param membershipRepository the repo
     */
    public MembershipService(MembershipRepository membershipRepository) {
        this.membershipRepository = membershipRepository;
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
    public boolean isInAssociation(String userId, int associationId) {
        return membershipRepository.existsByUserIdAndAssociationId(userId, associationId);
    }

    /**getter.
     *
     * @param userId user id
     * @param associationId association id
     * @return the corresponding membership
     * @throws NoSuchMembershipException if there is an invalid id
     */
    public Membership getMembership(String userId, int associationId) throws NoSuchMembershipException {
        Optional<Membership> membership = membershipRepository.findByUserIdAndAssociationId(userId, associationId);
        if (membership.isEmpty()) {
            throw new NoSuchMembershipException();
        }
        return membership.get();
    }

    /**add new membership.
     *
     * @param userId user id
     * @param assoId association id
     * @param address address
     * @param joinDate join date
     *
     * @return if successfully joined or not, new membership is not council as default
     */
    public boolean addMembership(String userId, int assoId, Address address, Date joinDate) throws FieldNoNullException {
        if (address == null || joinDate == null) {
            throw new FieldNoNullException();
        }
        if (membershipRepository.existsByUserIdAndAssociationId(userId, assoId)) {
            return false;
        }
        membershipRepository.save(new Membership(userId, assoId, address, joinDate));
        return true;
    }

    /**update membership info.
     *
     * @param userId user id
     * @param assoId association id
     * @param addr address
     *
     * @return if successfully updated or not
     * @throws FieldNoNullException if there is an invalid input
     */
    public boolean updateMembership(String userId, int assoId, Address addr) throws FieldNoNullException {
        if (addr == null) {
            throw new FieldNoNullException();
        }
        if (!membershipRepository.existsByUserIdAndAssociationId(userId, assoId)) {
            return false;
        }
        Membership membership = membershipRepository.findByUserIdAndAssociationId(userId, assoId).get();
        membership.setAddress(addr);
        membershipRepository.save(membership);
        return true;
    }

    /**delete a membership.
     *
     * @param userId user id
     * @param associationId association id
     * @return if the deletion is successful
     */
    public boolean deleteMembership(String userId, int associationId) {
        if (!membershipRepository.existsByUserIdAndAssociationId(userId, associationId)) {
            return false;
        }
        Membership membership = membershipRepository.findByUserIdAndAssociationId(userId, associationId).get();
        membershipRepository.delete(membership);
        return true;
    }
}
