package org.larics.jetshop.service;

import org.larics.jetshop.model.CurrentUser;
import org.larics.jetshop.model.Role;
import org.springframework.stereotype.Service;

/*
 * Used to verify if the user has access to specific methods in controllers.
 * 
 * @author Igor Laryukhin
 */
@Service
public class CurrentUserService {

	public boolean canAccessUser(CurrentUser currentUser, Long userId) {
		return currentUser != null
				&& (currentUser.getRole() == Role.ADMIN 
						|| currentUser.getId().equals(userId)
				);
	}

}
