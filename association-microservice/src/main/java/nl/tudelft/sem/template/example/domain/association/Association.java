package nl.tudelft.sem.template.example.domain.association;

import java.util.Date;
import java.util.Set;
import javax.persistence.*;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.example.domain.membership.CouncilUserIdsAttributeConverter;

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
    @Convert(converter = CouncilUserIdsAttributeConverter.class)
    private Set<Integer> councilUserIds;

    /**constructor.
     *
     * @param name name of association
     * @param creationDate creation date of association
     * @param description description of association
     * @param councilNumber the maximum council number of the association
     */
    public Association(String name, Date creationDate, String description, int councilNumber) {
        this.name = name;
        this.creationDate = creationDate;
        this.description = description;
        this.councilNumber = councilNumber;
    }

    /**getter.
     *
     * @return association id
     */
    public int getId() {
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

    /**getter.
     *
     * @return the User Ids of council members
     */
    public Set<Integer> getCouncilUserIds() {
        return councilUserIds;
    }
}
