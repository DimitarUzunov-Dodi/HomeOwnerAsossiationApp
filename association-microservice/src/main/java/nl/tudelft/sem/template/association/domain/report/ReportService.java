package nl.tudelft.sem.template.association.domain.report;

import java.util.List;
import nl.tudelft.sem.template.association.domain.association.AssociationService;
import nl.tudelft.sem.template.association.domain.membership.FieldNoNullException;
import nl.tudelft.sem.template.association.domain.membership.MembershipService;
import org.springframework.stereotype.Service;



@Service
public class ReportService {
    public final transient ReportRepository reportRepository;

    /**
     * constructor.
     *
     * @param reportRepository the repository for reports
     */
    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    /**
     * add a new report to the repository.
     *
     * @param service       service for consistancy checking
     * @param associationId association id
     * @param reporterId    reporter id
     * @param violatorId    violator id
     * @param rule          rule
     * @throws FieldNoNullException        throws if there is null parameter
     * @throws ReportInconsistentException exception for inconsistancy
     *                                     the method will check if the reporter and violator are actually in the association
     *                                     and raise an exception otherwise
     *                                     if not, the report will be saved to the repo
     */
    public void addReport(MembershipService service,
                          int associationId, String reporterId, String violatorId, String rule)
                            throws FieldNoNullException, ReportInconsistentException {
        if (reporterId == null || violatorId == null || service == null) {
            throw new FieldNoNullException();
        }
        if (!service.isInAssociation(violatorId, associationId) || !service.isInAssociation(reporterId, associationId)) {
            throw new ReportInconsistentException();
        }
        reportRepository.save(new Report(associationId, reporterId, violatorId, rule));
    }

    /**delete a report.
     *
     * @param report the report to delete
     * @return if the deletion is successfull
     */
    public boolean deleteReport(Report report) {
        if (!reportRepository.existsById(report.getId())) {
            return false;
        }
        reportRepository.delete(report);
        return true;
    }

    /**
     * get the reports from one association.
     *
     * @param service       helper service
     * @param associationId the association referring
     * @return all reports in the association
     * @throws IllegalArgumentException the association doesn't exist
     */
    public List<Report> reportsInAssociation(AssociationService service, int associationId) throws IllegalArgumentException {
        try {
            service.getAssociationById(associationId);
        } catch (IllegalArgumentException e) {
            throw e;
        }
        return reportRepository.findByAssociationId(associationId);
    }

    /**
     * get the violation report record for a user in an association.
     *
     * @param service       helper service
     * @param userId        user id
     * @param associationId association id
     * @return all reports that reported the user violating rules
     * @throws IllegalArgumentException the user is not in the association
     */
    public List<Report> checkViolation(MembershipService service,
                                       String userId, int associationId) throws IllegalArgumentException {
        if (!service.isInAssociation(userId, associationId)) {
            throw new IllegalArgumentException("The user " + userId + " is not in the association with id" + associationId);
        }
        return reportRepository.findByViolatorIdAndAssociationId(userId, associationId);
    }
}
