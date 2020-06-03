package com.fabrizio.consultorio.app.controllers;

import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fabrizio.consultorio.app.models.entity.Usuario;
import com.fabrizio.consultorio.app.models.entity.UsuarioConsulta;
import com.fabrizio.consultorio.app.models.service.EmailService;
import com.fabrizio.consultorio.app.models.service.IPacienteService;
import com.fabrizio.consultorio.app.models.service.ITerapeutaService;
import com.fabrizio.consultorio.app.models.service.IUsuarioService;


import static com.fabrizio.util.Texto.ERROR_LABEL;
import static com.fabrizio.util.Texto.SUCCESS_LABEL;
import static com.fabrizio.util.Texto.USUARIOID_LABEL;

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
	
	@Autowired
	private EmailService mailService;

	@GetMapping("/")
	public String inicio(Model model) {
		UsuarioConsulta nuevoUsuario = new UsuarioConsulta();
		model.addAttribute("usuarioConsulta", nuevoUsuario);
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UserDetails) {
		  String username = ((UserDetails)principal).getUsername();
		  Usuario usuario = usuarioService.findByMail(username);
		  switch(usuario.getRol()) {
		  case TERAPEUTA:
			  model.addAttribute(USUARIOID_LABEL, terapeutaService.byUsuarioId(usuario.getId()).getId());
//			  log.info("SESION: usuario terapeuta: " + username);
			  break;
		  case PACIENTE:
			  model.addAttribute(USUARIOID_LABEL, pacienteService.byUsuarioId(usuario.getId()).getId());
//			  log.info("SESION: usuario paciente: " + username);
			  break;
		case ADMINISTRADOR:
			break;
		case USUARIO:
			break;
		default:
			break;
		  }
		 
		} 
		
		
		return "inicio";
	}
	
	@PostMapping("/formulario")
	public String usuarioFormulario(@Valid UsuarioConsulta usuario, BindingResult result, Model model, RedirectAttributes flash) {
		if(result.hasErrors()) {
			flash.addFlashAttribute(ERROR_LABEL, "Formulario no ha sido llenado correctamente.");
			return "redirect:/";
		}
		mailService.formularioContacto(usuario);
		flash.addFlashAttribute(SUCCESS_LABEL, "Gracias, "+usuario.getNombre()+". Tu consulta ha sido enviada.");
		UsuarioConsulta nuevoUsuario = new UsuarioConsulta();
		model.addAttribute("usuarioConsulta", nuevoUsuario);
		return "redirect:/";
	}
	
	
	@GetMapping("/prueba")
	public String prueba() {
		return "pruebafecha";
	}
	
}
