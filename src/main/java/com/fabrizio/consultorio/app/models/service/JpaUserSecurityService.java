package com.fabrizio.consultorio.app.models.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fabrizio.consultorio.app.models.dao.IUsuarioDao;
import com.fabrizio.consultorio.app.models.entity.Usuario;


@Service
public class JpaUserSecurityService implements UserDetailsService {

	Logger log = LoggerFactory.getLogger(JpaUserSecurityService.class);

	@Autowired
	private IUsuarioDao usuarioRepository;

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) {

		Usuario usuario = null;
		List<GrantedAuthority> authorities = new ArrayList<>();

		try {
			usuario = usuarioRepository.porMail(username);
		} catch (Exception e1) {
			log.error("Error al buscar usuario.", e1);
		}

		if (usuario == null) {
			log.error("Error en el Login: no existe el usuario '" + username + "' en el sistema!");
			throw new UsernameNotFoundException("Username: " + username + " no existe en el sistema!");
		}

		switch(usuario.getRol()) {
		case ADMINISTRADOR:
			authorities.add(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"));
			break;
		case TERAPEUTA:
			authorities.add(new SimpleGrantedAuthority("ROLE_TERAPEUTA"));
			break;
		case USUARIO:
			authorities.add(new SimpleGrantedAuthority("ROLE_USUARIO"));
			break;
		case PACIENTE:
			authorities.add(new SimpleGrantedAuthority("ROLE_PACIENTE"));
			break;
		default:
			authorities.add(new SimpleGrantedAuthority("ROLE_USUARIO"));
			break;
		}
		

		if (authorities.isEmpty()) {
			log.error("Error login: usuario " + usuario.getMail() + " no tiene roles asignados	");
		}

		return new User(usuario.getMail(), usuario.getPassword(), true, true, true, true, authorities);
	}
	
	public List<GrantedAuthority> getAuthorities(String username){
		List<GrantedAuthority> authorities = new ArrayList<>();
		Usuario usuario = null;
		try {
			usuario = usuarioRepository.porMail(username);
		} catch (Exception e1) {
			log.error("Error al buscar usuario.", e1);
		}
		authorities.add(new SimpleGrantedAuthority(usuario.getRol().getNombre()));
		return authorities;
	}

	public String nombreApellidoUsuarioLogueado(Authentication userAuth) {
		String userName = userAuth.getName();
		Usuario usuarioLogueado = usuarioRepository.porMail(userName);
		String userLog = null;
		if (usuarioLogueado != null) {
			userLog = usuarioLogueado.getUsername() + " " + usuarioLogueado.getApellido();
		} else {
			userLog = "Super Admin Golden";
		}
		return userLog;
	}

}
