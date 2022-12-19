package nl.tudelft.sem.template.example.domain.member;

import javax.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "members")
@NoArgsConstructor
public class Member {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, unique = true)
    private int id;

    /*
    the memberId of the account
    also unique key
     */
    @Column(name = "member_id", nullable = false, unique = true)
    private String memberId;

    /*
    personal name for display
     */
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "association_id", nullable = false)
    private int associationId;

    @Column(name = "address", nullable = false)
    @Convert(converter = AddressAttributeConverter.class)
    private Address address;

    //TODO
    //realistic field such as contact email etc

    /** Constructor for member class.
     *
     * @param memberId id of the member
     * @param name name of the member
     * @param associationId id of the association
     * @param address address of the member
     */
    public Member(String memberId, String name, int associationId, Address address) {
        this.memberId = memberId;
        this.name = name;
        this.associationId = associationId;
        this.address = address;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getName() {
        return name;
    }

    public int getAssociationId() {
        return associationId;
    }

    public Address getAddress() {
        return address;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAssociationId(int associationId) {
        this.associationId = associationId;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
