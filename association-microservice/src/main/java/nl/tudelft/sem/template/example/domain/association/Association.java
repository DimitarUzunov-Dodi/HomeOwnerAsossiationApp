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

    /** Constructor for the association class.
     *
     * @param name name of the association
     * @param electionDate election date the association
     * @param description description of the association
     * @param councilNumber number of council members in the association
     */
    public Association(String name, Date electionDate, String description, int councilNumber) {
        this.name = name;
        this.electionDate = electionDate;
        this.description = description;
        this.councilNumber = councilNumber;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getElectionDate() {
        return electionDate;
    }

    public String getDescription() {
        return description;
    }

    public int getCouncilNumber() {
        return councilNumber;
    }

    public void setElectionDate(Date electionDate) {
        this.electionDate = electionDate;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
