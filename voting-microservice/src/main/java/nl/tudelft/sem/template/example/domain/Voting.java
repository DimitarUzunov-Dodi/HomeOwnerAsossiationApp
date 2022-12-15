package nl.tudelft.sem.template.example.domain;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.sql.Date;

@MappedSuperclass
public abstract class Voting {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, unique = true)
    private long id;
    private Date date;

    public Voting() {
        this.date = new Date(System.currentTimeMillis());
    }

    public Date getDate() {
        return date;
    }

    public abstract void createVote();

    public abstract boolean verify();

    public abstract String castVote();

    public abstract String getResults();

}
