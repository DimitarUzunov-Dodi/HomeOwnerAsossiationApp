package nl.tudelft.sem.template.example.domain.member;

import nl.tudelft.sem.template.example.domain.association.AssociationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class MemberService {
    private final transient MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /**
     *
     * @param memberId
     * @param associationId
     * @return if the member is in the association above or not,
     * note no such association also return false
     */
    public boolean isMember(String memberId, int associationId){
        return memberRepository.existsByMemberIdAndAssociationId(memberId,associationId);
    }

    /**
     *
     * @param memberId
     * @return the corresponding memberId
     */
    public Optional<Member> getMemberById(String memberId){
        return memberRepository.findByMemberId(memberId);
    }

    /**
     *
     * @param service
     * @param memberId
     * @param name
     * @param associationId
     * @param address
     * @throws NoSuchAssociationException
     *
     * save a member to the repository
     */
    public void addMember(AssociationService service, String memberId, String name, int associationId, String address) throws NoSuchAssociationException {
        checkAssociationExists(service, associationId);
        memberRepository.save(new Member(memberId, name, associationId, new Address(address)));
    }

    /**
     *
     * @param service
     * @param associationId
     * @return all members belong to the association
     * @throws NoSuchAssociationException
     */
    public List<Member> membersOfAssociation(AssociationService service, int associationId) throws NoSuchAssociationException {
        checkAssociationExists(service, associationId);
        return memberRepository.findAllByAssociationId(associationId);
    }

    /**
     *
     * @param service
     * @param associationId
     * @throws NoSuchAssociationException
     *
     * checks if there is an association correspond to the id
     */
    private void checkAssociationExists(AssociationService service, int associationId) throws NoSuchAssociationException{
        try{
            service.getAssociation(associationId).get();
        }catch (NoSuchElementException e){
            throw new NoSuchAssociationException();
        }
    }
}
