package com.fabrizio.consultorio.app.models.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fabrizio.consultorio.app.models.dao.IPacienteDao;
import com.fabrizio.consultorio.app.models.dao.ITurnoDao;
import com.fabrizio.consultorio.app.models.entity.Paciente;
import com.fabrizio.consultorio.app.models.entity.Turno;

@Service
public class TurnoServiceImpl implements ITurnoService {

	@Autowired
	ITurnoDao turnoDao;
	
	@Autowired
	IPacienteDao pacienteDao;

	@Override
	public List<Turno> findAll() {
		return turnoDao.findAll();
	}

	@Override
	public List<Turno> listarFuturos() {
		List<Turno> proximosTurnos = new ArrayList<>();
		for(Turno turno : findAll()) {
			if(turno.getFechaTurno().after(new Date())) {
				proximosTurnos.add(turno);
			}
		}
		return proximosTurnos;
	}
	
	@Override
	public List<Turno> listarFuturos(Paciente paciente) {
		List<Turno> proximosTurnos = new ArrayList<>();
		for(Turno turno : paciente.getTurnos()) {
			if(turno.getFechaTurno().after(new Date())) {
				proximosTurnos.add(turno);
			}
		}
		return proximosTurnos;
	}

	@Override
	public List<Turno> listarPasados() {
		List<Turno> anterioresTurnos = new ArrayList<>();
		for(Turno turno : findAll()) {
			if(turno.getFechaTurno().before(new Date())) {
				anterioresTurnos.add(turno);
			}
		}
		return anterioresTurnos;
	}
	
	@Override
	public List<Turno> listarPasados(Paciente paciente) {
		List<Turno> anterioresTurnos = new ArrayList<>();
		for(Turno turno : paciente.getTurnos()) {
			if(turno.getFechaTurno().before(new Date())) {
				anterioresTurnos.add(turno);
			}
		}
		return anterioresTurnos;
	}

	@Override
	public void save(Turno turno) {
		turnoDao.save(turno);
	}

	@Override
	public void delete(Turno turno) {
		turnoDao.delete(turno);
	}

	@Override
	public Turno findOne(Long id) {
		return turnoDao.findById(id).orElse(null);
	}

	@Override
	public void deleteAll() {
		turnoDao.deleteAll();
	}
	
	@Override
	public List<Date> listarSorted(Paciente paciente) {
		List<Turno> turnos = paciente.getTurnos();
		List<Date> turnoDate = new ArrayList<>();
		for(Turno turno: turnos) {
			turnoDate.add(turno.getFechaTurno());
		}
		Collections.sort(turnoDate);
		return turnoDate;
	}

	@Override
	public List<Date> listarSorted() {
		List<Turno> turnos = turnoDao.findAll();
		List<Date> turnoDate = new ArrayList<>();
		for(Turno turno: turnos) {
			turnoDate.add(turno.getFechaTurno());
		}
		Collections.sort(turnoDate);
		return turnoDate;
	}
	
	@Override
	public List<Date> listarSorted(List<Turno> turnos) {
		List<Date> turnoDate = new ArrayList<>();
		for(Turno turno: turnos) {
			turnoDate.add(turno.getFechaTurno());
		}
		Collections.sort(turnoDate);
		
		return turnoDate;
	}

	@Override
	public List<Turno> listarSortedObject(List<Turno> turnos) {
		Collections.sort(turnos);
		return turnos;
	}


	

}
