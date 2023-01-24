package nl.tudelft.sem.template.association.domain.history;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class HistoryService {
    private final transient HistoryRepository historyRepository;

    /**
     * Instantiates a HistoryService with a repository for the history information.
     *
     * @param historyRepository the history repository
     */
    public HistoryService(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    /**
     * Getter for the list of events in an association's history.
     *
     * @param associationId the association's id
     * @return the list of events in the association's history
     * @throws Exception if the association doesn't exist, or it doesn't have a history
     */
    public List<Event> getEvents(int associationId) throws Exception {
        Optional<History> optionalHistory = historyRepository.findByAssociationId(associationId);

        if (optionalHistory.isPresent()) {
            History history = optionalHistory.get();
            return history.getEvents();
        } else {
            throw new Exception("NOT FOUND");
        }
    }

    /**
     * Adds an event to the history of an association.
     *
     * @param associationId associationId of history repo
     * @param event event to be added
     * @throws Exception if the history or association don't exist
     */
    public void addEvent(int associationId, Event event) throws Exception {
        Optional<History> optionalHistory = historyRepository.findByAssociationId(associationId);

        if (optionalHistory != null && optionalHistory.isPresent()) {
            History history = optionalHistory.get();
            history.addEvent(event);
        } else {
            throw new Exception("NOT FOUND");
        }
    }


    /**
     * Getter for the list of events in string format in an association's history.
     *
     * @param associationId the association's id
     * @return the list of events in the association's history in string format
     * @throws Exception if the association doesn't exist, or it doesn't have a history
     */
    public List<String> getFormattedEvents(int associationId) throws Exception {
        Optional<History> optionalHistory = historyRepository.findByAssociationId(associationId);

        if (optionalHistory.isPresent()) {
            History history = optionalHistory.get();
            return history.getFormattedEvents();
        } else {
            throw new Exception("NOT FOUND");
        }
    }

    /**
     * Return a string consisting the association's entire history.
     *
     * @param associationId         the association's ID
     * @return                      the association's history
     */
    public String getHistoryString(int associationId) throws Exception {
        List<String> formattedEvents = getFormattedEvents(associationId);

        StringBuilder sb = new StringBuilder();

        for (String str : formattedEvents) {
            sb.append(str).append(System.lineSeparator());
        }

        return sb.toString();
    }
}
