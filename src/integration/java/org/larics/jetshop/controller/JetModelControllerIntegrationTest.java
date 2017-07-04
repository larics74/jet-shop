package org.larics.jetshop.controller;

import static org.hamcrest.CoreMatchers.allOf;
// Eclipse mistakenly shows this method as deprecated, though it's not:
// public static <T> org.hamcrest.Matcher<T> is(T value)
// Eclipse thinks, it's the following method:
// public static <T> org.hamcrest.Matcher<T> is(java.lang.Class<T> type)
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.larics.jetshop.model.JetModel;
import org.larics.jetshop.repository.JetModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.FileCopyUtils;

/*
 * Tests for {@link JetModelController}.
 * 
 * @author Igor Laryukhin
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class JetModelControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private JetModelRepository jetModelRepository;

	@Test
	public void getAll_WhenUserNotAuthenticated_ShouldReturnAll() throws Exception {
		int expectedCount = (int) jetModelRepository.count(); 

		mockMvc.perform(get("/jetModels"))
		.andExpect(status().isOk())
		.andExpect(view().name("jetModels/list"))
		.andExpect(model().hasNoErrors())
		.andExpect(model().attribute("jetModels", hasSize(expectedCount)))
		.andDo(print());
	}

	@Test
	public void getNew_WhenUserNotAuthenticated_ShouldRedirectToLoginPage() throws Exception {
		mockMvc.perform(get("/jetModels/create"))
		.andExpect(status().is3xxRedirection())
		.andExpect(redirectedUrlPattern("**/login"))
		.andDo(print());
	}

	@Test
	@WithUserDetails("user")
	public void getNew_WhenUserIsNotAdmin_ShouldReturnForbidden() throws Exception {
		mockMvc.perform(get("/jetModels/create"))
		.andExpect(status().isForbidden())
		.andDo(print());
	}

	@Test
	@WithUserDetails("admin")
	public void getNew_WhenUserIsAdmin_ShouldReturnNewJetModel() throws Exception {
		mockMvc.perform(get("/jetModels/create"))
		.andExpect(status().isOk())
		.andExpect(view().name("jetModels/edit"))
		.andExpect(model().hasNoErrors())
		.andExpect(model().attribute("jetModel", 
				allOf(
						hasProperty("id", nullValue()),
						hasProperty("name", nullValue())
				)
		))
		.andDo(print());
	}

	@Test
	public void getUpdatable_WhenUserNotAuthenticated_ShouldRedirectToLoginPage() throws Exception {
		mockMvc.perform(get("/jetModels/edit/1"))
		.andExpect(status().is3xxRedirection())
		.andExpect(redirectedUrlPattern("**/login"))
		.andDo(print());
	}

	@Test
	@WithUserDetails("user")
	public void getUpdatable_WhenUserIsNotAdmin_ShouldReturnForbidden() throws Exception {
		mockMvc.perform(get("/jetModels/edit/1"))
		.andExpect(status().isForbidden())
		.andDo(print());
	}

	@Test
	@WithUserDetails("admin")
	public void getUpdatable_WhenUserIsAdmin_ShouldReturnJetModel() throws Exception {
		Long id = 1L;
		JetModel jetModel = jetModelRepository.findOne(id);
		
		mockMvc.perform(get("/jetModels/edit/" + id))
		.andExpect(status().isOk())
		.andExpect(view().name("jetModels/edit"))
		.andExpect(model().hasNoErrors())
		.andExpect(model().attribute("jetModel", 
				allOf(
						hasProperty("id", is(id)),
						hasProperty("name", is(jetModel.getName())),
						hasProperty("description", is(jetModel.getDescription())),
						// TODO
						// Drawing has type byte[]
						// and must be compared with Arrays.equals.
						// It can be implemented via overriding equals() for JetModel
						// or creating custom matcher.
						hasProperty("drawing", notNullValue())
				)
		))
		.andDo(print());
	}

	@Test
	public void save_WhenUserNotAuthenticated_ShouldRedirectToLoginPage() throws Exception {
		MockMultipartFile file = new MockMultipartFile("file", getBytes("model737.jpg"));

		mockMvc.perform(MockMvcRequestBuilders.fileUpload("/jetModels").file(file)
				.param("id", "")
				.param("name", "737save")
				.param("description", "The brief description of 737 Jet model")
				.param("drawing", "")
				// "file" parameter already provided in MockMultipartFile constructor
				// .param("file", "src/main/resources/static/images/model737.jpg")
		)
		.andExpect(status().is3xxRedirection())
		.andExpect(redirectedUrlPattern("**/login"))
		.andDo(print());
	}

	@Test
	@WithUserDetails("user")
	public void save_WhenUserIsNotAdmin_ShouldReturnForbidden() throws Exception {
		MockMultipartFile file = new MockMultipartFile("file", getBytes("model737.jpg"));

		mockMvc.perform(MockMvcRequestBuilders.fileUpload("/jetModels").file(file)
				.param("id", "")
				.param("name", "737save")
				.param("description", "The brief description of 737 Jet model")
				.param("drawing", "")
				// "file" parameter already provided in MockMultipartFile constructor
				// .param("file", "src/main/resources/static/images/model737.jpg")
		)
		.andExpect(status().isForbidden())
		.andDo(print());
	}

	@Test
	@WithUserDetails("admin")
	public void save_WhenUserIsAdmin_ShouldSaveAndRedirectToView() throws Exception {
		MockMultipartFile file = new MockMultipartFile("file", getBytes("model737.jpg"));

		mockMvc.perform(MockMvcRequestBuilders.fileUpload("/jetModels").file(file)
				.param("id", "")
				.param("name", "737save")
				.param("description", "The brief description of 737 Jet model")
				.param("drawing", "")
				// "file" parameter already provided in MockMultipartFile constructor
				// .param("file", "src/main/resources/static/images/model737.jpg")
		)
		.andExpect(status().is3xxRedirection())
		// TODO
//		.andExpect(redirectedUrlPattern("/jetModels/{\\d+}"))
//		.andExpect(redirectedUrl("/jetModels/7"))
		.andDo(print());

		jetModelRepository.delete(jetModelRepository.findIdByName("737save"));
	}

	@Test
	public void delete_WhenUserNotAuthenticated_ShouldRedirectToLoginPage() throws Exception {
		mockMvc.perform(get("/jetModels/delete/1"))
		.andExpect(status().is3xxRedirection())
		.andExpect(redirectedUrlPattern("**/login"))
		.andDo(print());
	}

	@Test
	@WithUserDetails("user")
	public void delete_WhenUserIsNotAdmin_ShouldReturnForbidden() throws Exception {
		mockMvc.perform(get("/jetModels/delete/1"))
		.andExpect(status().isForbidden())
		.andDo(print());
	}

	@Test
	@WithUserDetails("admin")
	public void delete_WhenUserIsAdminAndNoErrors_ShouldDelete() throws Exception {
		JetModel jetModel = new JetModel("737-max",
				"The brief description of 737-max Jet model");
		jetModel.setDrawing(getBytes("model737.jpg"));
		Long id =jetModelRepository.save(jetModel).getId();

		mockMvc.perform(get("/jetModels/delete/" + id))
		.andExpect(status().is3xxRedirection())
		.andExpect(redirectedUrl("/jetModels"))
		.andExpect(model().hasNoErrors())
		.andDo(print());

		assertNull(jetModelRepository.findOne(id));
	}

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
