package org.larics.jetshop.controller;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
//Eclipse mistakenly shows this method as deprecated, though it's not:
//public static <T> org.hamcrest.Matcher<T> is(T value)
//Eclipse thinks, it's the following method:
//public static <T> org.hamcrest.Matcher<T> is(java.lang.Class<T> type)
import static org.hamcrest.CoreMatchers.is;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.larics.jetshop.model.JetModel;
import org.larics.jetshop.model.JetOrder;
import org.larics.jetshop.model.Requirement;
import org.larics.jetshop.model.User;
import org.larics.jetshop.service.JetModelService;
import org.larics.jetshop.service.JetOrderServiceImpl;
import org.larics.jetshop.service.UserService;
import org.springframework.context.MessageSource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/*
 * Tests for {@link JetOrderController}.
 * 
 * @author Igor Laryukhin
 */
public class JetOrderControllerTest {

	private MockMvc mockMvc;

	private JetOrderServiceImpl jetOrderService;
	private UserService userService;
	private JetModelService jetModelService;
	private MessageSource messageSource;

	@Before
	public void setUp() {
		jetOrderService = mock(JetOrderServiceImpl.class);
		userService = mock(UserService.class);
		jetModelService = mock(JetModelService.class);
		messageSource = mock(MessageSource.class);

		JetOrderController jetOrderController =
				new JetOrderController(jetOrderService, userService,
						jetModelService, messageSource);
		mockMvc = MockMvcBuilders.standaloneSetup(jetOrderController).build();
	}

	@Test
	public void getAll_WhenEmpty_ShouldReturnErrorMessage() throws Exception {
		when(messageSource.getMessage("jetOrders.not.found", null, Locale.ENGLISH))
			.thenReturn("No Jet orders found");

		mockMvc.perform(get("/jetOrders"))
		.andExpect(status().isOk())
		.andExpect(view().name("jetOrders/list"))
		.andExpect(model().hasNoErrors())
		.andExpect(model().attribute("messages",
				allOf(
						hasProperty("errorMessages", hasSize(1)),
						hasProperty("errorMessages", hasItem("No Jet orders found")),
						hasProperty("infoMessages", hasSize(0)),
						hasProperty("debugMessages", hasSize(0))
				)
		))
		.andExpect(model().attributeDoesNotExist("jetOrders"))
		.andDo(print());
	}	

	@Test
	public void getAll_WhenExist_ShouldReturnAll() throws Exception {
		List<JetOrder> jetOrders = new ArrayList<>();

		User user1 = new User();
		JetModel jetModel1 = new JetModel();
		JetOrder jetOrder1 = new JetOrder(101, user1, jetModel1, LocalDate.of(2018, 5, 26));
		jetOrders.add(jetOrder1);

		User user2 = new User();
		JetModel jetModel2 = new JetModel();
		JetOrder jetOrder2 = new JetOrder(102, user2, jetModel2, LocalDate.of(2018, 6, 27));
		jetOrders.add(jetOrder2);

		when(jetOrderService.getAll()).thenReturn(jetOrders);

		mockMvc.perform(get("/jetOrders"))
		.andExpect(status().isOk())
		.andExpect(view().name("jetOrders/list"))
		.andExpect(model().hasNoErrors())
		.andExpect(model().attribute("jetOrders", equalTo(jetOrders)))
		.andDo(print());

		verify(jetOrderService, times(1)).getAll();
		verifyNoMoreInteractions(jetOrderService);
	}	

	@Test
	public void getOne_WhenNotExists_ShouldReturnErrorMessage() throws Exception {
		Long id = 1L;

		when(jetOrderService.getById(id)).thenReturn(null);

		when(messageSource.getMessage("jetOrder.not.found", null, Locale.ENGLISH))
			.thenReturn("Jet order not found");

		mockMvc.perform(get("/jetOrders/" + id))
		.andExpect(status().is3xxRedirection())
		.andExpect(redirectedUrl("/jetOrders"))
		.andExpect(model().hasNoErrors())
		.andExpect(flash().attribute("messages",
				allOf(
						hasProperty("errorMessages", hasSize(1)),
						hasProperty("errorMessages", hasItem("Jet order not found")),
						hasProperty("infoMessages", hasSize(0)),
						hasProperty("debugMessages", hasSize(0))
				)
		))
		.andDo(print());

		verify(jetOrderService, times(1)).getById(id);
		verifyNoMoreInteractions(jetOrderService);
}	

