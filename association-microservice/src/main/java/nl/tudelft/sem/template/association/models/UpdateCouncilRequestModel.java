package nl.tudelft.sem.template.association.models;

import java.util.Set;
import lombok.Data;

@Data
public class UpdateCouncilRequestModel {
    private int associationId;
    private Set<String> council;
}
