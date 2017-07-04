package org.larics.jetshop.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.larics.jetshop.model.JetModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.FileCopyUtils;

/*
 * Tests for {@link JetModelRepository}.
 * 
 * @author Igor Laryukhin
 */
@RunWith(SpringRunner.class)
@DataJpaTest
public class JetModelRepositoryIntegrationTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private JetModelRepository jetModelRepository;

	@Test
	public void findByName() throws IOException {
		String expectedName = "787";
		JetModel jetModel1 = new JetModel("787", "787 description");
		jetModel1.setDrawing(getBytes("model787.jpg"));
		entityManager.persistAndFlush(jetModel1);
		JetModel jetModel2 = new JetModel("737", "737 description");
		jetModel2.setDrawing(getBytes("model737.jpg"));
		entityManager.persistAndFlush(jetModel2);

		JetModel jetModelFound = jetModelRepository.findByName(expectedName);

		assertThat(jetModelFound.getName()).isEqualTo(expectedName);
	}

	@Test
	public void findIdByName() throws IOException {
		JetModel jetModel1 = new JetModel("787", "787 description");
		jetModel1.setDrawing(getBytes("model787.jpg"));
		entityManager.persistAndFlush(jetModel1);
		JetModel jetModel2 = new JetModel("737", "737 description");
		jetModel2.setDrawing(getBytes("model737.jpg"));
		Long expectedId = entityManager.persistAndFlush(jetModel2).getId();

		Long actualId = jetModelRepository.findIdByName("737");

		assertThat(actualId).isEqualTo(expectedId);
	}

	@Test
	public void countById() throws IOException {
		Long expectedCount = 1L;
		JetModel jetModel1 = new JetModel("787", "787 description");
		jetModel1.setDrawing(getBytes("model787.jpg"));
		entityManager.persistAndFlush(jetModel1);
		JetModel jetModel2 = new JetModel("737", "737 description");
		jetModel2.setDrawing(getBytes("model737.jpg"));
		Long id = entityManager.persistAndFlush(jetModel2).getId();

		Long actualCount = jetModelRepository.countById(id);

		assertThat(actualCount).isEqualTo(expectedCount);
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
