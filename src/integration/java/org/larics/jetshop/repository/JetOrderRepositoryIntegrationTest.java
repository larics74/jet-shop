package org.larics.jetshop.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.larics.jetshop.model.JetModel;
import org.larics.jetshop.model.JetOrder;
import org.larics.jetshop.model.Role;
import org.larics.jetshop.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.FileCopyUtils;

/*
 * Tests for {@link JetOrderRepository}.
 * 
 * @author Igor Laryukhin
 */
@RunWith(SpringRunner.class)
@DataJpaTest
public class JetOrderRepositoryIntegrationTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private JetOrderRepository jetOrderRepository;

	@Test
	public void findByLineNumber() throws IOException {
		User user1 = new User("user1", 
				new BCryptPasswordEncoder().encode("user1"),
				Role.CUSTOMER);
		JetModel jetModel1 = new JetModel("787", "787 description");
		jetModel1.setDrawing(getBytes("model787.jpg"));
		JetOrder jetOrder1 = new JetOrder(101, user1, jetModel1,
				LocalDate.of(2018, 5, 26));
		entityManager.persistAndFlush(user1);
		entityManager.persistAndFlush(jetModel1);
		entityManager.persistAndFlush(jetOrder1);

		Integer expectedLineNumber = 102;
		User user2 = new User("user2", 
				new BCryptPasswordEncoder().encode("user2"),
				Role.CUSTOMER);
		JetModel jetModel2 = new JetModel("737", "737 description");
		jetModel2.setDrawing(getBytes("model737.jpg"));
		JetOrder jetOrder2 = new JetOrder(expectedLineNumber, user2, jetModel2,
				LocalDate.of(2018, 6, 27));
		entityManager.persistAndFlush(user2);
		entityManager.persistAndFlush(jetModel2);
		entityManager.persistAndFlush(jetOrder2);

		JetOrder jetOrderFound = jetOrderRepository
				.findByLineNumber(expectedLineNumber);

		assertThat(jetOrderFound.getLineNumber()).isEqualTo(expectedLineNumber);
	}

	@Test
	public void countByCustomerId() throws IOException {
		Long expectedCount = 1L;
		User user1 = new User("user1", 
				new BCryptPasswordEncoder().encode("user1"),
				Role.CUSTOMER);
		JetModel jetModel1 = new JetModel("787", "787 description");
		jetModel1.setDrawing(getBytes("model787.jpg"));
		JetOrder jetOrder1 = new JetOrder(101, user1, jetModel1,
				LocalDate.of(2018, 5, 26));
		Long customerId = entityManager.persistAndFlush(user1).getId();
		entityManager.persistAndFlush(jetModel1);
		entityManager.persistAndFlush(jetOrder1);

		User user2 = new User("user2", 
				new BCryptPasswordEncoder().encode("user2"),
				Role.CUSTOMER);
		JetModel jetModel2 = new JetModel("737", "737 description");
		jetModel2.setDrawing(getBytes("model737.jpg"));
		JetOrder jetOrder2 = new JetOrder(102, user2, jetModel2,
				LocalDate.of(2018, 6, 27));
		entityManager.persistAndFlush(user2);
		entityManager.persistAndFlush(jetModel2);
		entityManager.persistAndFlush(jetOrder2);

		Long actualCount = jetOrderRepository.countByCustomerId(customerId);

		assertThat(actualCount).isEqualTo(expectedCount);
	}

	@Test
	public void countByJetModelId() throws IOException {
		Long expectedCount = 1L;
		User user1 = new User("user1", 
				new BCryptPasswordEncoder().encode("user1"),
				Role.CUSTOMER);
		JetModel jetModel1 = new JetModel("787", "787 description");
		jetModel1.setDrawing(getBytes("model787.jpg"));
		JetOrder jetOrder1 = new JetOrder(101, user1, jetModel1,
				LocalDate.of(2018, 5, 26));
		entityManager.persistAndFlush(user1);
		Long jetModelId = entityManager.persistAndFlush(jetModel1).getId();
		entityManager.persistAndFlush(jetOrder1);

		User user2 = new User("user2", 
				new BCryptPasswordEncoder().encode("user2"),
				Role.CUSTOMER);
		JetModel jetModel2 = new JetModel("737", "737 description");
		jetModel2.setDrawing(getBytes("model737.jpg"));
		JetOrder jetOrder2 = new JetOrder(102, user2, jetModel2,
				LocalDate.of(2018, 6, 27));
		entityManager.persistAndFlush(user2);
		entityManager.persistAndFlush(jetModel2);
		entityManager.persistAndFlush(jetOrder2);

		Long actualCount = jetOrderRepository.countByJetModelId(jetModelId);

		assertThat(actualCount).isEqualTo(expectedCount);
	}

	@Test
	public void findByCustomerId() throws IOException {
		User user1 = new User("user1", 
				new BCryptPasswordEncoder().encode("user1"),
				Role.CUSTOMER);
		JetModel jetModel1 = new JetModel("787", "787 description");
		jetModel1.setDrawing(getBytes("model787.jpg"));
		JetOrder jetOrder1 = new JetOrder(101, user1, jetModel1,
				LocalDate.of(2018, 5, 26));
		Long customerId = entityManager.persistAndFlush(user1).getId();
		entityManager.persistAndFlush(jetModel1);
		entityManager.persistAndFlush(jetOrder1);

		User user2 = new User("user2", 
				new BCryptPasswordEncoder().encode("user2"),
				Role.CUSTOMER);
		JetModel jetModel2 = new JetModel("737", "737 description");
		jetModel2.setDrawing(getBytes("model737.jpg"));
		JetOrder jetOrder2 = new JetOrder(102, user2, jetModel2,
				LocalDate.of(2018, 6, 27));
		entityManager.persistAndFlush(user2);
		entityManager.persistAndFlush(jetModel2);
		entityManager.persistAndFlush(jetOrder2);

		JetModel jetModel3 = new JetModel("777", "777 description");
		jetModel3.setDrawing(getBytes("model777.jpg"));
		JetOrder jetOrder3 = new JetOrder(103, user1, jetModel3,
				LocalDate.of(2018, 7, 28));
		entityManager.persistAndFlush(jetModel3);
		entityManager.persistAndFlush(jetOrder3);

		List<JetOrder> jetOrders = jetOrderRepository
				.findByCustomerId(customerId);

		assertThat(jetOrders)
			.containsExactlyInAnyOrder(jetOrder1, jetOrder3);
	}

	@Test
	public void findFirstByIdAndCustomerId() throws IOException {
		User user1 = new User("user1", 
				new BCryptPasswordEncoder().encode("user1"),
				Role.CUSTOMER);
		JetModel jetModel1 = new JetModel("787", "787 description");
		jetModel1.setDrawing(getBytes("model787.jpg"));
		JetOrder jetOrder1 = new JetOrder(101, user1, jetModel1,
				LocalDate.of(2018, 5, 26));
		Long customerId = entityManager.persistAndFlush(user1).getId();
		entityManager.persistAndFlush(jetModel1);
		entityManager.persistAndFlush(jetOrder1);

		User user2 = new User("user2", 
				new BCryptPasswordEncoder().encode("user2"),
				Role.CUSTOMER);
		JetModel jetModel2 = new JetModel("737", "737 description");
		jetModel2.setDrawing(getBytes("model737.jpg"));
		JetOrder jetOrder2 = new JetOrder(102, user2, jetModel2,
				LocalDate.of(2018, 6, 27));
		entityManager.persistAndFlush(user2);
		entityManager.persistAndFlush(jetModel2);
		entityManager.persistAndFlush(jetOrder2);

		Integer lineNumber = 103;
		JetModel jetModel3 = new JetModel("777", "777 description");
		jetModel3.setDrawing(getBytes("model777.jpg"));
		JetOrder jetOrder3 = new JetOrder(lineNumber, user1, jetModel3,
				LocalDate.of(2018, 7, 28));
		entityManager.persistAndFlush(jetModel3);
		Long id = entityManager.persistAndFlush(jetOrder3).getId();

		JetOrder jetOrderFound = jetOrderRepository
				.findFirstByIdAndCustomerId(id, customerId);

		assertThat(jetOrderFound.getLineNumber()).isEqualTo(lineNumber);
	}

	@Test
	public void findIdByLineNumber() throws IOException {
		User user1 = new User("user1", 
				new BCryptPasswordEncoder().encode("user1"),
				Role.CUSTOMER);
		JetModel jetModel1 = new JetModel("787", "787 description");
		jetModel1.setDrawing(getBytes("model787.jpg"));
		JetOrder jetOrder1 = new JetOrder(101, user1, jetModel1,
				LocalDate.of(2018, 5, 26));
		entityManager.persistAndFlush(user1).getId();
		entityManager.persistAndFlush(jetModel1);
		entityManager.persistAndFlush(jetOrder1);

		User user2 = new User("user2", 
				new BCryptPasswordEncoder().encode("user2"),
				Role.CUSTOMER);
		JetModel jetModel2 = new JetModel("737", "737 description");
		jetModel2.setDrawing(getBytes("model737.jpg"));
		JetOrder jetOrder2 = new JetOrder(102, user2, jetModel2,
				LocalDate.of(2018, 6, 27));
		entityManager.persistAndFlush(user2);
		entityManager.persistAndFlush(jetModel2);
		entityManager.persistAndFlush(jetOrder2);

		Integer lineNumber = 103;
		JetModel jetModel3 = new JetModel("777", "777 description");
		jetModel3.setDrawing(getBytes("model777.jpg"));
		JetOrder jetOrder3 = new JetOrder(lineNumber, user1, jetModel3,
				LocalDate.of(2018, 7, 28));
		entityManager.persistAndFlush(jetModel3);
		Long expectedId = entityManager.persistAndFlush(jetOrder3).getId();

		Long actualId = jetOrderRepository
				.findIdByLineNumber(lineNumber);

		assertThat(actualId).isEqualTo(expectedId);
	}

	@Test
	public void countById() throws IOException {
		Long expectedCount = 1L;

		User user1 = new User("user1", 
				new BCryptPasswordEncoder().encode("user1"),
				Role.CUSTOMER);
		JetModel jetModel1 = new JetModel("787", "787 description");
		jetModel1.setDrawing(getBytes("model787.jpg"));
		JetOrder jetOrder1 = new JetOrder(101, user1, jetModel1,
				LocalDate.of(2018, 5, 26));
		entityManager.persistAndFlush(user1).getId();
		entityManager.persistAndFlush(jetModel1);
		entityManager.persistAndFlush(jetOrder1);

		User user2 = new User("user2", 
				new BCryptPasswordEncoder().encode("user2"),
				Role.CUSTOMER);
		JetModel jetModel2 = new JetModel("737", "737 description");
		jetModel2.setDrawing(getBytes("model737.jpg"));
		JetOrder jetOrder2 = new JetOrder(102, user2, jetModel2,
				LocalDate.of(2018, 6, 27));
		entityManager.persistAndFlush(user2);
		entityManager.persistAndFlush(jetModel2);
		entityManager.persistAndFlush(jetOrder2);

		Integer lineNumber = 103;
		JetModel jetModel3 = new JetModel("777", "777 description");
		jetModel3.setDrawing(getBytes("model777.jpg"));
		JetOrder jetOrder3 = new JetOrder(lineNumber, user1, jetModel3,
				LocalDate.of(2018, 7, 28));
		entityManager.persistAndFlush(jetModel3);
		Long id = entityManager.persistAndFlush(jetOrder3).getId();

		Long actualCount = jetOrderRepository
				.countById(id);

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
