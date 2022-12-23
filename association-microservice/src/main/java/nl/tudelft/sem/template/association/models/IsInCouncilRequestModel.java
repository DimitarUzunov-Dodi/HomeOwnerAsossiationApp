package nl.tudelft.sem.template.association.models;

import lombok.Data;

@Data
public class IsInCouncilRequestModel {
    private int associationId;
    private String userId;
}
