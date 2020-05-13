package com.fabrizio.consultorio.app.models.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;


@Entity
@Table(name = "turnos")
public class Turno {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
//	@NotNull
//	HACERLO UNICO AL TURNO
	@Column(name = "fecha_turno")
	@DateTimeFormat(pattern = "M/d/yyyy hh:mm aa")
	private Date fechaTurno;
	
	@ManyToMany(mappedBy="turnos")
	private List<Paciente> paciente;
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getFechaTurno() {
		return fechaTurno;
	}

	public void setFechaTurno(Date fechaTurno) {
		this.fechaTurno = fechaTurno;
	}

	public List<Paciente> getPaciente() {
		return paciente;
	}

	public void setPaciente(List<Paciente> paciente) {
		this.paciente = paciente;
	}

	@Override
	public String toString() {
		return fechaTurno+"";
	}
	
	
}
