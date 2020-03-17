package com.fabrizio.consultorio.app.models.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Paciente extends Usuario {

	
	@Column(name = "fecha_baja")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date fechaBaja;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Turno> turnos;
	
	@ManyToMany(mappedBy="pacientes")
	@JsonIgnore
	private List<Terapeuta> terapeutas;
	
	public Paciente() {
		super();
	}


	public Paciente(List<Turno> turnos, List<Terapeuta> terapeutas) {
		super();
		this.turnos = turnos;
		this.terapeutas = terapeutas;
	}


	public List<Turno> getTurnos() {
		return turnos;
	}


	public void setTurnos(List<Turno> turnos) {
		this.turnos = turnos;
	}


	public List<Terapeuta> getTerapeutas() {
		return terapeutas;
	}


	public void setTerapeutas(List<Terapeuta> terapeutas) {
		this.terapeutas = terapeutas;
	}


	public Date getFechaBaja() {
		return fechaBaja;
	}


	public void setFechaBaja(Date fechaBaja) {
		this.fechaBaja = fechaBaja;
	}
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void addTerapeuta(Terapeuta terapeuta) {
		this.terapeutas.add(terapeuta);
	}

}
