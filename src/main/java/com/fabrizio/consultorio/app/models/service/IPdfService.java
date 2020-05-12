package com.fabrizio.consultorio.app.models.service;

import java.util.List;

import com.fabrizio.consultorio.app.models.entity.Pdf;

public interface IPdfService {

	public List<Pdf> findAll();

	public void save(Pdf pdf);

	public List<Pdf> findByNombre(String term);

	public void detele(Pdf pdf);

	public void darDeBaja(Pdf pdf);
	
	public Pdf findOne(Long id);

	public void deleteAll();

}
