package nl.tudelft.sem.template.association.domain.report;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "reports")
@NoArgsConstructor
public class Report {
    @Id
    @GeneratedValue
    @Column(name = "id",nullable = false,unique = true)
    private int id;

    @Column(name = "association_id")
    private int associationId;

    @Column(name = "reporter_id",nullable = false)
    private String reporterId;

    @Column(name = "violator_id",nullable = false)
    private String violatorId;

    @Column(name = "report_date",nullable = false)
    private Date reportDate;

    @Column(name = "rule_id")
    private int ruleId;

    public Report(int associationId, String reporterId, String violatorId, int ruleId) {
        this.associationId = associationId;
        this.reporterId = reporterId;
        this.violatorId = violatorId;
        this.ruleId = ruleId;
        this.reportDate=new Date(System.currentTimeMillis());
    }

    public int getId() {
        return id;
    }

    public int getAssociationId() {
        return associationId;
    }

    public String getReporterId() {
        return reporterId;
    }

    public String getViolatorId() {
        return violatorId;
    }

    public Date getReportDate() {
        return reportDate;
    }

    public int getRuleId() {
        return ruleId;
    }
}
