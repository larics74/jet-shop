package org.larics.jetshop.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/*
 * Jet model.
 * 
 * @author Igor Laryukhin
 */
@Entity
@Table(name = "jet_model")
public class JetModel {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", nullable = false, updatable = false)
	private Long id;

	@NotNull
	@Size(min = 3, max = 20)
	@Column(name = "name", nullable = false, unique = true)
	private String name;

	@Size(min = 5, max = 100)
	@Column(name = "description")
	private String description;

	@Lob
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "drawing", nullable = false)
	private byte[] drawing;

	public JetModel() {
	}

	public JetModel(String name, String description) {
		this.name = name;
		this.description = description;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public byte[] getDrawing() {
		return drawing;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDrawing(byte[] drawing) {
		this.drawing = drawing;
	}

	@Override
	public String toString() {
		return String.format("JetModel[id=%d, name='%s', description='%s',"
				+ " drawing='%s']",
			id, name, description, drawing == null ? null : "notNull");
	}

}
