package nl.tudelft.sem.template.association.domain.report;

import java.util.List;
import nl.tudelft.sem.template.association.domain.association.AssociationRepository;
import nl.tudelft.sem.template.association.domain.membership.FieldNoNullException;
import nl.tudelft.sem.template.association.domain.membership.MembershipRepository;
import org.springframework.stereotype.Service;



@Service
public class ReportService {
    public final transient ReportRepository reportRepository;

    public final transient MembershipRepository membershipRepository;

    public final transient AssociationRepository associationRepository;

    /**
     * constructor.
     *
     * @param reportRepository      the repository for reports
     * @param membershipRepository the repository for membership
     * @param associationRepository the repository for association
     */
    public ReportService(ReportRepository reportRepository,
                         MembershipRepository membershipRepository, AssociationRepository associationRepository) {
        this.reportRepository = reportRepository;
        this.membershipRepository = membershipRepository;
        this.associationRepository = associationRepository;
    }

    /**
     * add a new report to the repository.
     *
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
    public void addReport(int associationId, String reporterId, String violatorId, String rule)
                            throws FieldNoNullException, ReportInconsistentException, NoSuchRuleException {
        if (reporterId == null || violatorId == null) {
            throw new FieldNoNullException();
        }
        if (!membershipRepository.existsByUserIdAndAssociationId(violatorId, associationId)
                || !membershipRepository.existsByUserIdAndAssociationId(reporterId, associationId)) {
            throw new ReportInconsistentException();
        }
        if (!associationRepository.findById(associationId).get().getRules().contains(rule)) {
            throw new NoSuchRuleException();
        }
        reportRepository.save(new Report(associationId, reporterId, violatorId, rule));
    }

    /**delete a report.
     *
     * @param report the report to delete
     * @return if the deletion is successful
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
     * @param associationId the association referring
     * @return all reports in the association
     * @throws IllegalArgumentException the association doesn't exist
     */
    public List<Report> reportsInAssociation(int associationId) throws IllegalArgumentException {
        if (associationRepository.findById(associationId).isEmpty()) {
            throw new IllegalArgumentException("there is no such association");
        }
        return reportRepository.findByAssociationId(associationId);
    }

    /**
     * get the violation report record for a user in an association.
     *
     * @param userId        user id
     * @param associationId association id
     * @return all reports that reported the user violating rules
     * @throws IllegalArgumentException the user is not in the association
     */
    public List<Report> checkViolation(String userId, int associationId) throws IllegalArgumentException {
        if (!membershipRepository.existsByUserIdAndAssociationId(userId, associationId)) {
            throw new IllegalArgumentException("The user " + userId + " is not in the association with id "
                    + associationId + ".");
        }
        return reportRepository.findByViolatorIdAndAssociationId(userId, associationId);
    }
}
