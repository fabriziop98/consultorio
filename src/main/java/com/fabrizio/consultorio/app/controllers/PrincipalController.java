package com.fabrizio.consultorio.app.controllers;

import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fabrizio.consultorio.app.models.entity.Usuario;
import com.fabrizio.consultorio.app.models.service.IPacienteService;
import com.fabrizio.consultorio.app.models.service.ITerapeutaService;
import com.fabrizio.consultorio.app.models.service.IUsuarioService;

@Controller
@RequestMapping(value= {"","/inicio"})
public class PrincipalController {
	
	protected Log log;
	
	@Autowired
	private IUsuarioService usuarioService;
	
	@Autowired
	private ITerapeutaService terapeutaService;
	
	@Autowired
	private IPacienteService pacienteService;

	@GetMapping("/")
	public String inicio(Model model) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UserDetails) {
		  String username = ((UserDetails)principal).getUsername();
		  Usuario usuario = usuarioService.findByMail(username);
		  switch(usuario.getRol()) {
		  case TERAPEUTA:
			  model.addAttribute("usuarioId", terapeutaService.byUsuarioId(usuario.getId()).getId());
//			  log.info("SESION: usuario terapeuta: " + username);
			  break;
		  case PACIENTE:
			  model.addAttribute("usuarioId", pacienteService.byUsuarioId(usuario.getId()).getId());
//			  log.info("SESION: usuario paciente: " + username);
			  break;
		case ADMINISTRADOR:
			break;
		case USUARIO:
			break;
		default:
			break;
		  }
		 
		} else {
//		  String username = principal.toString();
//		  log.info("SESION: usuario invitado");
		  
		}
		
		
		return "inicio";
	}
	
	
	@GetMapping("/prueba")
	public String prueba() {
		return "pruebafecha";
	}
	
}
