package nl.tudelft.sem.template.example.domain;

import java.sql.Date;
import java.time.Instant;

public abstract class Voting {

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
