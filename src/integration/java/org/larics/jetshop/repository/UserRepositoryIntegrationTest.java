package org.larics.jetshop.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.larics.jetshop.model.Role;
import org.larics.jetshop.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

/*
 * Tests for {@link UserRepository}.
 * 
 * @author Igor Laryukhin
 */
@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepositoryIntegrationTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private UserRepository userRepository;

	@Test
	public void findByName() {
		String name = "user";
		User user = new User(name, 
				new BCryptPasswordEncoder().encode("user"),
				Role.CUSTOMER);
		entityManager.persistAndFlush(user);

		User userFound = userRepository.findByName(name);

		assertThat(userFound.getName()).isEqualTo(name);
	}
	
	@Test
	public void findByRole() {
		User expectedUser1 = new User("user1", 
				new BCryptPasswordEncoder().encode("user1"),
				Role.CUSTOMER);
		entityManager.persistAndFlush(expectedUser1);
		User user2 = new User("user2", 
				new BCryptPasswordEncoder().encode("user2"),
				Role.ADMIN);
		entityManager.persistAndFlush(user2);
		User expectedUser3 = new User("user3", 
				new BCryptPasswordEncoder().encode("user3"),
				Role.CUSTOMER);
		entityManager.persistAndFlush(expectedUser3);

		List<User> users = userRepository.findByRole(Role.CUSTOMER);

		assertThat(users)
			.containsExactlyInAnyOrder(expectedUser1, expectedUser3);
	}

	@Test
	public void findIdByName() {
		User user1 = new User("user1", 
				new BCryptPasswordEncoder().encode("user1"),
				Role.CUSTOMER);
		entityManager.persistAndFlush(user1);
		User user2 = new User("user2", 
				new BCryptPasswordEncoder().encode("user2"),
				Role.ADMIN);
		Long expectedId = entityManager.persistAndFlush(user2).getId();
		User user3 = new User("user3", 
				new BCryptPasswordEncoder().encode("user3"),
				Role.CUSTOMER);
		entityManager.persistAndFlush(user3);

		Long actualId = userRepository.findIdByName("user2");

		assertThat(actualId).isEqualTo(expectedId);
	}

	@Test
	public void findById() {
		User user1 = new User("user1", 
				new BCryptPasswordEncoder().encode("user1"),
				Role.CUSTOMER);
		entityManager.persistAndFlush(user1);
		User user2 = new User("user2", 
				new BCryptPasswordEncoder().encode("user2"),
				Role.ADMIN);
		entityManager.persistAndFlush(user2);
		User expectedUser3 = new User("user3", 
				new BCryptPasswordEncoder().encode("user3"),
				Role.CUSTOMER);
		Long id = entityManager.persistAndFlush(expectedUser3).getId();

		List<User> users = userRepository.findById(id);

		assertThat(users)
			.containsExactlyInAnyOrder(expectedUser3);
	}

	@Test
	public void countById() {
		Long expectedCount = 1L;
		User user1 = new User("user1", 
				new BCryptPasswordEncoder().encode("user1"),
				Role.CUSTOMER);
		entityManager.persistAndFlush(user1);
		User user2 = new User("user2", 
				new BCryptPasswordEncoder().encode("user2"),
				Role.ADMIN);
		entityManager.persistAndFlush(user2);
		User user3 = new User("user3", 
				new BCryptPasswordEncoder().encode("user3"),
				Role.CUSTOMER);
		Long id = entityManager.persistAndFlush(user3).getId();

		Long actualCount = userRepository.countById(id);

		assertThat(actualCount).isEqualTo(expectedCount);
	}
}
