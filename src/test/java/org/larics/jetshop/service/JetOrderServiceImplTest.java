package org.larics.jetshop.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.larics.jetshop.model.JetOrder;
import org.larics.jetshop.repository.JetOrderRepository;

/*
 * Tests for {@link JetOrderServiceImpl}.
 * 
 * @author Igor Laryukhin
 */
public class JetOrderServiceImplTest {

	private JetOrderRepository jetOrderRepository;
	private JetOrderService jetOrderService;

	@Before
	public void setUp() {
		jetOrderRepository = mock(JetOrderRepository.class);
		jetOrderService = new JetOrderServiceImpl(jetOrderRepository);
	}

	@Test
	public void countByCustomerId() {
		Long id = 1L;
		Long expectedCount = 2L;

		when(jetOrderRepository.countByCustomerId(id)).thenReturn(expectedCount);

		assertEquals(expectedCount, jetOrderService.countByCustomerId(id));
	}

	@Test
	public void countByJetModelId() {
		Long id = 1L;
		Long expectedCount = 3L;

		when(jetOrderRepository.countByJetModelId(id)).thenReturn(expectedCount);

		assertEquals(expectedCount, jetOrderService.countByJetModelId(id));
	}

//	@Test
	public void getAll() {
		// security involved, see integration tests
	}

//	@Test
	public void getById() {
		// security involved, see integration tests
	}

	@Test
	public void getIdByLineNumber() {
		Integer lineNumber = 101;
		Long expectedId = 1L;

		when(jetOrderRepository.findIdByLineNumber(lineNumber)).thenReturn(expectedId);

		assertEquals(expectedId, jetOrderService.getIdByLineNumber(lineNumber));
	}

	@Test
	public void save() {
		JetOrder unsaved = new JetOrder();
		unsaved.setLineNumber(101);
		JetOrder saved = unsaved;
		Long expectedId = 1L;
		saved.setId(expectedId);

		when(jetOrderRepository.save(unsaved)).thenReturn(saved);

		assertEquals(expectedId, jetOrderService.save(unsaved));
	}

	@Test
	public void countById() {
		Long id = 1L;
		Long expectedCount = 2L;

		when(jetOrderRepository.countById(id)).thenReturn(expectedCount);

		assertEquals(expectedCount, jetOrderService.countById(id));
	}

	@Test
	public void delete() {
		Long id = 1L;

		jetOrderService.delete(id);

		verify(jetOrderRepository, times(1)).delete(id);
	}

}
