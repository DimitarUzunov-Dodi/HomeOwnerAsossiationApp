package nl.tudelft.sem.template.example.models;

import lombok.Data;

import java.util.Set;

@Data
public class UpdateCouncilRequestModel {
    private int associationId;
    private Set<Integer> council;
}
