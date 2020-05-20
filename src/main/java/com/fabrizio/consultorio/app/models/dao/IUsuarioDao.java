package com.fabrizio.consultorio.app.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fabrizio.consultorio.app.models.entity.Usuario;

@Repository
public interface IUsuarioDao extends JpaRepository<Usuario, Long>{

	@Query("SELECT u FROM Usuario u WHERE u.fechaBaja = null AND u.mail like %?1%")
	public Usuario porMail(String mail);
	
	public List<Usuario> findByUsernameLikeIgnoreCase(String term);
}
