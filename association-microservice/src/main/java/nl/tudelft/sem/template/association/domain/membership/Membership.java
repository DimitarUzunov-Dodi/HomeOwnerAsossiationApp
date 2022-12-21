package nl.tudelft.sem.template.association.domain.membership;

import java.util.Date;
import javax.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "memberships")
@NoArgsConstructor
public class Membership {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, unique = true)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(name = "association_id", nullable = false)
    private int associationId;

    @Column(name = "country", nullable = false)
    private String country;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "street", nullable = false)
    private String street;

    @Column(name = "house_number", nullable = false)
    private String houseNumber;

    @Column(name = "postal_code", nullable = false)
    private String postalCode;

    @Column(name = "join_date", nullable = false)
    private Date joinDate;

    @Column(name = "leave_date")
    private Date leaveDate;

    /**
     * Constructor for Membership class.
     */
    public Membership(int userId, int associationId, String country, String city, String street,
                      String houseNumber, String postalCode) {
        this.userId = userId;
        this.associationId = associationId;
        this.country = country;
        this.city = city;
        this.street = street;
        this.houseNumber = houseNumber;
        this.postalCode = postalCode;
        this.joinDate = new Date(System.currentTimeMillis());
    }

    public Integer getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public int getAssociationId() {
        return associationId;
    }

    public Date getJoinDate() {
        return joinDate;
    }

    public Date getLeaveDate() {
        return leaveDate;
    }

    public void leave() {
        this.leaveDate = new Date(System.currentTimeMillis());
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
}
