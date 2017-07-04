package org.larics.jetshop.controller;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.larics.jetshop.service.JetModelService;
import org.mockito.Mockito;
import org.springframework.context.MessageSource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/*
 * Tests for {@link HomeController}.
 * 
 * @author Igor Laryukhin
 */
public class HomeControllerTest {

	private MockMvc mockMvc;

	private String latestJetModelName = "787";
	private JetModelService jetModelService;
	private MessageSource messageSource;

	@Before
	public void setUp() {
		jetModelService = Mockito.mock(JetModelService.class);
		messageSource = Mockito.mock(MessageSource.class);
		HomeController homeController = new HomeController(latestJetModelName,
				jetModelService, messageSource);
		mockMvc = MockMvcBuilders.standaloneSetup(homeController).build();
	}

	@Test
	public void getHome_WhenLogout_ShouldReturnInfoMessage() throws Exception {
		when(messageSource.getMessage("user.logged.out", null, Locale.ENGLISH))
			.thenReturn("You have been logged out");

		Long id = 1L;
		when(jetModelService.getIdByName(latestJetModelName)).thenReturn(id);

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

		verify(jetModelService, times(1)).getIdByName(latestJetModelName);
		verifyNoMoreInteractions(jetModelService);
	}
}
