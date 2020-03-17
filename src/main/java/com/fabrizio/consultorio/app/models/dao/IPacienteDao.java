package com.fabrizio.consultorio.app.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fabrizio.consultorio.app.models.entity.Paciente;

@Repository
public interface IPacienteDao extends JpaRepository<Paciente, Long>{
	
	@Query("select p from Paciente p where p.username like %?1%")
	public List<Paciente> findPacienteByUsername(String term);
	
	public List<Paciente> findByUsernameLikeIgnoreCase(String term);
	
//	@Query("select p from paciente p where p.")

}
