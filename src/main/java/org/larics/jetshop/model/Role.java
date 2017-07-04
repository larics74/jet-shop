package org.larics.jetshop.model;

/*
 * Role of {@link User}.
 * 
 * @author Igor Laryukhin
 */
public enum Role {
	ADMIN("Admin"), CUSTOMER("Customer"), SCHEDULER("Scheduler");

	private final String name;

	private Role(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
