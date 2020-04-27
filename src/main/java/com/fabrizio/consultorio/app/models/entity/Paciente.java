package com.fabrizio.consultorio.app.models.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "pacientes")
public class Paciente implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotEmpty
	private String username;

	@NotEmpty
	private String apellido;

	@NotEmpty
	@Email
	private String mail;

	@NotEmpty
	@Column(length = 60, unique = false)
	private String password;

	@NotNull
	@Column(name = "fecha_alta")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date fechaAlta;

	private String foto;

	@ElementCollection(targetClass = String.class)
	private List<String> pdf;

	@PrePersist
	public void prePersist() {
		fechaAlta = new Date();
	}

	@Column(name = "fecha_baja")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date fechaBaja;

	@ManyToMany(cascade = CascadeType.ALL)
	private List<Turno> turnos;

	@ManyToMany
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getFechaAlta() {
		return fechaAlta;
	}

	public void setFechaAlta(Date fechaAlta) {
		this.fechaAlta = fechaAlta;
	}

	public String getFoto() {
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}

	public List<String> getPdf() {
		return pdf;
	}

	public void setPdf(List<String> pdf) {
		this.pdf = pdf;
	}

	public void addPdf(String pdf) {
		this.pdf.add(pdf);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void addTerapeuta(Terapeuta terapeuta) {
		this.terapeutas.add(terapeuta);
	}

	public void addTurno(Turno turno) {
		this.turnos.add(turno);
	}

	@Override
	public String toString() {
		return username;
	}

}
