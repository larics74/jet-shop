package org.larics.jetshop.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.larics.jetshop.model.CurrentUser;
import org.larics.jetshop.model.Role;
import org.larics.jetshop.model.User;

/*
 * Tests for {@link CurrentUserServiceTest}.
 * 
 * @author Igor Laryukhin
 */
public class CurrentUserServiceTest {

	CurrentUserService service = new CurrentUserService();

	@Test
	public void canAccessUser_WhenRoleIsAdmin_ShouldReturnTrue() {
		User user = new User("user", "user", Role.ADMIN);
		CurrentUser currentUser = new CurrentUser(user);
		// whatever
		Long userId = 0L;

		assertEquals(true, service.canAccessUser(currentUser, userId));
	}

	@Test
	public void canAccessUser_WhenUserEqualsToCurrentUser_ShouldReturnTrue() {
		User user = new User("user", "user", Role.CUSTOMER);
		Long userId = 0L;
		user.setId(userId);
		CurrentUser currentUser = new CurrentUser(user);

		assertEquals(true, service.canAccessUser(currentUser, userId));
	}

	@Test
	public void canAccessUser_WhenUserNotEqualsToCurrentUserAndRoleIsNotAdmin_ShouldReturnFalse() {
		User user = new User("user", "user", Role.CUSTOMER);
		Long userId = 1L;
		user.setId(userId);
		CurrentUser currentUser = new CurrentUser(user);
		user.setId(2L);

		assertEquals(false, service.canAccessUser(currentUser, userId));
	}
}
