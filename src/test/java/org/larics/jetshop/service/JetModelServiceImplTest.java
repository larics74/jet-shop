package org.larics.jetshop.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.larics.jetshop.model.JetModel;
import org.larics.jetshop.repository.JetModelRepository;

/*
 * Tests for {@link JetModelServiceImpl}.
 * 
 * @author Igor Laryukhin
 */
public class JetModelServiceImplTest {

	private JetModelRepository jetModelRepository;
	private JetModelService jetModelSerice;

	@Before
	public void setUp() {
		jetModelRepository = mock(JetModelRepository.class);
		jetModelSerice = new JetModelServiceImpl(jetModelRepository);
	}

	@Test
	public void getAll() {
		List<JetModel> expectedJetModels = new ArrayList<>();
		JetModel jetModel1 = new JetModel("737",
				"The brief description of 737 Jet model");
		JetModel jetModel2 = new JetModel("777",
				"The brief description of 777 Jet model");
		JetModel jetModel3 = new JetModel("787",
				"The brief description of 787 Jet model");
		expectedJetModels.add(jetModel1);
		expectedJetModels.add(jetModel2);
		expectedJetModels.add(jetModel3);

		when(jetModelRepository.findAll()).thenReturn(expectedJetModels);

		assertEquals(expectedJetModels, jetModelSerice.getAll());
	}

	@Test
	public void getById() {
		Long id = 1L;
		JetModel expectedJetModel = new JetModel("737",
				"The brief description of 737 Jet model");

		when(jetModelRepository.findOne(id)).thenReturn(expectedJetModel);

		assertEquals(expectedJetModel, jetModelSerice.getById(id));
	}

	@Test
	public void getIdByName() {
		Long expectedId = 1L;
		String name = "737";

		when(jetModelRepository.findIdByName(name)).thenReturn(expectedId);

		assertEquals(expectedId, jetModelSerice.getIdByName(name));
	}

	@Test
	public void save() {
		JetModel unsaved = new JetModel("737",
				"The brief description of 737 Jet model");
		Long expectedId = 1L;
		JetModel saved = new JetModel("737",
				"The brief description of 737 Jet model");
		saved.setId(expectedId);

		when(jetModelRepository.save(unsaved)).thenReturn(saved);

		assertEquals(expectedId, jetModelSerice.save(unsaved));
	}

	@Test
	public void delete() {
		Long id = 1L;

		jetModelSerice.delete(id);

		verify(jetModelRepository, times(1)).delete(id);
	}

	@Test
	public void countById() {
		Long id = 1L;
		Long expectedCount = 2L;

		when(jetModelRepository.countById(id)).thenReturn(expectedCount);

		assertEquals(expectedCount, jetModelSerice.countById(id));
	}
}
