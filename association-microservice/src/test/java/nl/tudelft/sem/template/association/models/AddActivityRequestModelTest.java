package nl.tudelft.sem.template.association.models;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.Date;
import org.junit.jupiter.api.Test;

class AddActivityRequestModelTest {

    @Test
    void isComplete() {
        AddActivityRequestModel model = new AddActivityRequestModel();
        model.setAssociationId(1);
        model.setEventName("Test event 1");
        model.setDescription("description");
        model.setStartingDate(new Date());
        model.setExpirationDate(new Date());
        assertThat(model.isComplete()).isTrue();
    }

    @Test
    void isEmpty() {
        AddActivityRequestModel model = new AddActivityRequestModel();
        assertThat(model.isComplete()).isFalse();
    }

    @Test
    void isMissingAssociationId() {
        AddActivityRequestModel model = new AddActivityRequestModel();
        model.setAssociationId(null);
        model.setEventName("Test event 1");
        model.setDescription("description");
        model.setStartingDate(new Date());
        model.setExpirationDate(new Date());
        assertThat(model.isComplete()).isFalse();
    }

    @Test
    void isMissingName() {
        AddActivityRequestModel model = new AddActivityRequestModel();
        model.setAssociationId(1);
        model.setEventName(null);
        model.setDescription("description");
        model.setStartingDate(new Date());
        model.setExpirationDate(new Date());
        assertThat(model.isComplete()).isFalse();
    }

    @Test
    void isMissingDescription() {
        AddActivityRequestModel model = new AddActivityRequestModel();
        model.setAssociationId(1);
        model.setEventName("Test event 1");
        model.setDescription(null);
        model.setStartingDate(new Date());
        model.setExpirationDate(new Date());
        assertThat(model.isComplete()).isFalse();
    }

    @Test
    void isMissingStartingDate() {
        AddActivityRequestModel model = new AddActivityRequestModel();
        model.setAssociationId(1);
        model.setEventName("Test event 1");
        model.setDescription("description");
        model.setStartingDate(null);
        model.setExpirationDate(new Date());
        assertThat(model.isComplete()).isFalse();
    }

    @Test
    void isMissingExpirationDate() {
        AddActivityRequestModel model = new AddActivityRequestModel();
        model.setAssociationId(1);
        model.setEventName("Test event 1");
        model.setDescription("description");
        model.setStartingDate(new Date());
        model.setExpirationDate(null);
        assertThat(model.isComplete()).isFalse();
    }


}