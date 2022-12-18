package nl.tudelft.sem.template.example.domain.Membership;

import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

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
    @Column(name = "id",nullable = false,unique = true)
    private Integer id;

    @Column(name = "user_id",nullable = false)
    private String userId;

    @Column(name = "association_id" ,nullable = false)
    private int associationId;

    @Column(name = "address",nullable = false)
    @Convert(converter = AddressAttributeConverter.class)
    private Address address;

    @Column(name = "join_date",nullable = false)
    private Date joinDate;

    @Column(name = "board")
    private boolean board;

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setBoard(boolean board) {
        board = board;
    }

    public Integer getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public int getAssociationId() {
        return associationId;
    }

    public Address getAddress() {
        return address;
    }

    public Date getJoinDate() {
        return joinDate;
    }

    public boolean isBoard() {
        return board;
    }



    public Membership(String userId, int associationId, Address address, Date joinDate, boolean isBoard) {
        this.userId = userId;
        this.associationId = associationId;
        this.address = address;
        this.joinDate = joinDate;
        this.board = isBoard;
    }
}
