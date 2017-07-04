package org.larics.jetshop.repository;

import org.larics.jetshop.model.JetModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


// CrudRepository: Iterable<T> findAll();
// JpaRepository: List<T> findAll(); 
// + paging (PagingAndSortingRepository) 
// + flushing, ...

/*
 * Repository for {@link JetModel}.
 * 
 * @author Igor Laryukhin
 */
@Repository
public interface JetModelRepository extends JpaRepository<JetModel, Long> {

	JetModel findByName(String name);

	@Query("select j.id from JetModel j where j.name = :name")
	Long findIdByName(@Param("name") String name);

	Long countById(Long id);
}
