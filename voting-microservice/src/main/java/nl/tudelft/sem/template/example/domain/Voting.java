package nl.tudelft.sem.template.example.domain;

import java.sql.Date;
import javax.persistence.*;

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

    public void setDate(Date date) {
        this.date = date;
    }

}
