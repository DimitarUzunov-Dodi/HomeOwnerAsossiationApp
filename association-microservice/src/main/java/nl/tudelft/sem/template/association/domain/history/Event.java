package nl.tudelft.sem.template.association.domain.history;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String dateString = sdf.format(date);
        return dateString + " | " + description;
    }
}
