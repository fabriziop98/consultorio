package com.fabrizio.consultorio.app.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fabrizio.consultorio.app.models.dao.IPdfDao;
import com.fabrizio.consultorio.app.models.entity.Pdf;

@Service
public class PdfServiceImpl implements IPdfService{
	
	@Autowired
	private IPdfDao pdfDao;
	
	
	@SuppressWarnings("unused")
	@Autowired(required=false)
	private Pdf pdf;

	@Override
	@Transactional(readOnly = true)
	public List<Pdf> findAll() {
		return pdfDao.findAll();
	}

	@Override
	@Transactional
	public void save(Pdf pdf) {
		pdfDao.save(pdf);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Pdf> findByNombre(String term) {
		return pdfDao.findByNombreLikeIgnoreCase(term);
	}

	@Override
	public void detele(Pdf pdf) {
		pdfDao.delete(pdf);
	}

	@Override
	@Transactional
	public void darDeBaja(Pdf pdf) {
		pdf.setEliminado(true);
		pdfDao.save(pdf);
		
	}

	@Override
	public Pdf findOne(Long id) {
		return pdfDao.findById(id).orElse(null);
	}

	@Override
	public void deleteAll() {
		pdfDao.deleteAll();
		
	}

	}
