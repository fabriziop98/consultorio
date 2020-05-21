package com.fabrizio.consultorio.app.controllers;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fabrizio.consultorio.app.models.entity.Paciente;
import com.fabrizio.consultorio.app.models.entity.Turno;
import com.fabrizio.consultorio.app.models.service.IPacienteService;
import com.fabrizio.consultorio.app.models.service.ITurnoService;

@Controller
@RequestMapping("/turno")
public class TurnoController {

	@Autowired
	private ITurnoService turnoService;
	
	@Autowired
	private IPacienteService pacienteService;
	
	
	@PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR')")
	@GetMapping("/listar")
	public String listarTurnos(Model model, Authentication authentication, HttpServletRequest request, Locale locale) {
		model.addAttribute("titulo", "Todos los turnos");
		model.addAttribute("turnos", turnoService.listarSortedObject(turnoService.findAll()));
		return "turnos";
		
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR','ROLE_TERAPEUTA','ROLE_PACIENTE')")
	@GetMapping("/listar/{id}")
	public String listarTurnosPorPaciente(@PathVariable Long id, Model model, Authentication authentication, HttpServletRequest request, Locale locale) {
		Paciente paciente = pacienteService.findOne(id);
		
		model.addAttribute("titulo", "Todos los turnos de: "+paciente.getApellido() + " " + paciente.getUsername());
		model.addAttribute("turnos", turnoService.listarSortedObject(paciente.getTurnos()));
		model.addAttribute("paciente", paciente);
		return "turnos";
		
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR')")
	@GetMapping("/listarAnteriores")
	public String listarTurnosAnteriores(Model model, Authentication authentication, HttpServletRequest request, Locale locale) {
		model.addAttribute("titulo", "Todos los turnos pasados.");
		model.addAttribute("turnos", turnoService.listarSortedObject(turnoService.listarPasados()));
		
		return "turnos";
		
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR','ROLE_TERAPEUTA','ROLE_PACIENTE')")
	@GetMapping("/listarAnteriores/{id}")
	public String listarTurnosAnteriores(@PathVariable Long id, Model model, Authentication authentication, HttpServletRequest request, Locale locale) {
		Paciente paciente = pacienteService.findOne(id);
		model.addAttribute("titulo", "Turnos pasados de: "+paciente.getApellido() + " " + paciente.getUsername());
		model.addAttribute("turnos", turnoService.listarSortedObject(turnoService.listarPasados(paciente)));
		model.addAttribute("paciente", paciente);
		return "turnos";
		
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR')")
	@GetMapping("/listarFuturos")
	public String listarTurnosFuturos(Model model, Authentication authentication, HttpServletRequest request, Locale locale) {
		
		List<Turno> turnos = turnoService.listarSortedObject(turnoService.listarFuturos());
		
		model.addAttribute("titulo", "Turnos futuros");
		model.addAttribute("turnos", turnos);
		return "turnos";
		
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR','ROLE_TERAPEUTA','ROLE_PACIENTE')")
	@GetMapping("/listarFuturos/{id}")
	public String listarTurnosFuturos(@PathVariable Long id, Model model, Authentication authentication, HttpServletRequest request, Locale locale) {
		Paciente paciente = pacienteService.findOne(id);
		
		model.addAttribute("titulo", "Turnos futuros de: "+paciente.getApellido() + " " + paciente.getUsername());
		model.addAttribute("turnos", turnoService.listarSortedObject(turnoService.listarFuturos(paciente)));
		model.addAttribute("paciente", paciente);
		return "turnos";
		
	}
	
	
	@PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR')")
	@RequestMapping(value = "/eliminar/{id}")
	public String eliminar(@PathVariable(value = "id") Long id, RedirectAttributes flash) {
		if (id>0){
			Turno turno = turnoService.findOne(id);
			turnoService.delete(turno);
			flash.addFlashAttribute("success", "Turno: "+turno.toString()+" eliminado.");
		}
		return "redirect:/turno/listar";
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR')")
	@RequestMapping(value = "/eliminar")
	public String eliminar(RedirectAttributes flash) {

		turnoService.deleteAll();
		flash.addFlashAttribute("success", "Todas las turnos fueron eliminadas con éxito");

		return "redirect:/receta/listar";
	}
	
	
}
