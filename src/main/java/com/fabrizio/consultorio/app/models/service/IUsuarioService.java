package com.fabrizio.consultorio.app.models.service;

import java.util.List;

import com.fabrizio.consultorio.app.models.entity.Usuario;

public interface IUsuarioService {
	
	public List<Usuario> findAll();
	
	public void save(Usuario usuario) throws Exception;
	
	public List<Usuario> findByNombre(String term);
	
	public void detele(Usuario usuario);
	
	public void darDeBaja(Usuario usuario);

	public Usuario findOne(Long id);
	
	public void deleteAll();


}
