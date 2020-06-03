package com.fabrizio.consultorio.app.controllers;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fabrizio.consultorio.app.models.entity.Paciente;
import com.fabrizio.consultorio.app.models.entity.Turno;
import com.fabrizio.consultorio.app.models.entity.Usuario;
import com.fabrizio.consultorio.app.models.service.IPacienteService;
import com.fabrizio.consultorio.app.models.service.ITerapeutaService;
import com.fabrizio.consultorio.app.models.service.ITurnoService;
import com.fabrizio.consultorio.app.models.service.IUsuarioService;

import static com.fabrizio.util.Texto.TITULO_LABEL;
import static com.fabrizio.util.Texto.TURNO_LABEL;
import static com.fabrizio.util.Texto.TURNOS_LABEL;
import static com.fabrizio.util.Texto.PACIENTE_LABEL;
import static com.fabrizio.util.Texto.USUARIOID_LABEL;
import static com.fabrizio.util.Texto.INFO_LABEL;
@Controller
@RequestMapping("/"+TURNO_LABEL)
public class TurnoController {

	@Autowired
	private ITurnoService turnoService;
	
	@Autowired
	private IPacienteService pacienteService;
	
	@Autowired
	private IUsuarioService usuarioService;
	
	@Autowired
	private ITerapeutaService terapeutaService;
	
	
	
	@PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR')")
	@GetMapping("/listar")
	public String listarTurnos(Model model, Authentication authentication, HttpServletRequest request, Locale locale) {
		model.addAttribute(TITULO_LABEL, "Todos los turnos");
		model.addAttribute(TURNOS_LABEL, turnoService.listarSortedObject(turnoService.listarTodosActivos()));
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
		return TURNOS_LABEL;
		
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR','ROLE_TERAPEUTA','ROLE_PACIENTE')")
	@GetMapping("/listar/{id}")
	public String listarTurnosPorPaciente(@PathVariable Long id, Model model, Authentication authentication, HttpServletRequest request, Locale locale) {
		Paciente paciente = pacienteService.findOne(id);
		
		model.addAttribute(TITULO_LABEL, "Todos los turnos de: "+paciente.getApellido() + " " + paciente.getUsername());
		model.addAttribute(TURNOS_LABEL, turnoService.listarSortedObject(turnoService.listarTodosActivos(paciente)));
		model.addAttribute(PACIENTE_LABEL, paciente);
		

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
		return TURNOS_LABEL;
		
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR')")
	@GetMapping("/listarAnteriores")
	public String listarTurnosAnteriores(Model model, Authentication authentication, HttpServletRequest request, Locale locale) {
		model.addAttribute(TITULO_LABEL, "Todos los turnos pasados.");
		model.addAttribute(TURNOS_LABEL, turnoService.listarSortedObject(turnoService.listarPasados()));
		
		
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
		return TURNOS_LABEL;
		
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR','ROLE_TERAPEUTA','ROLE_PACIENTE')")
	@GetMapping("/listarAnteriores/{id}")
	public String listarTurnosAnteriores(@PathVariable Long id, Model model, Authentication authentication, HttpServletRequest request, Locale locale) {
		Paciente paciente = pacienteService.findOne(id);
		model.addAttribute(TITULO_LABEL, "Turnos pasados de: "+paciente.getApellido() + " " + paciente.getUsername());
		model.addAttribute(TURNOS_LABEL, turnoService.listarSortedObject(turnoService.listarPasados(paciente)));
		model.addAttribute(PACIENTE_LABEL, paciente);
		
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
		return TURNOS_LABEL;
		
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR')")
	@GetMapping("/listarFuturos")
	public String listarTurnosFuturos(Model model, Authentication authentication, HttpServletRequest request, Locale locale) {
		
		List<Turno> turnos = turnoService.listarSortedObject(turnoService.listarFuturos());
		
		model.addAttribute(TITULO_LABEL, "Turnos futuros");
		model.addAttribute(TURNOS_LABEL, turnos);
		
		
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
		return TURNOS_LABEL;
		
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR','ROLE_TERAPEUTA','ROLE_PACIENTE')")
	@GetMapping("/listarFuturos/{id}")
	public String listarTurnosFuturos(@PathVariable Long id, Model model, Authentication authentication, HttpServletRequest request, Locale locale) {
		Paciente paciente = pacienteService.findOne(id);
		
		model.addAttribute(TITULO_LABEL, "Turnos futuros de: "+paciente.getApellido() + " " + paciente.getUsername());
		model.addAttribute(TURNOS_LABEL, turnoService.listarSortedObject(turnoService.listarFuturos(paciente)));
		model.addAttribute(PACIENTE_LABEL, paciente);
		
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
		return TURNOS_LABEL;
		
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR','ROLE_TERAPEUTA','ROLE_PACIENTE')")
	@GetMapping("/listarEliminados")
	public String listarTurnosEliminados(Model model, Authentication authentication, HttpServletRequest request, Locale locale) {
		model.addAttribute(TITULO_LABEL, "Todos los turnos eliminados");
		model.addAttribute(TURNOS_LABEL, turnoService.listarSortedObject(turnoService.listarEliminados()));
		
		
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
		return TURNOS_LABEL;
		
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR','ROLE_TERAPEUTA','ROLE_PACIENTE')")
	@GetMapping("/listarEliminados/{id}")
	public String listarTurnosEliminados(@PathVariable Long id, Model model, Authentication authentication, HttpServletRequest request, Locale locale) {
		Paciente paciente = pacienteService.findOne(id);
		model.addAttribute(TITULO_LABEL, "Turnos eliminados de: "+paciente.getApellido() + " " + paciente.getUsername());
		model.addAttribute(TURNOS_LABEL, turnoService.listarSortedObject(turnoService.listarEliminados(paciente)));
		model.addAttribute(PACIENTE_LABEL, paciente);
		
		
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
		return TURNOS_LABEL;
		
	}
	
	
	@PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR')")
	@GetMapping(value = "/eliminar/{id}")
	public String eliminar(@PathVariable(value = "id") Long id, Model model, RedirectAttributes flash) {
		if (id<0){
			
		}
		Turno turno = turnoService.findOne(id);
		turnoService.darDeBaja(turno);
		model.addAttribute(TITULO_LABEL, "Eliminar turno de: "+turno.getPaciente().getUsername()+" "+turno.getPaciente().getApellido());
		flash.addFlashAttribute(INFO_LABEL, "Eliminar turno de: "+turno.getPaciente().getUsername()+" "+turno.getPaciente().getApellido());
		model.addAttribute(TURNO_LABEL, turno);
		return "eliminarTurno";
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR')")
	@PostMapping(value = "/eliminar")
	public String eliminar(@Valid Turno turno, Model model, RedirectAttributes flash) {
			flash.addFlashAttribute("success", "Turno de: "+turno.getPaciente().getUsername()+" "+turno.getPaciente().getApellido()+" eliminado.");
			turnoService.darDeBaja(turno);
		return "redirect:/turno/listarEliminados/"+turno.getPaciente().getId();
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR')")
	@RequestMapping(value = "/eliminarTodos")
	public String eliminar(RedirectAttributes flash) {

		turnoService.deleteAll();
		flash.addFlashAttribute("success", "Todas las turnos fueron eliminadas con éxito");

		return "redirect:/receta/listar";
	}
	
	
}
