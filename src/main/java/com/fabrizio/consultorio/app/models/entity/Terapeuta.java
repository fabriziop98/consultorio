package com.fabrizio.consultorio.app.models.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
public class Terapeuta extends Usuario {
	
	
	@ManyToMany
	private List<Paciente> pacientes;
	
	@Column(name = "fecha_baja")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date fechaBaja;
	

	public List<Paciente> getPacientes() {
		return pacientes;
	}

	public void setPacientes(List<Paciente> pacientes) {
		this.pacientes = pacientes;
	}

	public Date getFechaBaja() {
		return fechaBaja;
	}

	public void setFechaBaja(Date fechaBaja) {
		this.fechaBaja = fechaBaja;
	}
	
	
	private static final long serialVersionUID = 1L;


	public void addPaciente(Paciente paciente) {
		this.pacientes.add(paciente);
		
	}

}
