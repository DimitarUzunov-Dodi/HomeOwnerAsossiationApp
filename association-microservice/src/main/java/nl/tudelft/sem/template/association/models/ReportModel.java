package nl.tudelft.sem.template.association.models;

import lombok.Data;

/*
 model for reporting json
 */
@Data
public class ReportModel {
    int associationId;
    int reporterId;
    int violatorId;
    String rule;
}
