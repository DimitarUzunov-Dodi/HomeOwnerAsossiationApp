package nl.tudelft.sem.template.example.domain.member;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository("memberRepository")
public interface MemberRepository extends JpaRepository<Member, Integer> {
    /*
    return the user according to member id
     */
    Optional<Member> findByMemberId(String memberId);

    /*
    the isMember method check if a member in inside an association
     */
    boolean existsByMemberIdAndAssociationId(String memberId, int associationId);

    /*
    return the members of a certain association
     */
    List<Member> findAllByAssociationId(int associationId);
}
