package org.larics.jetshop.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/*
 * A user of the system.
 * 
 * @author Igor Laryukhin
 */
@Entity
@Table(name = "user")
public class User {

	public static final Integer PASSWORD_MIN_SIZE = 4;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", nullable = false, updatable = false)
	private Long id;

	@NotNull
	@Size(min = 4, max = 20)
	@Column(name = "name", nullable = false, unique = true)
	private String name;

	@Column(name = "password", nullable = false)
	private String password;

	@Transient
	private String newPassword;

	@Transient
	private String passwordRepeated;

	@Enumerated(EnumType.STRING)
	private Role role;

	public User() {
	}

	public User(String name, String password, Role role) {
		this.name = name;
		this.password = password;
		this.role = role;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getPassword() {
		return password;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public String getPasswordRepeated() {
		return passwordRepeated;
	}

	public Role getRole() {
		return role;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public void setPasswordRepeated(String passwordRepeated) {
		this.passwordRepeated = passwordRepeated;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", password=" + password +
				", newPassword=" + newPassword +
				", passwordRepeated=" + passwordRepeated +
				", role=" + role + "]";
	}

}
