package nl.tudelft.sem.template.association.domain.membership;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.association.domain.history.Notification;
import nl.tudelft.sem.template.association.domain.location.Address;
import nl.tudelft.sem.template.association.domain.location.AddressConverter;

@Entity
@Table(name = "memberships")
@NoArgsConstructor
public class Membership {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, unique = true)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "association_id", nullable = false)
    private int associationId;

    @Column(name = "address", nullable = false)
    @Convert(converter = AddressConverter.class)
    private Address address;

    @Column(name = "join_date", nullable = false)
    private Date joinDate;

    @Column(name = "leave_date")
    private Date leaveDate;

    @Column(name = "times_council")
    private Integer timesCouncil;

    @Column(name = "notifications")
    @Convert(converter = NotificationAttributeConverter.class)
    private List<Notification> notifications;

    /**
     * Constructor for Membership class.
     */
    public Membership(String userId, int associationId, Address address) {
        this.userId = userId;
        this.associationId = associationId;
        this.address = address;
        this.joinDate = new Date(System.currentTimeMillis());
        this.notifications = new ArrayList<>();
        this.timesCouncil = 0;
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
     * @return get join date
     */
    public Date getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }

    public void leave() {
        this.leaveDate = new Date(System.currentTimeMillis());
    }

    public int getTimesCouncil() {
        return this.timesCouncil;
    }

    public void setTimesCouncil(int timesCouncil) {
        this.timesCouncil = timesCouncil;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    public void addNotification(Notification n) {
        this.notifications.add(n);
    }
    
}
