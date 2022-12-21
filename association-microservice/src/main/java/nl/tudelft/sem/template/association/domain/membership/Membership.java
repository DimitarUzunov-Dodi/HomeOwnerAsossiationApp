package nl.tudelft.sem.template.association.domain.membership;

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

    @Column(name = "leave_date")
    private Date leaveDate;

    /**constructor.
     *
     * @param userId user id
     * @param associationId association id
     * @param address address
     */
    public Membership(String userId, int associationId, Address address) {
        this.userId = userId;
        this.associationId = associationId;
        this.address = address;
        this.joinDate = new Date(System.currentTimeMillis());
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

    /**getter.
     *
     * @return get leave date
     */
    public Date getLeaveDate() {
        return leaveDate;
    }

    public void leave() {
        this.leaveDate = new Date(System.currentTimeMillis());
    }
}
