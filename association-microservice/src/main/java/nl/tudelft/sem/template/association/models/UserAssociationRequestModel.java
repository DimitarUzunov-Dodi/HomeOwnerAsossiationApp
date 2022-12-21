package nl.tudelft.sem.template.association.models;

import lombok.Data;

@Data
public class UserAssociationRequestModel {
    private String userId;
    private int associationId;
}
