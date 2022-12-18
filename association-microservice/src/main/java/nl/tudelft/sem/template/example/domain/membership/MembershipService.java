package nl.tudelft.sem.template.example.domain.membership;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class MembershipService {
    private final transient MembershipRepository membershipRepository;

    public MembershipService(MembershipRepository membershipRepository) {
        this.membershipRepository = membershipRepository;
    }

    public List<Membership> getMembers(int associationId) throws NoSuchMembershipException {
        List<Membership> memberships = membershipRepository.findByAssociationId(associationId);
        if (memberships.size() == 0) {
            throw new NoSuchMembershipException();
        }
        return memberships;
    }

    public List<String> getCouncil(int associationId) throws NoSuchMembershipException {
        List<Membership> council = membershipRepository.findByAssociationIdAndBoard(associationId, true);
        if (council.size() == 0) {
            throw new NoSuchMembershipException();
        }
        ArrayList<String> councilId = new ArrayList<>();
        for (Membership m : council) {
            councilId.add(m.getUserId());
        }
        return councilId;
    }

    public boolean isInAssociation(String userId, int associationId) {
        return membershipRepository.existsByUserIdAndAssociationId(userId, associationId);
    }

    public Membership getMembership(String userId, int associationId) throws NoSuchMembershipException {
        Optional<Membership> membership = membershipRepository.findByUserIdAndAssociationId(userId, associationId);
        if (membership.isEmpty()) {
            throw new NoSuchMembershipException();
        }
        return membership.get();
    }

    /**
     * @param userId
     * @param associationId
     * @param address
     * @param joinDate
     * @return if successfully joined or not, new membership is not council as default
     */
    public boolean addMembership(String userId, int associationId, Address address, Date joinDate) throws FieldNoNullException {
        if (address == null || joinDate == null) {
            throw new FieldNoNullException();
        }
        if (membershipRepository.existsByUserIdAndAssociationId(userId, associationId)) {
            return false;
        }
        membershipRepository.save(new Membership(userId, associationId, address, joinDate, false));
        return true;
    }

    public boolean updateMembership(String userId, int associationId, Address address, boolean isBoard) throws FieldNoNullException {
        if (address == null) {
            throw new FieldNoNullException();
        }
        if (!membershipRepository.existsByUserIdAndAssociationId(userId, associationId)) {
            return false;
        }
        Membership membership = membershipRepository.findByUserIdAndAssociationId(userId, associationId).get();
        membership.setBoard(isBoard);
        membership.setAddress(address);
        membershipRepository.save(membership);
        return true;
    }

    public boolean deleteMembership(String userId, int associationId) {
        if (!membershipRepository.existsByUserIdAndAssociationId(userId, associationId)) {
            return false;
        }
        Membership membership = membershipRepository.findByUserIdAndAssociationId(userId, associationId).get();
        membershipRepository.delete(membership);
        return true;
    }
}
