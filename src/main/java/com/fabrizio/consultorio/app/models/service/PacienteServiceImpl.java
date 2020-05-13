package com.fabrizio.consultorio.app.models.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fabrizio.consultorio.app.models.dao.IPacienteDao;
import com.fabrizio.consultorio.app.models.entity.Paciente;
import com.fabrizio.consultorio.app.models.entity.Terapeuta;
import com.fabrizio.consultorio.app.models.entity.Turno;

@Service
public class PacienteServiceImpl implements IPacienteService{
	
	@Autowired
	private IPacienteDao pacienteDao;
	
	@Autowired
	private ITerapeutaService terapeutaService;
	
	@SuppressWarnings("unused")
	@Autowired(required=false)
	private Paciente paciente;

	@Override
	@Transactional(readOnly = true)
	public List<Paciente> findAll() {
		return pacienteDao.findAll();
	}

	@Override
	@Transactional
	public void save(Paciente paciente) {
		pacienteDao.save(paciente);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Paciente> findByNombre(String term) {
		return pacienteDao.findByUsernameLikeIgnoreCase(term);
	}

	@Override
	public void detele(Paciente paciente) {
		pacienteDao.delete(paciente);
	}

	@Override
	@Transactional
	public void darDeBaja(Paciente paciente) {
		paciente.setFechaBaja(new Date());
		
		pacienteDao.save(paciente);
		
	}

	@Override
	public Paciente findOne(Long id) {
		return pacienteDao.findById(id).orElse(null);
	}

	@Override
	public void deleteAll() {
		pacienteDao.deleteAll();
		
	}

	@Override
	public List<Paciente> findByTerapeutaId(Long id) {
		Terapeuta terapeuta = terapeutaService.findOne(id);
		return terapeuta.getPacientes();
	}

}
