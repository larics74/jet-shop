package org.larics.jetshop.repository;

import java.util.List;

import org.larics.jetshop.model.JetOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/*
 * Repository for {@link JetOrder}.
 * 
 * @author Igor Laryukhin
 */
@Repository
public interface JetOrderRepository extends JpaRepository<JetOrder, Long> {

	JetOrder findByLineNumber(Integer lineNumber);

	Long countByCustomerId(Long customerId);

	Long countByJetModelId(Long jetModelId);

	List<JetOrder> findByCustomerId(Long customerId);

	// It will be one or no order,
	// using First to return JetOrder instead of List.
	JetOrder findFirstByIdAndCustomerId(Long id, Long customerId);

	@Query("select jo.id from JetOrder jo where jo.lineNumber = :lineNumber")
	Long findIdByLineNumber(@Param("lineNumber") Integer lineNumber);

	Long countById(Long id);
}
