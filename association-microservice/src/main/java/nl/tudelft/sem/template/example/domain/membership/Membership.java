package nl.tudelft.sem.template.example.domain.membership;

import java.util.Date;
import javax.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "memberships")
@NoArgsConstructor
public class Membership {
    /*
primary key
unique id for different memberships
 */
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, unique = true)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "association_id", nullable = false)
    private int associationId;

    @Column(name = "address", nullable = false)
    @Convert(converter = AddressAttributeConverter.class)
    private Address address;

    @Column(name = "join_date", nullable = false)
    private Date joinDate;

    /**constructor.
     *
     * @param userId user id
     * @param associationId association id
     * @param address address
     * @param joinDate join date
     */
    public Membership(String userId, int associationId, Address address, Date joinDate) {
        this.userId = userId;
        this.associationId = associationId;
        this.address = address;
        this.joinDate = joinDate;
    }

    /**getter.
     *
     * @return membership id
     */
    public Integer getId() {
        return id;
    }

    /**getter.
     *
     * @return user id
     */
    public String getUserId() {
        return userId;
    }

    /**getter.
     *
     * @return association id
     */
    public int getAssociationId() {
        return associationId;
    }

    /**getter.
     *
     * @return address
     */
    public Address getAddress() {
        return address;
    }

    /**setter.
     *
     * @param address set new address
     */
    public void setAddress(Address address) {
        this.address = address;
    }

    /**getter.
     *
     * @return get join date
     */
    public Date getJoinDate() {
        return joinDate;
    }
}
