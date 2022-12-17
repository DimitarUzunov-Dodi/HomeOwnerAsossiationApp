package nl.tudelft.sem.template.voting.domain;

import java.sql.Date;
import javax.persistence.*;

@MappedSuperclass
public abstract class Voting {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, unique = true)
    private long id;

    private Date creationDate;

    @Column(name = "endDate", nullable = false)
    private Date endDate;

    public Voting() {
        this.creationDate = new Date(System.currentTimeMillis());
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date date) {
        this.creationDate = date;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date date) {
        this.endDate = date;
    }

}
