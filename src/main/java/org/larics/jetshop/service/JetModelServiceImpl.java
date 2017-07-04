package org.larics.jetshop.service;

import java.util.List;

import org.larics.jetshop.model.JetModel;
import org.larics.jetshop.repository.JetModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/*
 * Implementation of {@link JetModelService}
 * backed by {@link JetModelRepository}.
 * 
 * @author Igor Laryukhin
 */
@Service
public class JetModelServiceImpl implements JetModelService {

	private final JetModelRepository jetModelRepository;

	@Autowired
	public JetModelServiceImpl(JetModelRepository jetModelRepository) {
		this.jetModelRepository = jetModelRepository;
	}

	@Override
	public List<JetModel> getAll() {
		return jetModelRepository.findAll();
	}

	@Override
	public JetModel getById(Long id) {
		return jetModelRepository.findOne(id);
	}

	@Override
	public Long getIdByName(String name) {
		return jetModelRepository.findIdByName(name);
	}

	@Override
	public Long save(JetModel jetModel) {
		return jetModelRepository.save(jetModel).getId();
	}

	@Override
	public void delete(Long id) {
		jetModelRepository.delete(id);
	}

	@Override
	public Long countById(Long id) {
		return jetModelRepository.countById(id);
	}

}
