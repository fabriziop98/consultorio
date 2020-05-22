package com.fabrizio.consultorio.app.models.service;

import java.util.Date;
import java.util.List;

import com.fabrizio.consultorio.app.models.entity.Paciente;
import com.fabrizio.consultorio.app.models.entity.Turno;

public interface ITurnoService {
	
	public List<Turno> findAll();
	
	public List<Turno> listarTodosActivos();
	
	public List<Turno> listarTodosActivos(Paciente paciente);
	
	public List<Turno> listarFuturos();
	
	public List<Turno> listarFuturos(Paciente paciente);
	
	public List<Turno> listarPasados();
	
	public List<Turno> listarPasados(Paciente paciente);
	
	public List<Turno> listarEliminados();
	
	public List<Turno> listarEliminados(Paciente paciente);
	
	public List<Turno> listarSortedObject(List<Turno> turnos);
	
	public void save(Turno turno);
	
	public void crear(Turno turno, Long idPaciente, String turnoString) throws Exception;
	
	public void darDeBaja(Turno turno);
	
	public Turno findOne(Long id);

	public void deleteAll();
	
	public List<Date> listarSorted(Paciente paciente);
	
	public List<Date> listarSorted();

	public List<Date> listarSorted(List<Turno> turnos);


}
