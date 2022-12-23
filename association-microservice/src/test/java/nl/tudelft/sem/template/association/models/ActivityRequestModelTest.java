package nl.tudelft.sem.template.association.models;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.Date;
import org.junit.jupiter.api.Test;

class ActivityRequestModelTest {

    @Test
    void isComplete() {
        ActivityRequestModel model = new ActivityRequestModel("Test event 1", "description", new Date(), new Date());
        assertThat(model.isComplete()).isTrue();
    }

    @Test
    void isEmpty() {
        ActivityRequestModel model = new ActivityRequestModel(null, null, null, null);
        assertThat(model.isComplete()).isFalse();
    }

    @Test
    void isMissingName() {
        ActivityRequestModel model = new ActivityRequestModel(null, "description", new Date(), new Date());
        assertThat(model.isComplete()).isFalse();
    }

    @Test
    void isMissingDescription() {
        ActivityRequestModel model = new ActivityRequestModel("Test event 3", null, new Date(), new Date());
        assertThat(model.isComplete()).isFalse();
    }

    @Test
    void isMissingStartingDate() {
        ActivityRequestModel model = new ActivityRequestModel("Test event 4", "description", null, new Date());
        assertThat(model.isComplete()).isFalse();
    }

    @Test
    void isMissingExpirationDate() {
        ActivityRequestModel model = new ActivityRequestModel("Test event 5", "description", new Date(), null);
        assertThat(model.isComplete()).isFalse();
    }


}