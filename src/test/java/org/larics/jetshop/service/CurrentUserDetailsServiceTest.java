package org.larics.jetshop.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.larics.jetshop.model.CurrentUser;
import org.larics.jetshop.model.Role;
import org.larics.jetshop.model.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/*
 * Tests for {@link CurrentUserDetailsServiceTest}.
 * 
 * @author Igor Laryukhin
 */
public class CurrentUserDetailsServiceTest {

	private UserService userService;
	private CurrentUserDetailsService currentUserDetailsService;

	@Before
	public void setUp() {
		userService = mock(UserService.class);
		currentUserDetailsService = new CurrentUserDetailsService(userService);
	}

	@Test
	public void loadUserByUsername_WhenUserExists_ShouldReturnCurrentUser() {
		User user = new User("user", "password", Role.CUSTOMER);
		CurrentUser expectedCurrentUser = new CurrentUser(user);

		when(userService.findByName(user.getName())).thenReturn(user);

		assertEquals(expectedCurrentUser, currentUserDetailsService.loadUserByUsername(user.getName()));
	}

	@Test(expected=UsernameNotFoundException.class)
	public void loadUserByUsername_WhenUserNotExists_ShouldThrowUsernameNotFoundException() {
		User user = new User("user", "password", Role.CUSTOMER);

		when(userService.findByName(user.getName())).thenReturn(null);

		currentUserDetailsService.loadUserByUsername(user.getName());
	}
}
