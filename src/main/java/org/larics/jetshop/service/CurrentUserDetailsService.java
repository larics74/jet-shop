package org.larics.jetshop.service;


import org.larics.jetshop.model.CurrentUser;
import org.larics.jetshop.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
 * Used by Spring Security to verify user's password.
 * The service is injected in SecurityConfig.
 * 
 * @author Igor Laryukhin
 */
@Service
public class CurrentUserDetailsService implements UserDetailsService {

	private UserService userService;
	
	@Autowired
	public CurrentUserDetailsService(UserService userService) {
		this.userService = userService;
	}

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) 
			throws UsernameNotFoundException {

		User user = userService.findByName(username);
		
		if (user == null) {
			throw new UsernameNotFoundException(
					String.format("User with name=%s was not found", username));
		}
		
		return new CurrentUser(user);
	}

}
