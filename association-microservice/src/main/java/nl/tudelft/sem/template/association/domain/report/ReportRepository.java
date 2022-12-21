package nl.tudelft.sem.template.association.domain.report;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("reportRepository")
public interface ReportRepository extends JpaRepository<Report, Integer> {

    /**findByViolatorIdAndAssociationId.
     *
     * @param violatorId violator id
     * @param associationId association id
     *
     * @return list of reports accordingly
     */
    List<Report> findByViolatorIdAndAssociationId(int violatorId, int associationId);

    /**findByAssociationId.
     *
     * @param associationId association id
     *
     * @return list of reports accordingly
     */
    List<Report> findByAssociationId(int associationId);

    /**if the report exists.
     *
     * @param id the id of the report
     *
     * @return the existence of the report
     */
    boolean existsById(int id);
}
