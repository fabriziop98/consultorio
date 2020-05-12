package com.fabrizio.consultorio.app.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fabrizio.consultorio.app.models.entity.Pdf;

@Repository
public interface IPdfDao extends JpaRepository<Pdf, Long>{
	
	@Query("select p from Pdf p where p.nombre like %?1%")
	public List<Pdf> findPdfByNombre(String term);
	
	public List<Pdf> findByNombreLikeIgnoreCase(String term);
	

}
