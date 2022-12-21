package nl.tudelft.sem.template.association.models;

import lombok.Data;

@Data
public class UserAssociationRequestModelInternal {
    private int userId;
    private int associationId;
}
