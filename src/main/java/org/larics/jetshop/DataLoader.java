package org.larics.jetshop;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;

import javax.annotation.PostConstruct;

import org.larics.jetshop.model.JetModel;
import org.larics.jetshop.model.JetOrder;
import org.larics.jetshop.model.Requirement;
import org.larics.jetshop.model.Role;
import org.larics.jetshop.model.User;
import org.larics.jetshop.repository.JetModelRepository;
import org.larics.jetshop.repository.JetOrderRepository;
import org.larics.jetshop.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

/*
 * Initial data for DB.
 * 
 * @author Igor Laryukhin
 */
@Component
public class DataLoader {

	private static final Logger logger = LoggerFactory.
			getLogger(DataLoader.class);

	private final UserRepository userRepository;
	private final JetModelRepository jetModelRepository;
	private final JetOrderRepository jetOrderRepository;
	private final String drawingLocation;

	@Autowired
	public DataLoader(UserRepository userRepository,
			JetModelRepository jetModelRepository,
			JetOrderRepository jetOrderRepository,
			@Value("${drawing.location}") String drawingLocation) {

		this.userRepository = userRepository;
		this.jetModelRepository = jetModelRepository;
		this.jetOrderRepository = jetOrderRepository;
		this.drawingLocation = drawingLocation;
	}

	@PostConstruct
	private void loadData() throws IOException {
		createUsers();
		createJetModels();
		createJetOrders();

		logger.debug("Data loaded:");
		List<User> users = userRepository.findAll();
		for (User user : users) {
			logger.debug(user.toString());
		}
		List<JetModel> jetModels = jetModelRepository.findAll();
		for (JetModel jetModel: jetModels) {
			logger.debug(jetModel.toString());
		}
		List<JetOrder> jetOrders = jetOrderRepository.findAll();
		for (JetOrder jetOrder: jetOrders) {
			logger.debug(jetOrder.toString());
		}
		for (User user : users) {
			logger.debug("user.id={}, orders={}", user.getId(),
					jetOrderRepository.countByCustomerId(user.getId()));
		}
	}

	private void createUsers() {
		User u1 = new User("admin", 
				new BCryptPasswordEncoder().encode("admin"),
				Role.ADMIN);
		userRepository.save(u1);
		User u2 = new User("user", 
				new BCryptPasswordEncoder().encode("user"),
				Role.CUSTOMER);
		userRepository.save(u2);
		User u3 = new User("sched", 
				new BCryptPasswordEncoder().encode("sched"),
				Role.SCHEDULER);
		userRepository.save(u3);

		User u4 = new User("Delta", 
				new BCryptPasswordEncoder().encode("user"),
				Role.CUSTOMER);
		userRepository.save(u4);
		User u5 = new User("Alaska Airlines", 
				new BCryptPasswordEncoder().encode("user"),
				Role.CUSTOMER);
		userRepository.save(u5);
		User u6 = new User("Lufthansa", 
				new BCryptPasswordEncoder().encode("user"),
				Role.CUSTOMER);
		userRepository.save(u6);
	}

	private void createJetModels() throws IOException {
		JetModel m1 = new JetModel("737",
				"The brief description of 737 Jet model");
		m1.setDrawing(getBytes("model737.jpg"));
		jetModelRepository.save(m1);

		JetModel m2 = new JetModel("777",
				"The brief description of 777 Jet model");
		m2.setDrawing(getBytes("model777.jpg"));
		jetModelRepository.save(m2);

		JetModel m3 = new JetModel("787",
				"The brief description of 787 Jet model");
		m3.setDrawing(getBytes("model787.jpg"));
		jetModelRepository.save(m3);
	}

	private void createJetOrders() {
		User c1 = userRepository.findByName("Delta");
		JetModel jm1 = jetModelRepository.findByName("787");
		JetOrder jo1 = new JetOrder(101, c1, jm1, LocalDate.of(2018, 5, 26));

		Requirement r11 = new Requirement(jo1,
				"Seahawks exterior painting for dear team and fans");
		jo1.addRequirement(r11);
		Requirement r12 = new Requirement(jo1, "Red carpet floor");
		jo1.addRequirement(r12);
		Requirement r13 = new Requirement(jo1, "Requirement 3");
		jo1.addRequirement(r13);
		Requirement r14 = new Requirement(jo1, "Requirement 4");
		jo1.addRequirement(r14);
		Requirement r15 = new Requirement(jo1, "Requirement 5");
		jo1.addRequirement(r15);

		jetOrderRepository.save(jo1);

		User c2 = userRepository.findByName("Lufthansa");
		JetModel jm2 = jetModelRepository.findByName("737");
		JetOrder jo2 = new JetOrder(102, c2, jm2, LocalDate.of(2018, 6, 26));
		jetOrderRepository.save(jo2);

		User c3 = userRepository.findByName("Delta");
		JetModel jm3 = jetModelRepository.findByName("777");
		JetOrder jo3 = new JetOrder(103, c3, jm3, LocalDate.of(2018, 7, 26));

		Requirement r31 = new Requirement(jo3,
				"Omit row 13 for seats numbering");
		jo3.addRequirement(r31);

		jetOrderRepository.save(jo3);

		JetOrder jo4 = new JetOrder(104, c3, jm2, LocalDate.of(2018, 8, 26));

		Requirement r41 = new Requirement(jo4, "No business class");
		jo4.addRequirement(r41);

		jetOrderRepository.save(jo4);

		JetOrder jo5 = new JetOrder(105, c3, jm1, LocalDate.of(2018, 9, 26));
		jetOrderRepository.save(jo5);
	}

	private byte[] getBytes(String filename) throws IOException {

		InputStream input = getClass().getClassLoader()
				.getResourceAsStream(drawingLocation
						// File.separator didn't work here
						// (at least, on Windows)
						+ "/" + filename);
		byte[] bytes = FileCopyUtils.copyToByteArray(input);

		return bytes; 
	}

}
