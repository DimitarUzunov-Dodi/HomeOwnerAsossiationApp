package nl.tudelft.sem.template.voting.domain;

import java.util.Date;
import javax.persistence.*;

@MappedSuperclass
public abstract class Voting {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, unique = true)
    private long id;

    @Column(name = "creationDate", nullable = false)
    private Date creationDate;

    @Column(name = "endDate", nullable = false)
    private Date endDate;

    public Voting() {
        this.creationDate = new Date(System.currentTimeMillis());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
