package org.larics.jetshop.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.hasItem;
//Eclipse mistakenly shows this method as deprecated, though it's not:
//public static <T> org.hamcrest.Matcher<T> is(T value)
//Eclipse thinks, it's the following method:
//public static <T> org.hamcrest.Matcher<T> is(java.lang.Class<T> type)
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertNull;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.larics.jetshop.model.Role;
import org.larics.jetshop.model.User;
import org.larics.jetshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

/*
 * Tests for {@link UserController}.
 * 
 * @author Igor Laryukhin
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Test
	@WithUserDetails("user")
	public void getRegister_WhenUserAuthenticated_ShouldReturnForbidden() throws Exception {
		mockMvc.perform(get("/register"))
		.andExpect(status().isForbidden())
		.andDo(print());
	}

	@Test
	public void getRegister_WhenUserNotAuthenticated_ShouldReturnRegisterPageWithNewUser() throws Exception {
		mockMvc.perform(get("/register"))
		.andExpect(status().isOk())
		.andExpect(view().name("users/edit"))
		.andExpect(model().hasNoErrors())
		.andExpect(model().attribute("user", 
				allOf(
						hasProperty("id", nullValue()),
						hasProperty("name", nullValue())
				)
		))
		.andExpect(model().attribute("action", "/register"))
		.andExpect(model().attribute("headTitle", "title.register"))
		.andExpect(model().attribute("roles", new Role[] {Role.CUSTOMER}))
		.andDo(print());
	}

	@Test
	@WithUserDetails("user")
	public void register_WhenUserAuthenticated_ShouldReturnForbidden() throws Exception {
		mockMvc.perform(post("/register"))
		.andExpect(status().isForbidden())
		.andDo(print());
	}

	@Test
	public void register_WhenUserNameExists_ShouldReturnError() throws Exception {
		mockMvc.perform(post("/register")
				.param("name", "user")
				.param("newPassword", "user")
				.param("passwordRepeated", "user")
				.param("role", Role.CUSTOMER.toString())
		)
		.andExpect(status().isOk())
		.andExpect(view().name("users/edit"))
		.andExpect(model().attributeHasFieldErrorCode("user", "name", "duplicate.user.name"))
		.andExpect(model().attributeExists("user"))
		.andExpect(model().attribute("action", "/register"))
		.andExpect(model().attribute("headTitle", "title.register"))
		.andExpect(model().attribute("roles", new Role[] {Role.CUSTOMER}))
		.andDo(print());
	}

	@Test
	public void getAll_WhenUserNotAuthenticated_ShouldRedirectToLoginPage() throws Exception {
		mockMvc.perform(get("/users"))
		.andExpect(status().is3xxRedirection())
		.andExpect(redirectedUrlPattern("**/login"))
		.andDo(print());
	}

	@Test
	@WithUserDetails("user")
	public void getAll_WhenUserIsNotAdmin_ShouldReturnForbidden() throws Exception {
		mockMvc.perform(get("/users"))
		.andExpect(status().isForbidden())
		.andDo(print());
	}

	@Test
	@WithUserDetails("admin")
	public void getAll_WhenUserIsAdmin_ShouldReturnOk() throws Exception {
		mockMvc.perform(get("/users"))
		.andExpect(status().isOk())
		.andDo(print());
	}

	@Test
	public void getOne_WhenUserIsNotAuthenticated_ShouldRedirectToLoginPage() throws Exception {
		mockMvc.perform(get("/users/2"))
		.andExpect(status().is3xxRedirection())
		.andExpect(redirectedUrlPattern("**/login"))
		.andDo(print());
	}

	@Test
	@WithUserDetails("user")
	public void getOne_WhenUserIsTheSameUser_ShouldReturnOk() throws Exception {
		Long id = userRepository.findIdByName("user");

		mockMvc.perform(get("/users/" + id))
		.andExpect(status().isOk())
		.andDo(print());
	}

	@Test
	@WithUserDetails("sched")
	public void getOne_WhenUserIsNotTheSameUser_ShouldReturnForbidden() throws Exception {
		Long id = userRepository.findIdByName("user");

		mockMvc.perform(get("/users/" + id))
		.andExpect(status().isForbidden())
		.andDo(print());
	}

	@Test
	@WithUserDetails("admin")
	public void getOne_WhenUserIsAdmin_ShouldReturnOk() throws Exception {
		Long id = userRepository.findIdByName("user");

		mockMvc.perform(get("/users/" + id))
		.andExpect(status().isOk())
		.andDo(print());
	}

	@Test
	@WithUserDetails("sched")
	public void getNew_WhenUserIsNotAdmin_ShouldReturnForbidden() throws Exception {
		mockMvc.perform(get("/users/create"))
		.andExpect(status().isForbidden())
		.andDo(print());
	}

	@Test
	@WithUserDetails("admin")
	public void getNew_WhenUserIsAdmin_ShouldReturnOk() throws Exception {
		mockMvc.perform(get("/users/create"))
		.andExpect(status().isOk())
		.andDo(print());
	}

	@Test
	public void getUpdatable_WhenUserIsNotAuthenticated_ShouldRedirectToLoginPage() throws Exception {
		mockMvc.perform(get("/users/edit/2"))
		.andExpect(status().is3xxRedirection())
		.andExpect(redirectedUrlPattern("**/login"))
		.andDo(print());
	}

	@Test
	@WithUserDetails("sched")
	public void getUpdatable_WhenUserIsDifferent_ShouldReturnForbidden() throws Exception {
		Long id = userRepository.findIdByName("user");

		mockMvc.perform(get("/users/edit/" + id))
		.andExpect(status().isForbidden())
		.andDo(print());
	}

	@Test
	@WithUserDetails("user")
	public void getUpdatable_WhenUserIsTheSame_ShouldReturnOk() throws Exception {
		Long id = userRepository.findIdByName("user");
		User user = userRepository.findOne(id);

		mockMvc.perform(get("/users/edit/" + id))
		.andExpect(status().isOk())
		.andExpect(authenticated().withUsername("user"))
		.andExpect(model().hasNoErrors())
		.andExpect(model().attribute("user",
				allOf(
						hasProperty("id", is(id)),
						hasProperty("name", is(user.getName())),
						// TODO
						// To verify password the matches() method must be used.
						// It can be implemented by overriding equals() for User
						// or by creating new Matcher.
//						hasProperty("password", ...),
						hasProperty("newPassword", is(nullValue())),
						hasProperty("passwordRepeated", is(nullValue())),
						hasProperty("role", is(user.getRole()))
				)
		))
		.andDo(print());
	}

	@Test
	@WithUserDetails("admin")
	public void getUpdatable_WhenUserIsAdmin_ShouldReturnOk() throws Exception {
		Long id = 3L;
		User user = userRepository.findOne(id);

		mockMvc.perform(get("/users/edit/" + id))
		.andExpect(status().isOk())
		.andExpect(authenticated().withUsername("admin"))
		.andExpect(model().hasNoErrors())
		.andExpect(model().attribute("user",
				allOf(
						hasProperty("id", is(id)),
						hasProperty("name", is(user.getName())),
						// TODO
						// To verify password the matches() method must be used.
						// It can be implemented by overriding equals() for User
						// or by creating new Matcher.
//						hasProperty("password", ...),
						hasProperty("newPassword", is(nullValue())),
						hasProperty("passwordRepeated", is(nullValue())),
						hasProperty("role", is(user.getRole()))
				)
		))
		.andDo(print());
	}

	@Test
	public void save_WhenUserIsNotAuthenticated_ShouldRedirectToLoginPage() throws Exception {
		mockMvc.perform(post("/users"))
		.andExpect(status().is3xxRedirection())
		.andExpect(redirectedUrlPattern("**/login"))
		.andDo(print());
	}

	@Test
	@WithUserDetails("sched")
	public void save_WhenNotAdminUserSavesNewUser_ShouldReturnForbidden() throws Exception {
		mockMvc.perform(post("/users")
				// no id - new user
				.param("name", "user")
				.param("newPassword", "user")
				.param("passwordRepeated", "user")
				.param("role", Role.CUSTOMER.toString())
		)
		.andExpect(status().isForbidden())
		.andDo(print());
	}

	@Test
	@WithUserDetails("sched")
	public void save_WhenNotAdminUserSavesDifferentUser_ShouldReturnForbidden() throws Exception {
		Long id = 1L;
		Long schedId = userRepository.findIdByName("sched");
		assertThat(schedId).isNotEqualTo(id);

		mockMvc.perform(post("/users")
				.param("id", id.toString())
				.param("name", "user")
				.param("newPassword", "user")
				.param("passwordRepeated", "user")
				.param("role", Role.CUSTOMER.toString())
		)
		.andExpect(status().isForbidden())
		.andDo(print());
	}

	@Test
	@WithUserDetails("admin")
	public void save_WhenAdminUserSavesNewDifferentUserAndNameExists_ShouldReturnError() throws Exception {
		mockMvc.perform(post("/users")
				.param("name", "user")
				.param("newPassword", "user")
				.param("passwordRepeated", "user")
				.param("role", Role.CUSTOMER.toString())
		)
		.andExpect(status().isOk())
		.andExpect(view().name("users/edit"))
		.andExpect(model().attributeHasFieldErrorCode("user", "name", "duplicate.user.name"))
		.andExpect(model().attributeExists("user"))
		.andDo(print());
	}

	@Test
	@WithUserDetails("user")
	public void save_WhenNotAdminUserSavesTheSameUserAndNoErrors_ShouldReturnUserView() throws Exception {
		Long id = userRepository.findIdByName("user");

		mockMvc.perform(post("/users")
				.param("id", id.toString())
				.param("name", "user")
				.param("newPassword", "user")
				.param("passwordRepeated", "user")
				.param("role", Role.CUSTOMER.toString())
		)
		.andExpect(status().is3xxRedirection())
		// TODO
//		.andExpect(redirectedUrlPattern("/users/{\\d+}"))
		.andExpect(model().hasNoErrors())
		.andExpect(flash().attribute("messages",
				allOf(
						hasProperty("errorMessages", hasSize(0)),
						hasProperty("infoMessages", hasSize(1)),
						hasProperty("infoMessages", hasItem("User saved")),
						hasProperty("debugMessages", hasSize(0))
				)
		))
		.andDo(print());
	}

	@Test
	@WithUserDetails("admin")
	public void save_WhenAdminUserSavesNewDifferentUserAndNoErrors_ShouldReturnUserView() throws Exception {
		mockMvc.perform(post("/users")
				.param("name", "user1")
				.param("newPassword", "user1")
				.param("passwordRepeated", "user1")
				.param("role", Role.CUSTOMER.toString())
		)
		.andExpect(status().is3xxRedirection())
		// TODO
//		.andExpect(redirectedUrlPattern("/users/{\\d+}"))
		.andExpect(model().hasNoErrors())
		.andExpect(flash().attribute("messages",
				allOf(
						hasProperty("errorMessages", hasSize(0)),
						hasProperty("infoMessages", hasSize(1)),
						hasProperty("infoMessages", hasItem("User saved")),
						hasProperty("debugMessages", hasSize(0))
				)
		))
		.andDo(print());

		userRepository.delete(userRepository.findIdByName("user1"));
	}

	@Test
	@WithUserDetails("sched")
	public void delete_WhenUserIsNotAdmin_ShouldReturnForbidden() throws Exception {
		Long id = userRepository.findIdByName("sched");

		mockMvc.perform(get("/users/delete/" + id))
		.andExpect(status().isForbidden())
		.andDo(print());
	}

	@Test
	@WithUserDetails("admin")
	public void delete_WhenUserIsAdmin_ShouldDeleteAndReturnInfoMessage() throws Exception {
		User user = new User("userToDelete", 
				new BCryptPasswordEncoder().encode("user"),
				Role.CUSTOMER);
		Long id = userRepository.save(user).getId();

		mockMvc.perform(get("/users/delete/" + id))
		.andExpect(status().is3xxRedirection())
		.andExpect(redirectedUrl("/users"))
		.andExpect(flash().attribute("messages",
				allOf(
						hasProperty("errorMessages", hasSize(0)),
						hasProperty("infoMessages", hasSize(1)),
						hasProperty("infoMessages", hasItem("The user deleted")),
						hasProperty("debugMessages", hasSize(0))
				)
		))
		.andDo(print());

		assertNull(userRepository.findOne(id));
	}

}
