package org.larics.jetshop.service;

import java.util.List;

import org.larics.jetshop.model.JetModel;

/*
 * A service to retrieve {@link JetModel} data.
 * 
 * @author Igor Laryukhin
 */
public interface JetModelService {

	List<JetModel> getAll();

	JetModel getById(Long id);

	Long getIdByName(String name);

	Long save(JetModel jetModel);

	void delete(Long id);

	Long countById(Long id);

}