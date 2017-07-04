package org.larics.jetshop.service;

import java.util.List;

import org.larics.jetshop.model.CurrentUser;
import org.larics.jetshop.model.JetOrder;
import org.larics.jetshop.model.Role;
import org.larics.jetshop.repository.JetOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/*
 * Implementation of {@link JetOrderService}
 * backed by {@link JetOrderRepository}.
 * 
 * @author Igor Laryukhin
 */
@Service
public class JetOrderServiceImpl implements JetOrderService {

	private final JetOrderRepository jetOrderRepository;
	
	@Autowired
	public JetOrderServiceImpl(JetOrderRepository jetOrderRepository) {
		this.jetOrderRepository = jetOrderRepository;
	}

	@Override
	public Long countByCustomerId(Long id) {
		return jetOrderRepository.countByCustomerId(id);
	}

	@Override
	public Long countByJetModelId(Long id) {
		return jetOrderRepository.countByJetModelId(id);
	}

	@Override
	public List<JetOrder> getAll() {
		CurrentUser currentUser = getCurrentUser();
		if (currentUser.getRole() == Role.ADMIN) {
			return jetOrderRepository.findAll();
		} else {
			return jetOrderRepository.findByCustomerId(currentUser.getId());
		}
	}

	// TODO: access denied vs not found?
	@Override
	public JetOrder getById(Long id) {
		CurrentUser currentUser = getCurrentUser();
		if (currentUser.getRole() == Role.ADMIN) {
			return jetOrderRepository.findOne(id);
		} else {
			return jetOrderRepository.findFirstByIdAndCustomerId(id, currentUser.getId());
		}
	}

	@Override
	public Long getIdByLineNumber(Integer lineNumber) {
		return jetOrderRepository.findIdByLineNumber(lineNumber);
	}

	@Override
	public Long save(JetOrder jetOrder) {
		return jetOrderRepository.save(jetOrder).getId();
	}

	@Override
	public Long countById(Long id) {
		return jetOrderRepository.countById(id);
	}

	@Override
	public void delete(Long id) {
		jetOrderRepository.delete(id);
	}

	// TODO: move to bean?
	/*
	 * Returns CurrentUser.
	 */
	private CurrentUser getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		CurrentUser currentUser = null;
		try {
			currentUser = (CurrentUser) authentication.getPrincipal();
		} catch (ClassCastException e) {
			// TODO
		}
		return currentUser;
	}

}
