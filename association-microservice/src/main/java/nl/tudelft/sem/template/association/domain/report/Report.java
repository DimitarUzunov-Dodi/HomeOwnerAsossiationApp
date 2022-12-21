package nl.tudelft.sem.template.association.domain.report;

import java.util.Date;
import javax.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reports")
@NoArgsConstructor
public class Report {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, unique = true)
    private int id;

    @Column(name = "association_id", nullable = false)
    private int associationId;

    @Column(name = "reporter_id", nullable = false)
    private int reporterId;

    @Column(name = "violator_id", nullable = false)
    private int violatorId;

    @Column(name = "report_date", nullable = false)
    private Date reportDate;

    @Column(name = "rule")
    private String rule;

    /**constructor.
     *
     * @param associationId association id
     * @param reporterId reporter id
     * @param violatorId violator id
     * @param rule rule
     */
    public Report(int associationId, int reporterId, int violatorId, String rule) {
        this.associationId = associationId;
        this.reporterId = reporterId;
        this.violatorId = violatorId;
        this.rule = rule;
        this.reportDate = new Date(System.currentTimeMillis());
    }

    /**getter.
     *
     * @return id
     */
    public int getId() {
        return id;
    }

    /**getter.
     *
     * @return association id
     */
    public int getAssociationId() {
        return associationId;
    }

    /**getter.
     *
     * @return reporter id
     */
    public int getReporterId() {
        return reporterId;
    }

    /**getter.
     *
     * @return violator id
     */
    public int getViolatorId() {
        return violatorId;
    }

    /**getter.
     *
     * @return report date
     */
    public Date getReportDate() {
        return reportDate;
    }

    /**getter.
     *
     * @return rule
     */
    public String getRule() {
        return rule;
    }
}
