package nl.tudelft.sem.template.example.domain.member;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import nl.tudelft.sem.template.example.domain.association.AssociationService;
import org.springframework.stereotype.Service;

@Service
public class MemberService {
    private final transient MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /** Check whether the user is member of given association.
     *
     * @param memberId id of the member
     * @param associationId id of the association
     * @return if the member is in the association above or not, note no such association also return false
     *
     */
    public boolean isMember(String memberId, int associationId) {
        return memberRepository.existsByMemberIdAndAssociationId(memberId, associationId);
    }

    /** gets the member by corresponding id.
     *
     * @param memberId id of the member
     * @return the corresponding memberId
     */
    public Optional<Member> getMemberById(String memberId) {
        return memberRepository.findByMemberId(memberId);
    }

    /** save a member to the repository.
     *
     * @param service associationService
     * @param memberId id of the member
     * @param name name of the member
     * @param associationId id of the association
     * @param address address of the member
     * @throws NoSuchAssociationException
     *
     */
    public void addMember(AssociationService service, String memberId, String name,
                          int associationId, String address) throws NoSuchAssociationException {
        checkAssociationExists(service, associationId);
        memberRepository.save(new Member(memberId, name, associationId, new Address(address)));
    }

    /** returns a list of all members in the association.
     *
     * @param service association service
     * @param associationId id of the association
     * @return all members belong to the association
     * @throws NoSuchAssociationException thrown when no such association exists
     */
    public List<Member> membersOfAssociation(AssociationService service, int associationId)
            throws NoSuchAssociationException {
        checkAssociationExists(service, associationId);
        return memberRepository.findAllByAssociationId(associationId);
    }

    /** checks if there is an association correspond to the id.
     *
     * @param service association service
     * @param associationId id of the association
     * @throws NoSuchAssociationException
     *
     */
    private void checkAssociationExists(AssociationService service, int associationId) throws NoSuchAssociationException {
        try {
            service.getAssociation(associationId).get();
        } catch (NoSuchElementException e) {
            throw new NoSuchAssociationException();
        }
    }
}
