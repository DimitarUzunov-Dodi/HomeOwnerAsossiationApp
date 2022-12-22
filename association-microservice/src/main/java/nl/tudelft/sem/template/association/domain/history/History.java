package nl.tudelft.sem.template.association.domain.history;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "histories")
@NoArgsConstructor
public class History {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, unique = true)
    private Integer id;

    @Column(name = "associationId", nullable = false, unique = true)
    private int associationId;

    @Column(name = "events")
    @Convert(converter = EventListAttributeConverter.class)
    private List<Event> events;

    /**
     * Constructor for history with an association's id and empty event list.
     *
     * @param associationId the association this history is tied to
     */
    public History(int associationId) {
        this.associationId = associationId;
        this.events = new ArrayList<>();
        this.events.add(new Event("Association was created with ID: " + associationId, new Date()));
    }

    /**
     * Getter for the history's associationId.
     *
     * @return the history's associationId
     */
    public int getAssociationId() {
        return this.associationId;
    }

    /**
     * Getter for events.
     *
     * @return the list of events in history
     */
    public List<Event> getEvents() {
        return events;
    }

    /**
     * Setter for events.
     *
     * @param events new list of events of the history
     */
    public void setEvents(List<Event> events) {
        this.events = events;
    }

    /**
     * Adds a new event to the event list.
     *
     * @param e the event to be added
     */
    public void addEvent(Event e) {
        events.add(e);
    }

    /**
     * Getter for formatted events.
     *
     * @return the list of strings containing formatted events in history
     */
    public List<String> getFormattedEvents() {
        List<String> res = new ArrayList<>();

        for (Event e : this.events) {
            res.add(e.toString());
        }

        return res;
    }

}
