package nl.tudelft.sem.template.example.domain.history;


import java.util.Date;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class Event {
    private transient String description;
    private transient Date date;


    public Event(String description, Date date) {
        this.description = description;
        this.date = date;
    }

    public String getDescription() {
        return this.description;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(Date date) {
        this.date = date;
    }


    @Override
    public String toString() {
        return date.toString() + System.lineSeparator() + description;
    }
}
