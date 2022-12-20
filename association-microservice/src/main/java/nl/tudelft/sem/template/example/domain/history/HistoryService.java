package nl.tudelft.sem.template.example.domain.history;

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

            List<Event> events = history.getEvents();

            return events;
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

            List<String> formattedEvents = history.getFormattedEvents();

            return formattedEvents;
        } else {
            throw new Exception("NOT FOUND");
        }
    }
}
