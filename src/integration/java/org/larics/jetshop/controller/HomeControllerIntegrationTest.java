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
import org.larics.jetshop.repository.JetModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

/*
 * Tests for {@link HomeController}.
 * 
 * @author Igor Laryukhin
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class HomeControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private JetModelRepository jetModelRepository;

	@Test
	public void getHome_WhenUserNotAuthenticated_ShouldReturnOk() throws Exception {
		mockMvc.perform(get("/"))
			.andExpect(status().isOk())
			.andExpect(view().name("index"))
			.andDo(print());
	}

	@Test
	@WithUserDetails("user")
	public void getHome_WhenUserAuthenticated_ShouldReturnOk() throws Exception {
		mockMvc.perform(get("/"))
			.andExpect(status().isOk())
			.andExpect(view().name("index"))
			.andDo(print());
	}

	@Test
	public void getHome_WhenLogout_ShouldReturnInfoMessage() throws Exception {
		String latestJetModelName = "787";
		Long id = jetModelRepository.findIdByName(latestJetModelName);

		mockMvc.perform(get("/")
				.param("logout", "whatever")
		)
			.andExpect(status().isOk())
			.andExpect(view().name("index"))
			.andExpect(model().attribute("messages",
					allOf(
							hasProperty("errorMessages", hasSize(0)),
							hasProperty("infoMessages", hasSize(1)),
							hasProperty("infoMessages", hasItem("You have been logged out")),
							hasProperty("debugMessages", hasSize(0))
					)
			))
			.andExpect(model().attribute("latestJetModelName", latestJetModelName))
			.andExpect(model().attribute("latestJetModelId", id))
			.andDo(print());
	}

}
