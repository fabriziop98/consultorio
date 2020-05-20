package com.fabrizio.consultorio.app.models.service;


import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fabrizio.consultorio.app.models.dao.IPacienteDao;
import com.fabrizio.consultorio.app.models.dao.ITerapeutaDao;
import com.fabrizio.consultorio.app.models.dao.IUsuarioDao;
import com.fabrizio.consultorio.app.models.entity.Paciente;
import com.fabrizio.consultorio.app.models.entity.Rol;
import com.fabrizio.consultorio.app.models.entity.Terapeuta;
import com.fabrizio.consultorio.app.models.entity.Usuario;

@Service
public class UsuarioServiceImpl implements IUsuarioService{
	
	@Autowired
	private IUsuarioDao usuarioDao;
	
	@Autowired
	private ITerapeutaDao terapeutaDao;
	
	@Autowired
	private IPacienteDao pacienteDao;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private AuthenticationManagerBuilder builder;
	
	
	@SuppressWarnings("unused")
	@Autowired(required=false)
	private Usuario usuario;

	@Override
	@Transactional(readOnly = true)
	public List<Usuario> findAll() {
		return usuarioDao.findAll();
	}

	@Override
	@Transactional
	public void save(Usuario usuario) throws Exception {
		if (usuarioDao.porMail(usuario.getMail()) != null) {
			throw new Exception();
		}
		usuario.setFechaAlta(new Date());
		
		switch(usuario.getRol()) {
		case TERAPEUTA:
			usuario.setRol(Rol.TERAPEUTA);
			Terapeuta terapeuta = new Terapeuta();
			terapeuta.setFechaAlta(usuario.getFechaAlta());
			terapeuta.setUsername(usuario.getUsername());
			terapeuta.setPassword(passwordEncoder.encode(usuario.getPassword()));
			terapeuta.setMail(usuario.getMail());
			terapeuta.setFoto(usuario.getFoto());
			terapeuta.setId(usuario.getId());
			terapeuta.setApellido(usuario.getApellido());
			terapeutaDao.save(terapeuta);
//			builder.inMemoryAuthentication()
//			.withUser(usuario.getUsername()).password(usuario.getPassword()).roles(usuario.getRol().getNombre());
			break;
		case ADMINISTRADOR:
			usuario.setRol(Rol.ADMINISTRADOR);
			break;
//			builder.inMemoryAuthentication()
//			.withUser(usuario.getUsername()).password(usuario.getPassword()).roles("ADMIN");
		case PACIENTE:
			usuario.setRol(Rol.PACIENTE);
			Paciente paciente = new Paciente();
			paciente.setFechaAlta(usuario.getFechaAlta());
			paciente.setUsername(usuario.getUsername());
			paciente.setPassword(passwordEncoder.encode(usuario.getPassword()));
			paciente.setMail(usuario.getMail());
			paciente.setApellido(usuario.getApellido());
			paciente.setId(usuario.getId());
			paciente.setFoto(usuario.getFoto());
			pacienteDao.save(paciente);
//			builder.inMemoryAuthentication()
//			.withUser(usuario.getUsername()).password(usuario.getPassword()).roles(usuario.getRol().getNombre());
			break;
		case USUARIO:
//			builder.inMemoryAuthentication()
//			.withUser(usuario.getUsername()).password(usuario.getPassword()).roles(usuario.getRol().getNombre());
			break;
		default:
			usuario.setRol(Rol.USUARIO);
//			builder.inMemoryAuthentication()
//			.withUser(usuario.getUsername()).password(usuario.getPassword()).roles(usuario.getRol().getNombre());
			break;		
		}
		usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
		usuarioDao.save(usuario);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Usuario> findByNombre(String term) {
		return usuarioDao.findByUsernameLikeIgnoreCase(term);
	}

	@Override
	public void detele(Usuario usuario) {
		usuarioDao.delete(usuario);
	}

	@Override
	@Transactional
	public void darDeBaja(Usuario usuario) {
		usuario.setFechaBaja(new Date());
		usuarioDao.save(usuario);
	}

	@Override
	public Usuario findOne(Long id) {
		return usuarioDao.findById(id).orElse(null);
	}

	@Override
	public void deleteAll() {
		usuarioDao.deleteAll();
		
	}


}
