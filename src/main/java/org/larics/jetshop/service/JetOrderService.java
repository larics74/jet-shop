package org.larics.jetshop.service;

import java.util.List;

import org.larics.jetshop.model.JetOrder;

/*
 * A service to retrieve {@link JetOrder} data.
 * 
 * @author Igor Laryukhin
 */
public interface JetOrderService {

	Long countByCustomerId(Long id);

	Long countByJetModelId(Long id);

	List<JetOrder> getAll();

	JetOrder getById(Long id);

	Long getIdByLineNumber(Integer lineNumber);

	Long save(JetOrder jetOrder);

	Long countById(Long id);

	void delete(Long id);

}