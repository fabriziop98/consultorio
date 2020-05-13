package com.fabrizio.consultorio.app.models.service;

import java.util.List;

import com.fabrizio.consultorio.app.models.entity.Paciente;

public interface IPacienteService {
	
	public List<Paciente> findAll();
	
	public void save(Paciente paciente);
	
	public List<Paciente> findByNombre(String term);
	
	public void detele(Paciente paciente);
	
	public void darDeBaja(Paciente paciente);

	public Paciente findOne(Long id);
	
	public void deleteAll();

	public List<Paciente> findByTerapeutaId(Long id);

}
