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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.larics.jetshop.model.JetModel;
import org.larics.jetshop.model.JetOrder;
import org.larics.jetshop.model.User;
import org.larics.jetshop.repository.JetModelRepository;
import org.larics.jetshop.repository.JetOrderRepository;
import org.larics.jetshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/*
 * Tests for {@link JetOrderController}.
 * 
 * @author Igor Laryukhin
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class JetOrderControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private JetOrderRepository jetOrderRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private JetModelRepository jetModelRepository;

	@Test
	public void getAll_WhenNotAuthenticated_ShouldRedirectToLoginPage() throws Exception {
		mockMvc.perform(get("/jetOrders"))
		.andExpect(status().is3xxRedirection())
		.andExpect(redirectedUrlPattern("**/login"))
		.andDo(print());
	}

	@Test
	@WithUserDetails("Delta")
	public void getAll_WhenAuthenticated_ShouldReturnJetOrdersForUser() throws Exception {
		Long customerId = userRepository.findByName("Delta").getId();
		int count = jetOrderRepository.countByCustomerId(customerId).intValue();

		mockMvc.perform(get("/jetOrders"))
		.andExpect(status().isOk())
		.andExpect(view().name("jetOrders/list"))
		.andExpect(model().hasNoErrors())
		.andExpect(model().attribute("jetOrders", hasSize(count)))
		.andDo(print());
	}	

	@Test
	@WithUserDetails("admin")
	public void getAll_WhenUserIsAdmin_ShouldReturnAllJetOrders() throws Exception {
		int count = (int) jetOrderRepository.count();

		mockMvc.perform(get("/jetOrders"))
		.andExpect(status().isOk())
		.andExpect(view().name("jetOrders/list"))
		.andExpect(model().hasNoErrors())
		.andExpect(model().attribute("jetOrders", hasSize(count)))
		.andDo(print());
	}	

	@Test
	public void getOne_WhenNotAuthenticated_ShouldRedirectToLoginPage() throws Exception {
		mockMvc.perform(get("/jetOrders/1"))
		.andExpect(status().is3xxRedirection())
		.andExpect(redirectedUrlPattern("**/login"))
		.andDo(print());
	}

	@Test
	@WithUserDetails("Delta")
	public void getOne_WhenAuthenticatedAndOwnsJetOrder_ShouldReturnJetOrder() throws Exception {
		Long customerId = userRepository.findByName("Delta").getId();
		Long id = jetOrderRepository.findByCustomerId(customerId).get(0).getId();

		mockMvc.perform(get("/jetOrders/" + id))
		.andExpect(status().isOk())
		.andExpect(view().name("jetOrders/view"))
		.andExpect(model().hasNoErrors())
		.andExpect(model().attribute("jetOrder", 
				hasProperty("id", is(id))
		))
		.andDo(print());
	}	

	@Test
	@WithUserDetails("sched")
	public void getOne_WhenAuthenticatedAndNotOwnsJetOrder_ShouldReturnErrorMessage() throws Exception {
		Long customerId = userRepository.findByName("Delta").getId();
		Long id = jetOrderRepository.findByCustomerId(customerId).get(0).getId();

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
	}	

	@Test
	@WithUserDetails("admin")
	public void getOne_WhenUserIsAdminAndNotOwnsJetOrder_ShouldReturnJetOrder() throws Exception {
		Long customerId = userRepository.findByName("Delta").getId();
		Long id = jetOrderRepository.findByCustomerId(customerId).get(0).getId();

		mockMvc.perform(get("/jetOrders/" + id))
		.andExpect(status().isOk())
		.andExpect(view().name("jetOrders/view"))
		.andExpect(model().hasNoErrors())
		.andExpect(model().attribute("jetOrder", 
				hasProperty("id", is(id))
		))
		.andDo(print());
	}	

	@Test
	public void getNew_WhenNotAuthenticated_ShouldRedirectToLoginPage() throws Exception {
		mockMvc.perform(get("/jetOrders/create"))
		.andExpect(status().is3xxRedirection())
		.andExpect(redirectedUrlPattern("**/login"))
		.andDo(print());
	}

	@Test
	@WithUserDetails("Delta")
	public void getNew_WhenJetModelIdIsNotNull_ShouldReturnNewJetOrderWithPresetJetModel() throws Exception {
		Long jetModelId = jetModelRepository.findIdByName("787");
		int countJetModels = (int) jetModelRepository.count();
		// Just this customer.
		int countCustomers = 1;

		mockMvc.perform(get("/jetOrders/create")
				.param("jetModelId", jetModelId.toString())
				)
		.andExpect(status().isOk())
		.andExpect(view().name("jetOrders/edit"))
		.andExpect(model().hasNoErrors())
		.andExpect(model().attribute("jetModels", hasSize(countJetModels)))
		.andExpect(model().attribute("customers", hasSize(countCustomers)))
		.andExpect(model().attribute("jetOrder",
				allOf(
					hasProperty("id", nullValue()),
					hasProperty("jetModel", hasProperty("id", is(jetModelId)))
				)
		))
		.andDo(print());
	}

	@Test
	@WithUserDetails("admin")
	public void getNew_WhenUserIsAdmin_ShouldReturnNewJetOrderWithAllCustomers() throws Exception {
		int countJetModels = (int) jetModelRepository.count();
		// It is better to have repository method for counting
		// instead of this magic number.
		int countCustomers = 4;

		mockMvc.perform(get("/jetOrders/create"))
		.andExpect(status().isOk())
		.andExpect(view().name("jetOrders/edit"))
		.andExpect(model().hasNoErrors())
		.andExpect(model().attribute("jetModels", hasSize(countJetModels)))
		.andExpect(model().attribute("customers", hasSize(countCustomers)))
		.andExpect(model().attribute("jetOrder",
				allOf(
					hasProperty("id", nullValue()),
					hasProperty("jetModel", nullValue())
				)
		))
		.andDo(print());
	}

	@Test
	public void getUpdatable_WhenNotAuthenticated_ShouldRedirectToLoginPage() throws Exception {
		mockMvc.perform(get("/jetOrders/edit/1"))
		.andExpect(status().is3xxRedirection())
		.andExpect(redirectedUrlPattern("**/login"))
		.andDo(print());
	}

	@Test
	@WithUserDetails("Delta")
	public void getUpdatable_WhenAuthenticatedAndOwnsJetOrder_ShouldReturnJetOrder() throws Exception {
		Long customerId = userRepository.findByName("Delta").getId();
		Long id = jetOrderRepository.findByCustomerId(customerId).get(0).getId();

		mockMvc.perform(get("/jetOrders/edit/" + id))
		.andExpect(status().isOk())
		.andExpect(view().name("jetOrders/edit"))
		.andExpect(model().hasNoErrors())
		.andExpect(model().attribute("jetOrder", 
				hasProperty("id", is(id))
		))
		.andDo(print());
	}	

	@Test
	@WithUserDetails("sched")
	public void getUpdatable_WhenAuthenticatedAndNotOwnsJetOrder_ShouldReturnErrorMessage() throws Exception {
		Long customerId = userRepository.findByName("Delta").getId();
		Long id = jetOrderRepository.findByCustomerId(customerId).get(0).getId();

		mockMvc.perform(get("/jetOrders/edit/" + id))
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
	}	

	@Test
	@WithUserDetails("admin")
	public void getUpdatable_WhenUserIsAdminAndNotOwnsJetOrder_ShouldReturnJetOrder() throws Exception {
		Long customerId = userRepository.findByName("Delta").getId();
		Long id = jetOrderRepository.findByCustomerId(customerId).get(0).getId();

		mockMvc.perform(get("/jetOrders/edit/" + id))
		.andExpect(status().isOk())
		.andExpect(view().name("jetOrders/edit"))
		.andExpect(model().hasNoErrors())
		.andExpect(model().attribute("jetOrder", 
				hasProperty("id", is(id))
		))
		.andDo(print());
	}	

	@Test
	@WithUserDetails("Delta")
	@Transactional
	public void saveNewJetOrder_WhenNoErrors_ShouldSaveAndRedirectToView() throws Exception {
		String customerName = "Delta";
		Long customerId = userRepository.findIdByName(customerName);
		String jetModelName = "787";
		Long jetModelId = jetModelRepository.findIdByName(jetModelName);
		String description1 = "Seahawks exterior painting for dear team and fans";
		String description2 = "Red carpet floor";

		mockMvc.perform(post("/jetOrders")
				.param("save", "")
				.param("lineNumber", "111")
				.param("customer.id", customerId.toString())
				.param("customer.name", customerName)
				.param("jetModel.id", jetModelId.toString())
				.param("jetModel.name", jetModelName)
				.param("deliveryDate", "2018-05-26")
				.param("requirements[0].description", description1)
				.param("requirements[1].description", description2)
				)
		.andExpect(status().is3xxRedirection())
		// TODO
//		.andExpect(redirectedUrl("/jetOrders/" + id))
		.andDo(print());

		JetOrder jetOrder = jetOrderRepository.findByLineNumber(111);
		// This is why @Transactional is used for this method - 
		// to be able to get lazy initialized members.
		// The other possible approaches are:
		// - Hibernate-specific initialization;
		// - JPA projections;
		// - additional query methods.
		assertThat(jetOrder.getCustomer().getId()).isEqualTo(4L);
		assertThat(jetOrder.getJetModel().getId()).isEqualTo(3L);
		assertThat(jetOrder.getDeliveryDate()).isEqualTo(LocalDate.of(2018, 05, 26));
		assertThat(jetOrder.getRequirements().size()).isEqualTo(2);
		assertThat(jetOrder.getRequirements().get(0).getDescription()).isEqualTo(description1);
		assertThat(jetOrder.getRequirements().get(1).getDescription()).isEqualTo(description2);

		jetOrderRepository.delete(jetOrder.getId());
	}	

	@Test
	@WithUserDetails("Delta")
	public void delete_WhenUserIsNotAdmin_ShouldReturnForbidden() throws Exception {
		User customer = userRepository.findByName("Delta");
		JetModel jetModel = jetModelRepository.findByName("787");
		JetOrder jetOrder = new JetOrder(111, customer, jetModel, LocalDate.of(2018, 5, 26));
		Long id = jetOrderRepository.save(jetOrder).getId();

		mockMvc.perform(get("/jetOrders/delete/" + id))
		.andExpect(status().isForbidden())
		.andDo(print());

		jetOrderRepository.delete(id);
	}

	@Test
	@WithUserDetails("admin")
	public void delete_WhenExistsAndUserIsAdmin_ShouldDeleteAndReturnInfoMessage() throws Exception {
		User customer = userRepository.findByName("Delta");
		JetModel jetModel = jetModelRepository.findByName("787");
		JetOrder jetOrder = new JetOrder(111, customer, jetModel, LocalDate.of(2018, 5, 26));
		Long id = jetOrderRepository.save(jetOrder).getId();

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

		jetOrder = jetOrderRepository.findByLineNumber(111);
		assertThat(jetOrder).isNull();
	}

}
