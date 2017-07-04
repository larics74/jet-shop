package org.larics.jetshop.repository;

import java.util.List;

import org.larics.jetshop.model.Role;
import org.larics.jetshop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/*
 * Repository for {@link User}.
 * 
 * @author Igor Laryukhin
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	User findByName(String name);

	List<User> findByRole(Role role);

	@Query("select u.id from User u where u.name = :name")
	Long findIdByName(@Param("name") String name);

	// We need List, so added this method (instead of findOne).
	List<User> findById(Long id);

	Long countById(Long id);
}
