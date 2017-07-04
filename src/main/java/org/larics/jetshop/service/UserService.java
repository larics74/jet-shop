package org.larics.jetshop.service;

import java.util.List;

import org.larics.jetshop.model.User;

/*
 * A service to retrieve {@link User} data.
 * 
 * @author Igor Laryukhin
 */
public interface UserService {

	User findByName(String name);

	Long save(User user);

	List<User> getAll();

	User getById(Long id);

	void delete(Long id);

	Long countById(Long id);

	List<User> getCustomers();

	Long getIdByName(String name);

}