package com.fabrizio.consultorio.app.models.service;

import java.util.List;

import com.fabrizio.consultorio.app.models.entity.Paciente;
import com.fabrizio.consultorio.app.models.entity.Terapeuta;

public interface ITerapeutaService {

	public List<Terapeuta> findAll();

	public void save(Terapeuta terapeuta);

	public List<Terapeuta> findByNombre(String term);

	public void detele(Terapeuta terapeuta);

	public void darDeBaja(Terapeuta terapeuta);
	
	public Terapeuta findOne(Long id);

	public void deleteAll();

	public Terapeuta findTerapeutaById(Long id);

	public Paciente findPacienteById(Long id);

	public List<Terapeuta> findTerapeutaByNombre(String term);
	
	public Terapeuta byUsuarioId(Long id);

}
