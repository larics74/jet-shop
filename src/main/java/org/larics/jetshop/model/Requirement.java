package org.larics.jetshop.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/*
 * Requirement for {@link JetOrder}.
 * 
 * @author Igor Laryukhin
 */
@Entity
@Table(name = "requirement")
public class Requirement {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", nullable = false, updatable = false)
	private Long id;

	@NotNull
	@Size(min = 1, max = 100)
	@Column(name = "description", nullable = false)
	private String description;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "jet_order_id", nullable = false)
	private JetOrder jetOrder;

	public Requirement() {
	}

	public Requirement(JetOrder jetOrder, String description) {
		this.jetOrder = jetOrder;
		this.description = description;
	}

	public Long getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public JetOrder getJetOrder() {
		return jetOrder;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setJetOrder(JetOrder jetOrder) {
		this.jetOrder = jetOrder;
	}

	@Override
	public String toString() {
		return String.format("Requirement[id=%s, description='%s',"
				+ " jetOrder.id='%s']",
				id, description, (jetOrder == null) ? null : jetOrder.getId());
	}

}
