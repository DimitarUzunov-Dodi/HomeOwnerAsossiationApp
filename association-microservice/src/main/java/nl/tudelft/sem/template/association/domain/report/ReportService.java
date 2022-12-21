package nl.tudelft.sem.template.association.domain.report;

import nl.tudelft.sem.template.association.domain.association.AssociationService;
import nl.tudelft.sem.template.association.domain.membership.FieldNoNullException;
import nl.tudelft.sem.template.association.domain.membership.MembershipService;
import nl.tudelft.sem.template.association.domain.user.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportService {
    public final transient ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    public void addReport(MembershipService service, int associationId, String reporterId, String violatorId, int ruleId) throws FieldNoNullException, ReportInconsistentException {
        if(reporterId==null||violatorId==null||service==null) {
            throw new FieldNoNullException();
        }
        if(!service.isInAssociation(violatorId,associationId)||!service.isInAssociation(reporterId,associationId)){
            throw new ReportInconsistentException();
        }
        reportRepository.save(new Report(associationId,reporterId,violatorId,ruleId));
    }

    public boolean deleteReport(Report report){
        if(!reportRepository.existsById(report.getId())){
            return false;
        }
        reportRepository.delete(report);
        return true;
    }

    public List<Report> reportsInAssociation(AssociationService service,int associationId) throws IllegalArgumentException {
        try{
            service.getAssociationById(associationId);
        }catch (IllegalArgumentException e){
            throw e;
        }
        return reportRepository.findByAssociationId(associationId);
    }

    public List<Report> checkViolation(MembershipService service, String userId, int associationId) throws IllegalArgumentException{
        if(!service.isInAssociation(userId,associationId)){
            throw new IllegalArgumentException("The user "+ userId+" is not in the association with id"+ associationId);
        }
        return reportRepository.findByViolatorIdAndAssociationId(userId,associationId);
    }
}
