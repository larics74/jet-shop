package org.larics.jetshop.controller;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

/*
 * Tests for {@link LoginController}.
 * 
 * @author Igor Laryukhin
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class LoginControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

//	TODO:
//	@Test
//	@WithUserDetails("user")
//	public void getRegister_WhenUserIsAuthenticated_ShouldReturnForbidden() throws Exception {
//		mockMvc.perform(get("/login"))
//		.andExpect(status().isForbidden())
//		.andDo(print());
//	}

	@Test
	public void login_WhenUserIsNotAuthenticated_ShouldReturnLoginPage() throws Exception {
		mockMvc.perform(get("/login"))
		.andExpect(status().isOk())
		.andExpect(view().name("login/login"))
		.andExpect(model().hasNoErrors())
		.andExpect(model().attribute("messages",
				allOf(
						hasProperty("errorMessages", hasSize(0)),
						hasProperty("infoMessages", hasSize(0)),
						hasProperty("debugMessages", hasSize(0))
				)
		))
		.andDo(print());
	}

	@Test
	public void login_WhenErrorParameter_ShouldReturnLoginPageWithErrorMessage() throws Exception {
		mockMvc.perform(get("/login")
				.param("error", "whatever")
		)
		.andExpect(status().isOk())
		.andExpect(view().name("login/login"))
		.andExpect(model().hasNoErrors())
		.andExpect(model().attribute("messages",
				allOf(
						hasProperty("errorMessages", hasSize(1)),
						hasProperty("errorMessages", hasItem("Invalid username or password, try again.")),
						hasProperty("infoMessages", hasSize(0)),
						hasProperty("debugMessages", hasSize(0))
				)
		))
		.andDo(print());
	}
}
