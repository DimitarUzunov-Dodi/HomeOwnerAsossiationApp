package nl.tudelft.sem.template.association.domain.association;

import java.util.*;
import javax.persistence.*;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.association.domain.location.Location;
import nl.tudelft.sem.template.association.domain.location.LocationConverter;
import nl.tudelft.sem.template.association.domain.membership.UserIdsAttributeConverter;

@Entity
@Table(name = "associations")
@NoArgsConstructor
public class Association {

    /*
    primary key
    unique id for different association
     */
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, unique = true)
    private Integer id;

    /*
    name of the association, not necessarily unique
     */
    @Column(name = "name", nullable = false)
    private String name;


    /*
    location of the association
     */
    @Column(name = "location", nullable = false)
    @Convert(converter = LocationConverter.class)
    private Location location;

    /*
    creation date of the association
     */
    @Column(name = "creation_date", nullable = false)
    private Date creationDate;

    /*
    a short description of the association
    not necessary for association
     */
    @Column(name = "description")
    private String description;

    /*
    specify the number of possible council number position of the association
     */
    @Column(name = "council_number")
    private int councilNumber;

    /*
    User Ids of the council members
     */
    @Column(name = "council_user_ids")
    @Convert(converter = UserIdsAttributeConverter.class)
    private Set<String> councilUserIds;

    /*
    User Ids of the members
     */
    @Column(name = "member_user_ids")
    @Convert(converter = UserIdsAttributeConverter.class)
    private Set<String> memberUserIds;

    /*
    rules for the association
     */
    @Column(name = "rules")
    @Convert(converter = RulesAttributeConverter.class)
    private List<String> rules;

    /**getter.
     *
     * @return rules
     */
    public List<String> getRules() {
        return rules;
    }

    /**constructor.
     *
     * @param name name of association
     * @param description description of association
     * @param councilNumber the maximum council number of the association
     */
    public Association(String name, Location location, String description, int councilNumber) {
        this.name = name;
        this.location=location;
        this.description = description;
        this.councilNumber = councilNumber;
        this.creationDate = new Date(System.currentTimeMillis());
        this.councilUserIds = new HashSet<>();
        this.memberUserIds = new HashSet<>();
        this.rules = new ArrayList<>();
    }

    /**getter.
     *
     * @return association id
     */
    public Integer getId() {
        return id;
    }

    /**getter.
     *
     * @return association name
     */
    public String getName() {
        return name;
    }

    /**getter.
     *
     * @return election date
     */
    public Date getCreationDate() {
        return creationDate;
    }

    /**setter.
     *
     * @param creationDate set new election date
     */
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    /**getter.
     *
     * @return description of the association
     */
    public String getDescription() {
        return description;
    }

    /**setter.
     *
     * @param description set new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**getter.
     *
     * @return the cap number of council
     */
    public int getCouncilNumber() {
        return councilNumber;
    }

    /**setter.
     *
     * @param councilNumber set new councilNumber
     */
    public void setCouncilNumber(int councilNumber) {
        this.councilNumber = councilNumber;
    }

    /**getter.
     *
     * @return the User Ids of council members
     */
    public Set<String> getCouncilUserIds() {
        return councilUserIds;
    }

    /**
     * Setter for the council user ids field.
     *
     * @param councilUserIds The new set of council user ids.
     */
    public void setCouncilUserIds(Set<String> councilUserIds) {
        this.councilUserIds = councilUserIds;
    }

    /**getter.
     *
     * @return the User Ids of members
     */
    public Set<String> getMemberUserIds() {
        return memberUserIds;
    }

    /**
     * Setter for the council user ids field.
     *
     * @param memberUserIds Set of member user ids.
     */
    public void setMemberUserIds(Set<String> memberUserIds) {
        this.memberUserIds = memberUserIds;
    }

    public void addMember(String userId) {
        this.memberUserIds.add(userId);
    }

    public void removeMember(String userId) {
        this.memberUserIds.remove(userId);
    }

    public Location getLocation() {
        return location;
    }

    public void setRules(List<String> rules) {
        this.rules = rules;
    }
}
