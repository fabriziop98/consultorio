package com.fabrizio.consultorio.app.models.entity;

public enum Rol {

	ADMINISTRADOR,TERAPEUTA,PACIENTE,USUARIO;

	private String nombre;

	private Rol() {
	}

	private Rol(String nombre) {
		this.nombre = nombre;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	

}
