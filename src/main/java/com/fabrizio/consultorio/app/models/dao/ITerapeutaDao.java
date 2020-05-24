package com.fabrizio.consultorio.app.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fabrizio.consultorio.app.models.entity.Terapeuta;

@Repository
public interface ITerapeutaDao extends JpaRepository<Terapeuta, Long>{
	
	public List<Terapeuta> findByUsernameLikeIgnoreCase(String term);
	
	public Terapeuta findByUsuarioId(Long id);

	public List<Terapeuta> findByFechaBajaIsNull();

}
