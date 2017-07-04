package org.larics.jetshop.service;

import java.util.List;

import org.larics.jetshop.model.CurrentUser;
import org.larics.jetshop.model.Role;
import org.larics.jetshop.model.User;
import org.larics.jetshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/*
 * Implementation of {@link UserService}
 * backed by {@link UserRepository}.
 * 
 * @author Igor Laryukhin
 */
@Service
public class UserServiceImpl implements UserService {

	private UserRepository userRepository;

	@Autowired
	public UserServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public User findByName(String name) {
		return userRepository.findByName(name);
	}

	@Override
	public Long save(User user) {
		return userRepository.save(user).getId();
	}

	@Override
	public List<User> getAll() {
		return userRepository.findAll();
	}

	@Override
	public User getById(Long id) {
		return userRepository.findOne(id);
	}

	@Override
	public void delete(Long id) {
		userRepository.delete(id);
	}

	@Override
	public Long countById(Long id) {
		return userRepository.countById(id);
	}

	@Override
	public List<User> getCustomers() {
		CurrentUser currentUser = getCurrentUser();
		if (currentUser.getRole() == Role.ADMIN) {
			return userRepository.findByRole(Role.CUSTOMER);
		} else {
			return userRepository.findById(currentUser.getId());
		}
	}

	@Override
	public Long getIdByName(String name) {
		return userRepository.findIdByName(name);
	}

	// TODO: move to bean?
	/*
	 * Returns CurrentUser.
	 */
	private CurrentUser getCurrentUser() {
		Authentication authentication 
			= SecurityContextHolder.getContext().getAuthentication();
		CurrentUser currentUser = null;
		try {
			currentUser = (CurrentUser) authentication.getPrincipal();
		} catch (ClassCastException e) {
			// TODO
		}
		return currentUser;
	}

}
