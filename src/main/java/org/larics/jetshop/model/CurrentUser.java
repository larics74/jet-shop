package org.larics.jetshop.model;

import org.springframework.security.core.authority.AuthorityUtils;

/*
 * Used by {@link CurrentUserDetailsService}.
 * 
 * @author Igor Laryukhin
 */
@SuppressWarnings("serial")
public class CurrentUser
	extends org.springframework.security.core.userdetails.User {

	private User user;

	public CurrentUser(User user) {
		super(user.getName(), user.getPassword(),
				AuthorityUtils.createAuthorityList(user.getRole().toString()));
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	public Long getId() {
		return user.getId();
	}

	public Role getRole() {
		return user.getRole();
	}

	@Override
	public String toString() {
		return "CurrentUser [user=" + user + super.toString() + "]";
	}

}
