package nl.tudelft.sem.template.association.domain.report;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("reportRepository")
public interface ReportRepository extends JpaRepository<Report,Integer> {

    List<Report> findByReporterIdAndAssociationId(String reporterId,int associationId);

    List<Report> findByViolatorIdAndAssociationId(String violatorId,int associationId);

    List<Report> findByRuleIdAndAssociationId(int ruleId,int associationId);

    List<Report> findByAssociationId(int associationId);

    boolean existsById(int Id);
}
