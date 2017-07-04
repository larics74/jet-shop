package org.larics.jetshop.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.larics.jetshop.model.User;
import org.larics.jetshop.repository.UserRepository;

/*
 * Tests for {@link UserServiceImpl}.
 * 
 * @author Igor Laryukhin
 */
public class UserServiceImplTest {

	private UserRepository userRepository;
	private UserService userService;

	@Before
	public void setUp() {
		userRepository = mock(UserRepository.class);
		userService = new UserServiceImpl(userRepository);
	}

	@Test
	public void findByName() {
		User expectedUser = new User();
		String name = "user";
		expectedUser.setName(name);

		when(userRepository.findByName(name)).thenReturn(expectedUser);

		assertEquals(expectedUser, userService.findByName(name));
	}

	@Test
	public void save() {
		User unsaved = new User();
		unsaved.setName("user");
		User saved = unsaved;
		Long expectedId = 1L;
		saved.setId(expectedId);

		when(userRepository.save(unsaved)).thenReturn(saved);

		assertEquals(expectedId, userService.save(unsaved));
	}

	@Test
	public void getAll() {
		List<User> expectedUsers = new ArrayList<>();
		User user1 = new User();
		user1.setId(1L);
		user1.setName("user1");
		expectedUsers.add(user1);
		User user2 = new User();
		user2.setId(2L);
		user2.setName("user2");
		expectedUsers.add(user2);

		when(userRepository.findAll()).thenReturn(expectedUsers);

		assertEquals(expectedUsers, userService.getAll());
}

	@Test
	public void getById() {
		Long id = 1L;
		User expectedUser = new User();
		expectedUser.setId(id);
		expectedUser.setName("user");

		when(userRepository.findOne(id)).thenReturn(expectedUser);

		assertEquals(expectedUser, userService.getById(id));
	}

	@Test
	public void delete() {
		Long id = 1L;

		userService.delete(id);

		verify(userRepository, times(1)).delete(id);
	}

	@Test
	public void countById() {
		Long expectedCount = 1L;
		Long id = 1L;

		when(userRepository.countById(id)).thenReturn(expectedCount);

		assertEquals(expectedCount, userService.countById(id));
	}

	@Test
	public void getCustomers() {
		// security involved, see integration tests
	}

	@Test
	public void getIdByName() {
		String name = "user";
		Long expectedId = 1L;

		when(userRepository.findIdByName(name)).thenReturn(expectedId);

		assertEquals(expectedId, userService.getIdByName(name));
	}
}
