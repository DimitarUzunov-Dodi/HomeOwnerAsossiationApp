package nl.tudelft.sem.template.example.domain.association;

import java.util.Date;
import javax.persistence.*;
import lombok.NoArgsConstructor;

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
    election date of the association
    assumed to be yearly election
     */
    @Column(name = "election_date", nullable = false)
    private Date electionDate;

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

    /**constructor.
     *
     * @param name name of association
     * @param electionDate election date of association
     * @param description description of association
     * @param councilNumber the maximum council number of the association
     */
    public Association(String name, Date electionDate, String description, int councilNumber) {
        this.name = name;
        this.electionDate = electionDate;
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
    public Date getElectionDate() {
        return electionDate;
    }

    /**setter.
     *
     * @param electionDate set new election date
     */
    public void setElectionDate(Date electionDate) {
        this.electionDate = electionDate;
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
}
