package nl.tudelft.sem.template.association.domain.history;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Fail.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HistoryServiceTests {

    HistoryRepository historyRepository = mock(HistoryRepository.class);
    HistoryService historyService = new HistoryService(historyRepository);
    History history;
    Event event1;
    Event event2;

    @BeforeEach
    void setup() {
        history = new History(1);
        when(historyRepository.findByAssociationId(1)).thenReturn(Optional.ofNullable(history));
        event1 = new Event("Gonna make you wonder why you even try", new Date());
        event2 = new Event("Lose yourself to dance", new Date());
    }


    @Test
    public void associationNotFoundTest() {
        when(historyRepository.findByAssociationId(13)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> historyService.addEvent(13, event1))
                .isInstanceOf(Exception.class).hasMessageContaining("NOT FOUND");
    }

    @Test
    public void getEventsTest() throws Exception {
        historyService.addEvent(1, event1);
        historyService.addEvent(1, event2);

        List<Event> events = new ArrayList<>();
        events.add(new Event("Association was created with ID: 1", new Date()));
        events.add(event1);
        events.add(event2);

        assertThat(historyService.getEvents(1)).isEqualTo(events);
    }

    @Test
    public void getFormattedEventsTest() throws Exception {
        historyService.addEvent(1, event1);
        historyService.addEvent(1, event2);

        List<String> prettyEvents = new ArrayList<>();
        prettyEvents.add(new Event("Association was created with ID: 1", new Date()).toString());
        prettyEvents.add(event1.toString());
        prettyEvents.add(event2.toString());

        assertThat(historyService.getFormattedEvents(1)).isEqualTo(prettyEvents);
    }
}
