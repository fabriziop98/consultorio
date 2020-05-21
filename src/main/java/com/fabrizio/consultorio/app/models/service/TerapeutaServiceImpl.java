package com.fabrizio.consultorio.app.models.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fabrizio.consultorio.app.models.dao.IPacienteDao;
import com.fabrizio.consultorio.app.models.dao.ITerapeutaDao;
import com.fabrizio.consultorio.app.models.entity.Paciente;
import com.fabrizio.consultorio.app.models.entity.Terapeuta;

@Service
public class TerapeutaServiceImpl implements ITerapeutaService {

	@Autowired
	ITerapeutaDao terapeutaDao;
	
	@Autowired
	IPacienteDao pacienteDao;
	
	@Autowired(required=false)
	Terapeuta terapeuta;
	 
	@Override
	public List<Terapeuta> findAll() {
		return terapeutaDao.findAll();
	}

	@Override
	public void save(Terapeuta terapeuta) {
		terapeutaDao.save(terapeuta);
		
	}

	@Override
	public List<Terapeuta> findByNombre(String term) {
		return terapeutaDao.findByUsernameLikeIgnoreCase(term);
	}

	@Override
	public void detele(Terapeuta terapeuta) {
		terapeutaDao.delete(terapeuta);
		
	}

	@Override
	public void darDeBaja(Terapeuta terapeuta) {
		terapeuta.setFechaBaja(new Date());
		
		terapeutaDao.save(terapeuta);
		
	}

	@Override
	public Terapeuta findOne(Long id) {
		return terapeutaDao.findById(id).orElse(null);
	}

	@Override
	public void deleteAll() {
		terapeutaDao.deleteAll();
		
	}

	@Override
	public Terapeuta findTerapeutaById(Long id) {
		return terapeutaDao.findById(id).orElse(null);
	}

	@Override
	public Paciente findPacienteById(Long id) {
		return pacienteDao.findById(id).orElse(null);
	}

	@Override
	public List<Terapeuta> findTerapeutaByNombre(String term) {
		return terapeutaDao.findByUsernameLikeIgnoreCase(term);
	}

	@Override
	public Terapeuta byUsuarioId(Long id) {
		return terapeutaDao.findByUsuarioId(id);
	}

}
