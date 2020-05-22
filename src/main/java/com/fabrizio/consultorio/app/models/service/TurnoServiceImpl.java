package com.fabrizio.consultorio.app.models.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

	@Autowired
	IPacienteService pacienteService;

	@Override
	public List<Turno> findAll() {
		return turnoDao.findAll();
	}
	
	@Override
	public List<Turno> listarTodosActivos() {
		List<Turno> turnos = new ArrayList<>();
		for (Turno turno : findAll()) {
			if (!turno.isEliminado()) {
				turnos.add(turno);
			}
		}
		return turnos;
	}
	
	@Override
	public List<Turno> listarTodosActivos(Paciente paciente) {
		List<Turno> turnos = new ArrayList<>();
		for (Turno turno : paciente.getTurnos()) {
			if (!turno.isEliminado()) {
				turnos.add(turno);
			}
		}
		return turnos;
	}

	@Override
	public List<Turno> listarFuturos() {
		List<Turno> proximosTurnos = new ArrayList<>();
		for (Turno turno : findAll()) {
			if (turno.getFechaTurno().after(new Date())&& !(turno.isEliminado())) {
				proximosTurnos.add(turno);
			}
		}
		return proximosTurnos;
	}

	@Override
	public List<Turno> listarFuturos(Paciente paciente) {
		List<Turno> proximosTurnos = new ArrayList<>();
		for (Turno turno : paciente.getTurnos()) {
			if (turno.getFechaTurno().after(new Date())&& !(turno.isEliminado())) {
				proximosTurnos.add(turno);
			}
		}
		return proximosTurnos;
	}

	@Override
	public List<Turno> listarPasados() {
		List<Turno> anterioresTurnos = new ArrayList<>();
		for (Turno turno : findAll()) {
			if (turno.getFechaTurno().before(new Date())&& !(turno.isEliminado())) {
				anterioresTurnos.add(turno);
			}
		}
		return anterioresTurnos;
	}

	@Override
	public List<Turno> listarPasados(Paciente paciente) {
		List<Turno> anterioresTurnos = new ArrayList<>();
		for (Turno turno : paciente.getTurnos()) {
			if (turno.getFechaTurno().before(new Date())&& !(turno.isEliminado())) {
				anterioresTurnos.add(turno);
			}
		}
		return anterioresTurnos;
	}

	@Override
	public List<Turno> listarEliminados() {
		List<Turno> turnosEliminados = new ArrayList<>();
		for (Turno turno : findAll()) {
			if (turno.isEliminado()) {
				turnosEliminados.add(turno);
			}
		}
		return turnosEliminados;
	}

	@Override
	public List<Turno> listarEliminados(Paciente paciente) {
		List<Turno> turnosEliminados = new ArrayList<>();
		for (Turno turno : paciente.getTurnos()) {
			if (turno.isEliminado()) {
				turnosEliminados.add(turno);
			}
		}
		return turnosEliminados;
	}
	
	@Override
	public void save(Turno turno) {
		turnoDao.save(turno);
	}

	@Override
	public void crear(Turno turno, Long idPaciente, String turnoString) throws Exception {
		Turno t = new Turno();
		if (idPaciente == null) {
			throw new Exception();
		}
		if (turnoString == null || turnoString.isEmpty()) {
			throw new Exception();
		}
		
		if(turno.getTerapeuta()==null) {
			throw new Exception();
		}

		Paciente paciente = pacienteService.findOne(idPaciente);
		String dateTimePicker = turnoString;
		DateFormat format = new SimpleDateFormat("M/d/yyyy hh:mm aa", Locale.ENGLISH);
		try {
			t.setFechaTurno(format.parse(dateTimePicker));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		paciente.addTurno(t);
		t.setPaciente(paciente);
		t.setTerapeuta(turno.getTerapeuta());
		save(t);
	}

	@Override
	public void darDeBaja(Turno turno) {
		if(turno!= null) {
			turno.setObservacion(turno.getObservacion());
			turno.setEliminado(true);
			turnoDao.save(turno);
		} 
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
		for (Turno turno : turnos) {
			turnoDate.add(turno.getFechaTurno());
		}
		Collections.sort(turnoDate);
		return turnoDate;
	}

	@Override
	public List<Date> listarSorted() {
		List<Turno> turnos = turnoDao.findAll();
		List<Date> turnoDate = new ArrayList<>();
		for (Turno turno : turnos) {
			turnoDate.add(turno.getFechaTurno());
		}
		Collections.sort(turnoDate);
		return turnoDate;
	}

	@Override
	public List<Date> listarSorted(List<Turno> turnos) {
		List<Date> turnoDate = new ArrayList<>();
		for (Turno turno : turnos) {
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
