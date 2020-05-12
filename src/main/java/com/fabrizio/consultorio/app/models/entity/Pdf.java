package com.fabrizio.consultorio.app.models.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "pdf")
public class Pdf implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String nombre;
	private boolean eliminado;
	@NotNull
	@Column(name = "fecha_subida")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date fechaSubida;
	
	
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public Date getFechaSubida() {
		return fechaSubida;
	}
	public void setFechaSubida(Date fechaSubida) {
		this.fechaSubida = fechaSubida;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public boolean isEliminado() {
		return eliminado;
	}
	public void setEliminado(boolean eliminado) {
		this.eliminado = eliminado;
	}

	private static final long serialVersionUID = 1L;
}
