package org.larics.jetshop.controller;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.larics.jetshop.model.Role;
import org.larics.jetshop.model.User;
import org.larics.jetshop.service.JetOrderServiceImpl;
import org.larics.jetshop.service.UserService;
import org.springframework.context.MessageSource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/*
 * Tests for {@link UserController}.
 * 
 * @author Igor Laryukhin
 */
public class UserControllerTest {

	private MockMvc mockMvc;

	private UserService userService;
	private JetOrderServiceImpl jetOrderService;
	private MessageSource messageSource;

	@Before
	public void setUp() {
		userService = mock(UserService.class);
		jetOrderService = mock(JetOrderServiceImpl.class);
		messageSource = mock(MessageSource.class);
		UserController userController =
				new UserController(userService, jetOrderService, messageSource);
		mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
	}

	@Test
	public void getRegister_When_ShouldReturnNewUser() throws Exception {
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
	public void register_WhenUserNameExists_ShouldReturnError() throws Exception {
		Long id = 1L;

		when(userService.getIdByName("user")).thenReturn(id);


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

		verify(userService, times(1)).getIdByName("user");
		verifyNoMoreInteractions(userService);
	}

	@Test
	public void register_WhenPasswordsMismatch_ShouldReturnError() throws Exception {

		when(userService.getIdByName("user")).thenReturn(null);

		mockMvc.perform(post("/register")
				.param("name", "user")
				.param("newPassword", "user1")
				.param("passwordRepeated", "user2")
				.param("role", Role.CUSTOMER.toString())
		)
		.andExpect(status().isOk())
		.andExpect(view().name("users/edit"))
		.andExpect(model().attributeHasFieldErrorCode("user", "passwordRepeated", "mismatch.user.passwordRepeated"))
		.andExpect(model().attributeExists("user"))
		.andExpect(model().attribute("action", "/register"))
		.andExpect(model().attribute("headTitle", "title.register"))
		.andExpect(model().attribute("roles", new Role[] {Role.CUSTOMER}))
		.andDo(print());

		verify(userService, times(1)).getIdByName("user");
		verifyNoMoreInteractions(userService);
	}

	@Test
	public void getOne_WhenUserNotExists_ShouldReturnErrorMessage() throws Exception {
		Long id = 1L;
		when(userService.getById(id)).thenReturn(null);

		when(messageSource.getMessage("user.not.found", null, Locale.ENGLISH))
			.thenReturn("The user not found");

		mockMvc.perform(get("/users/" + id))
		.andExpect(status().is3xxRedirection())
		.andExpect(redirectedUrl("/users"))
		.andExpect(flash().attribute("messages",
				allOf(
						hasProperty("errorMessages", hasSize(1)),
						hasProperty("errorMessages", hasItem("The user not found")),
						hasProperty("infoMessages", hasSize(0)),
						hasProperty("debugMessages", hasSize(0))
				)
		))
		.andDo(print());

		verify(userService, times(1)).getById(id);
		verifyNoMoreInteractions(userService);
}

	@Test
	public void getOne_WhenUserExists_ShouldReturnUser() throws Exception {
		User user = new User();
		user.setName("user");
		Long id = 1L;
		user.setId(id);

		when(userService.getById(id)).thenReturn(user);

		mockMvc.perform(get("/users/" + id))
		.andExpect(status().isOk())
		.andExpect(view().name("users/view"))
		.andExpect(model().hasNoErrors())
		.andExpect(model().attribute("user", user))
		.andDo(print());

		verify(userService, times(1)).getById(id);
		verifyNoMoreInteractions(userService);
	}

	@Test
	public void save_WhenNoErrors_ShouldReturnUserView() throws Exception {
		Long id = 1L;

		when(userService.getIdByName("user")).thenReturn(null);
		when(userService.save(isA(User.class))).thenReturn(id);
		when(messageSource.getMessage("user.saved", null, Locale.ENGLISH))
			.thenReturn("User saved");

		mockMvc.perform(post("/users")
				.param("name", "user")
				.param("newPassword", "user")
				.param("passwordRepeated", "user")
				.param("role", Role.CUSTOMER.toString())
		)
		.andExpect(status().is3xxRedirection())
		.andExpect(redirectedUrl("/users/" + id))
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

		verify(userService, times(1)).getIdByName("user");
		verify(userService, times(1)).save(isA(User.class));
		verifyNoMoreInteractions(userService);
	}

	@Test
	public void delete_WhenUserIsCustomer_ShouldNotDeleteAndReturnErrorMessage() throws Exception {
		Long id = 1L;

		when(userService.countById(id)).thenReturn(1L);

		when(jetOrderService.countByCustomerId(id)).thenReturn(1L);

		when(messageSource.getMessage("user.is.customer", null, Locale.ENGLISH))
			.thenReturn("The user is a customer and has orders, cannot be deleted");

		mockMvc.perform(get("/users/delete/" + id))
		.andExpect(status().is3xxRedirection())
		.andExpect(redirectedUrl("/users"))
		.andExpect(flash().attribute("messages",
				allOf(
						hasProperty("errorMessages", hasSize(1)),
						hasProperty("errorMessages", hasItem("The user is a customer and has orders, cannot be deleted")),
						hasProperty("infoMessages", hasSize(0)),
						hasProperty("debugMessages", hasSize(0))
				)
		))
		.andDo(print());

		verify(userService, times(1)).countById(id);
		verify(userService, times(0)).delete(id);
		verifyNoMoreInteractions(userService);
		verify(jetOrderService, times(1)).countByCustomerId(id);
		verifyNoMoreInteractions(jetOrderService);
	}

	@Test
	public void delete_WhenExists_ShouldDeleteAndReturnInfoMessage() throws Exception {
		Long id = 1L;

		when(userService.countById(id)).thenReturn(1L);

		when(jetOrderService.countByCustomerId(id)).thenReturn(0L);

		when(messageSource.getMessage("user.deleted", null, Locale.ENGLISH))
			.thenReturn("The user deleted");

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

		verify(userService, times(1)).countById(id);
		verify(userService, times(1)).delete(id);
		verifyNoMoreInteractions(userService);
		verify(jetOrderService, times(1)).countByCustomerId(id);
		verifyNoMoreInteractions(jetOrderService);
	}
}
