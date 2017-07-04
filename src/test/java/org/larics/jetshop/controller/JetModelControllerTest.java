package org.larics.jetshop.controller;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.larics.jetshop.model.JetModel;
import org.larics.jetshop.service.JetModelService;
import org.larics.jetshop.service.JetOrderService;
import org.larics.jetshop.service.JetOrderServiceImpl;
import org.mockito.Mockito;
import org.springframework.context.MessageSource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.FileCopyUtils;

/*
 * Tests for {@link JetModelController}.
 * 
 * @author Igor Laryukhin
 */
public class JetModelControllerTest {

	private MockMvc mockMvc;

	private JetModelService jetModelService;
	private JetOrderService jetOrderService;
	private MessageSource messageSource;

	@Before
	public void setUp() {
		jetModelService = Mockito.mock(JetModelService.class);
		jetOrderService = Mockito.mock(JetOrderServiceImpl.class);
		messageSource = Mockito.mock(MessageSource.class);
		JetModelController jetModelController =
				new JetModelController(jetModelService, jetOrderService,
						messageSource);
		mockMvc = MockMvcBuilders.standaloneSetup(jetModelController).build();
	}

	@Test
	public void getAll_WhenNotExist_ShouldReturnErrorMessage() throws Exception {
		when(jetModelService.getAll()).thenReturn(new ArrayList<JetModel>());

		when(messageSource.getMessage("jetModels.not.found", null, Locale.ENGLISH))
			.thenReturn("No Jet models found");

		mockMvc.perform(get("/jetModels"))
		.andExpect(status().isOk())
		.andExpect(view().name("jetModels/list"))
		.andExpect(model().hasNoErrors())
		.andExpect(model().attributeDoesNotExist("jetModels"))
		.andExpect(model().attribute("messages",
				allOf(
						hasProperty("errorMessages", hasSize(1)),
						hasProperty("errorMessages", hasItem("No Jet models found")),
						hasProperty("infoMessages", hasSize(0)),
						hasProperty("debugMessages", hasSize(0))
				)
		))
		.andDo(print());

		verify(jetModelService, times(1)).getAll();
		verifyNoMoreInteractions(jetModelService);
}

	@Test
	public void getAll_WhenExist_ShouldReturnAll() throws Exception {
		List<JetModel> expectedJetModels = new ArrayList<>();
		JetModel jetModel1 = new JetModel("737",
				"The brief description of 737 Jet model");
		jetModel1.setDrawing(getBytes("model737.jpg"));
		expectedJetModels.add(jetModel1);

		JetModel jetModel2 = new JetModel("777",
				"The brief description of 777 Jet model");
		jetModel2.setDrawing(getBytes("model777.jpg"));
		expectedJetModels.add(jetModel2);

		when(jetModelService.getAll()).thenReturn(expectedJetModels);

		mockMvc.perform(get("/jetModels"))
		.andExpect(status().isOk())
		.andExpect(view().name("jetModels/list"))
		.andExpect(model().hasNoErrors())
		.andExpect(model().attribute("jetModels", hasSize(2)))
		.andExpect(model().attribute("jetModels", hasItems(expectedJetModels.toArray())))
		.andDo(print());

		verify(jetModelService, times(1)).getAll();
		verifyNoMoreInteractions(jetModelService);
}

	@Test
	public void getUpdatable_WhenExists_ShouldReturnJetModel() throws Exception {
		JetModel jetModel = new JetModel("737",
				"The brief description of 737 Jet model");
		Long id = 1L;
		jetModel.setId(id);
		jetModel.setDrawing(getBytes("model737.jpg"));

		when(jetModelService.getById(id)).thenReturn(jetModel);

		mockMvc.perform(get("/jetModels/edit/" + id))
		.andExpect(status().isOk())
		.andExpect(view().name("jetModels/edit"))
		.andExpect(model().hasNoErrors())
		.andExpect(model().attribute("jetModel", jetModel))
		.andExpect(model().attribute("help", true))
		.andDo(print());

		verify(jetModelService, times(1)).getById(id);
		verifyNoMoreInteractions(jetModelService);
}

