package org.larics.jetshop.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;

/*
 * Jet order.
 * 
 * @author Igor Laryukhin
 */
@Entity
@Table(name = "jet_order")
public class JetOrder {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", nullable = false, updatable = false)
	private Long id;

	@NotNull
	@NumberFormat(pattern = "###,###,###")
	@Min(1)
	@Max(999_999_999)
	@Column(name = "line_number", nullable = false, unique = true)
	private Integer lineNumber;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id", nullable = false)
	private User customer;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "jet_model_id", nullable = false)
	private JetModel jetModel;
	
	// Note: there should be some kind of validation rules for delivery date,
	// depending on factory load.
	@NotNull
	@DateTimeFormat (pattern="yyyy-MM-dd")
	@Column(name = "delivery_date", nullable = false)
	private LocalDate deliveryDate;
	
	@OneToMany(mappedBy = "jetOrder", cascade=CascadeType.ALL,
			orphanRemoval = true, fetch = FetchType.LAZY)
	@Valid
	private List<Requirement> requirements = new ArrayList<>();

	public JetOrder() {
	}

	public JetOrder(Integer lineNumber, User customer, JetModel jetModel,
			LocalDate deliveryDate) {

		this.lineNumber = lineNumber;
		this.customer = customer;
		this.jetModel = jetModel;
		this.deliveryDate = deliveryDate;
	}

	public Long getId() {
		return id;
	}
	
	public Integer getLineNumber() {
		return lineNumber;
	}

	public User getCustomer() {
		return customer;
	}

	public JetModel getJetModel() {
		return jetModel;
	}
	
	public List<Requirement> getRequirements() {
		return requirements;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public void setLineNumber(Integer lineNumber) {
		this.lineNumber = lineNumber;
	}	

	public void setCustomer(User customer) {
		this.customer = customer;
	}	

	public void setJetModel(JetModel jetModel) {
		this.jetModel = jetModel;
	}
	
	public void setRequirements(List<Requirement> requirements) {
		this.requirements.clear();
		for (Requirement requirement : requirements) {
			addRequirement(requirement);
		}
	}
	
	public LocalDate getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(LocalDate deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public void addRequirement(Requirement requirement) {
		requirement.setJetOrder(this);
		requirements.add(requirement);
	}
	
	public void removeRequirement(Requirement requirement) {
		requirement.setJetOrder(null);
		requirements.remove(requirement);
	}

	@Override
	public String toString() {
		String customer = "customer";
		try {
			customer = getCustomer().toString();
		} catch (Exception e) {
		}
		String jetModel = "jetModel";
		try {
			jetModel = getJetModel().toString();
		} catch (Exception e) {
		}
		String requirements = "requirements";
		try {
			requirements = getRequirements().toString();
		} catch (Exception e) {
		}
		return String.format(
				"JetOrder[id=%s, lineNumber='%s', customer='%s', jetModel='%s'"
						+ ", deliveryDate='%s', requirements='%s']",
				id, lineNumber, customer, jetModel, deliveryDate, requirements);
	}
}
