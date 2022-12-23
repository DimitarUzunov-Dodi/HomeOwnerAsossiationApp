package nl.tudelft.sem.template.association.domain.history;

import java.util.Date;

public class Notification extends Event {
    private boolean read;

    /**
     * The constructor for a notification object.
     *
     * @param description   The description of the event.
     * @param date          The date on which the event happened.
     */
    public Notification(String description, Date date) {
        super(description, date);
        this.read = false;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    @Override
    public String toString() {
        return super.toString() + " | " + this.read;
    }
}