	@Test
	public void getOne_WhenExists_ShouldReturnJetOrder() throws Exception {
		JetOrder jetOrder = new JetOrder();
		Long id = 1L;
		jetOrder.setId(id);

		when(jetOrderService.getById(id)).thenReturn(jetOrder);

		mockMvc.perform(get("/jetOrders/" + id))
		.andExpect(status().isOk())
		.andExpect(view().name("jetOrders/view"))
		.andExpect(model().hasNoErrors())
		.andExpect(model().attribute("jetOrder", jetOrder))
		.andDo(print());

		verify(jetOrderService, times(1)).getById(id);
		verifyNoMoreInteractions(jetOrderService);
	}	

	@Test
	public void getNew_WhenJetModelIdIsNotNull_ShouldReturnNewJetOrderWithPresetJetModel() throws Exception {
		List<JetModel> jetModels = new ArrayList<>();
		JetModel jetModel1 = new JetModel("737", "The brief description of 737 Jet model");
		jetModels.add(jetModel1);
		JetModel jetModel2 = new JetModel("777", "The brief description of 777 Jet model");
		jetModels.add(jetModel2);

		when(jetModelService.getAll()).thenReturn(jetModels);

		List<User> customers = new ArrayList<>();
		User user1 = new User();
		user1.setName("user");
		customers.add(user1);
		User user2 = new User();
		user2.setName("admin");
		customers.add(user2);

		when(userService.getCustomers()).thenReturn(customers);

		Long id = 1L;
		when(jetModelService.getById(id)).thenReturn(jetModel2);


		mockMvc.perform(get("/jetOrders/create")
				.param("jetModelId", id.toString())
				)
		.andExpect(status().isOk())
		.andExpect(view().name("jetOrders/edit"))
		.andExpect(model().hasNoErrors())
		.andExpect(model().attribute("jetModels", equalTo(jetModels)))
		.andExpect(model().attribute("customers", equalTo(customers)))
		.andExpect(model().attribute("jetOrder", hasProperty("jetModel", is(equalTo(jetModel2)))))
		.andDo(print());

		verify(jetModelService, times(1)).getAll();
		verify(userService, times(1)).getCustomers();
		verify(jetModelService, times(1)).getById(id);
		verifyNoMoreInteractions(jetModelService);
		verifyNoMoreInteractions(userService);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void save_WhenNewJetOrderAndLineNumberExists_ShouldReturnErrorMessage() throws Exception {
		Integer lineNumber = 101;

		when(jetOrderService.getIdByLineNumber(lineNumber)).thenReturn(1L);

		List<JetModel> jetModels = new ArrayList<>();
		JetModel m1 = new JetModel("737", "The brief description of 737 Jet model");
		jetModels.add(m1);
		JetModel m2 = new JetModel("777", "The brief description of 777 Jet model");
		jetModels.add(m2);

		when(jetModelService.getAll()).thenReturn(jetModels);

		List<User> customers = new ArrayList<>();
		User user1 = new User();
		user1.setName("user");
		customers.add(user1);
		User user2 = new User();
		user2.setName("admin");
		customers.add(user2);

		when(userService.getCustomers()).thenReturn(customers);

		mockMvc.perform(post("/jetOrders")
				.param("save", "")
				.param("lineNumber", lineNumber.toString())
				.param("customer.id", "4")
				.param("customer.name", "Delta")
				.param("jetModel.id", "3")
				.param("jetModel.name", "787")
				.param("deliveryDate", "2018-05-26")
				.param("requirements[0].description", "Seahawks exterior painting for dear team and fans")
				.param("requirements[1].description", "Red carpet floor")
				)
		.andExpect(status().isOk())
		.andExpect(view().name("jetOrders/edit"))
		.andExpect(model().attributeHasFieldErrorCode("jetOrder", "lineNumber", "duplicate.jetOrder.lineNumber"))
		.andExpect(model().attribute("jetOrder", 
				allOf(
						hasProperty("id", is(nullValue())),
						hasProperty("lineNumber", is(lineNumber)),
						hasProperty("deliveryDate", is(LocalDate.of(2018, 5, 26))),
						hasProperty("customer", hasProperty("id", is(4L))),
						hasProperty("customer", hasProperty("name", is("Delta"))),
						hasProperty("jetModel", hasProperty("id", is(3L))),
						hasProperty("jetModel", hasProperty("name", is("787"))),
						hasProperty("requirements", hasSize(2)),
						hasProperty("requirements", hasItem(
								hasProperty("description", is("Seahawks exterior painting for dear team and fans"))
						)),
						hasProperty("requirements", hasItem(
								hasProperty("description", is("Red carpet floor"))
						))
		)))
		.andExpect(model().attribute("jetModels", equalTo(jetModels)))
		.andExpect(model().attribute("customers", equalTo(customers)))
		.andDo(print());

		verify(jetOrderService, times(1)).getIdByLineNumber(lineNumber);
		verifyNoMoreInteractions(jetOrderService);
		verify(jetModelService, times(1)).getAll();
		verifyNoMoreInteractions(jetModelService);
		verify(userService, times(1)).getCustomers();
		verifyNoMoreInteractions(userService);
	}	

	@Test
	public void saveNewJetOrder_WhenNoErrors_ShouldSaveAndRedirectToView() throws Exception {
		Integer lineNumber = 101;

		when(jetOrderService.getIdByLineNumber(lineNumber)).thenReturn(null);

		User user = new User();
		user.setId(4L);
		user.setName("Delta");
		JetModel jetModel = new JetModel();
		jetModel.setId(3L);
		jetModel.setName("787");
		JetOrder jetOrder = new JetOrder(lineNumber, user, jetModel, LocalDate.of(2018, 5, 26));
		Requirement r11 = new Requirement(jetOrder,
				"Seahawks exterior painting for dear team and fans");
		jetOrder.addRequirement(r11);
		Requirement r12 = new Requirement(jetOrder, "Red carpet floor");
		jetOrder.addRequirement(r12);
		Long id = 1L;

		when(jetOrderService.save(isA(JetOrder.class))).thenReturn(id);

		mockMvc.perform(post("/jetOrders")
				.param("save", "")
				.param("lineNumber", lineNumber.toString())
				.param("customer.id", "4")
				.param("customer.name", "Delta")
				.param("jetModel.id", "3")
				.param("jetModel.name", "787")
				.param("deliveryDate", "2018-05-26")
				.param("requirements[0].description", "Seahawks exterior painting for dear team and fans")
				.param("requirements[1].description", "Red carpet floor")
				)
		.andExpect(status().is3xxRedirection())
		.andExpect(redirectedUrl("/jetOrders/" + id))
		.andDo(print());

		verify(jetOrderService, times(1)).getIdByLineNumber(lineNumber);
		verify(jetOrderService, times(1)).save(isA(JetOrder.class));
		verifyNoMoreInteractions(jetOrderService);
	}	

	@Test
	public void delete_WhenNotExists_ShouldReturnErrorMessage() throws Exception {
		Long id = 1L;

		when(jetOrderService.countById(id)).thenReturn(0L);

		when(messageSource.getMessage("jetOrder.not.found.cannnot.delete", null, Locale.ENGLISH))
			.thenReturn("Jet order not found, cannot be deleted");

		mockMvc.perform(get("/jetOrders/delete/" + id))
		.andExpect(status().is3xxRedirection())
		.andExpect(redirectedUrl("/jetOrders"))
		.andExpect(flash().attribute("messages",
				allOf(
						hasProperty("errorMessages", hasSize(1)),
						hasProperty("errorMessages", hasItem("Jet order not found, cannot be deleted")),
						hasProperty("infoMessages", hasSize(0)),
						hasProperty("debugMessages", hasSize(0))
				)
		))
		.andDo(print());

		verify(jetOrderService, times(1)).countById(id);
		verifyNoMoreInteractions(jetOrderService);
	}

	@Test
	public void delete_WhenExists_ShouldDeleteAndReturnInfoMessage() throws Exception {
		Long id = 1L;

		when(jetOrderService.countById(id)).thenReturn(1L);

		when(messageSource.getMessage("jetOrder.deleted", null, Locale.ENGLISH))
			.thenReturn("Jet order deleted");

		mockMvc.perform(get("/jetOrders/delete/" + id))
		.andExpect(status().is3xxRedirection())
		.andExpect(redirectedUrl("/jetOrders"))
		.andExpect(flash().attribute("messages",
				allOf(
						hasProperty("errorMessages", hasSize(0)),
						hasProperty("infoMessages", hasSize(1)),
						hasProperty("infoMessages", hasItem("Jet order deleted")),
						hasProperty("debugMessages", hasSize(0))
				)
		))
		.andDo(print());

		verify(jetOrderService, times(1)).countById(id);
		verify(jetOrderService, times(1)).delete(id);
		verifyNoMoreInteractions(jetOrderService);
	}
}