	@Test
	public void save_WhenFileIsEmpty_ShouldShowErrorMessage() throws Exception {
		when(jetModelService.getIdByName("737")).thenReturn(null);

		MockMultipartFile file = new MockMultipartFile("file", getBytes("testEmpty.jpg"));

		mockMvc.perform(MockMvcRequestBuilders.fileUpload("/jetModels").file(file)
				.param("id", "")
				.param("name", "737")
				.param("description", "The brief description of 737 Jet model")
				.param("drawing", "")
				// "file" parameter already provided in MockMultipartFile constructor
				// .param("file", "src/main/resources/static/images/model737.jpg")
		)
		.andExpect(status().isOk())
		.andExpect(view().name("jetModels/edit"))
		.andExpect(model().attributeHasFieldErrorCode("jetModel", "drawing", "jetModel.no.drawing"))
		.andDo(print());

		verify(jetModelService, times(1)).getIdByName("737");
		verify(jetModelService, times(0)).save(isA(JetModel.class));
		verifyNoMoreInteractions(jetModelService);
	}

	@Test
	public void save_WhenNoErrors_ShouldSaveAndRedirectToView() throws Exception {
		Long id = 1L;

		when(jetModelService.getIdByName("737")).thenReturn(null);
		when(jetModelService.save(isA(JetModel.class))).thenReturn(id);

		MockMultipartFile file = new MockMultipartFile("file", getBytes("model737.jpg"));

		mockMvc.perform(MockMvcRequestBuilders.fileUpload("/jetModels").file(file)
				.param("id", "")
				.param("name", "737")
				.param("description", "The brief description of 737 Jet model")
				.param("drawing", "")
				// "file" parameter already provided in MockMultipartFile constructor
				// .param("file", "src/main/resources/static/images/model737.jpg")
		)
		.andExpect(status().is3xxRedirection())
		.andExpect(redirectedUrl("/jetModels/" + id))
		.andExpect(model().hasNoErrors())
		.andDo(print());

		verify(jetModelService, times(1)).getIdByName("737");
		verify(jetModelService, times(1)).save(isA(JetModel.class));
		verifyNoMoreInteractions(jetModelService);
	}

	@Test
	public void delete_WhenJetOrderContainsThisJetModel_ShouldNotDelete() throws Exception {
		Long id = 1L;

		when(jetModelService.countById(id)).thenReturn(1L);
		when(jetOrderService.countByJetModelId(id)).thenReturn(1L);

		mockMvc.perform(get("/jetModels/delete/" + id))
		.andExpect(status().is3xxRedirection())
		.andExpect(redirectedUrl("/jetModels"))
		.andExpect(model().hasNoErrors())
		.andDo(print());

		verify(jetModelService, times(0)).delete(id);
}

	@Test
	public void delete_WhenNoErrors_ShouldDelete() throws Exception {
		Long id = 1L;

		when(jetModelService.countById(id)).thenReturn(1L);
		when(jetOrderService.countByJetModelId(id)).thenReturn(0L);

		mockMvc.perform(get("/jetModels/delete/" + id))
		.andExpect(status().is3xxRedirection())
		.andExpect(redirectedUrl("/jetModels"))
		.andExpect(model().hasNoErrors())
		.andDo(print());

		verify(jetModelService, times(1)).delete(id);
}

//	@Test
//	public void test() throws IOException {
//		byte[] a1 = getBytes("model737.jpg");
//		byte[] a2 = getBytes("model737.jpg");
//		byte[] a3 = getBytes("model737.jpg");
//		System.out.println("a1: " + a1);
//		System.out.println("a2: " + a2);
//		System.out.println("a3: " + a3);
//		System.out.println("Arrays.equals(a1, a2)) = " + Arrays.equals(a1, a2));
//		System.out.println("Arrays.equals(a1, a3)) = " + Arrays.equals(a1, a3));
//		System.out.println("Arrays.equals(a2, a3)) = " + Arrays.equals(a2, a3));
//	}

	private byte[] getBytes(String filename) throws IOException {
		InputStream input = getClass().getClassLoader()
				.getResourceAsStream("static/images"
						+ "/" + filename);
		byte[] bytes = FileCopyUtils.copyToByteArray(input);
//		Path path = Paths.get("src/main/resources/static/images").resolve(filename);
//		byte[] bytes = Files.readAllBytes(path);

		return bytes; 
	}

}
