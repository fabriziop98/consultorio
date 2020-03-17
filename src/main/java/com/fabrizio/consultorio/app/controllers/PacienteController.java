package com.fabrizio.consultorio.app.controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fabrizio.consultorio.app.models.entity.Paciente;
import com.fabrizio.consultorio.app.models.entity.Terapeuta;
import com.fabrizio.consultorio.app.models.service.IPacienteService;
import com.fabrizio.consultorio.app.models.service.ITerapeutaService;
import com.fabrizio.consultorio.app.models.service.IUploadFileService;

@Controller
@RequestMapping("/paciente")
public class PacienteController {

	@Autowired
	private IPacienteService pacienteService;
	
	@Autowired
	private ITerapeutaService terapeutaService;
	
	@Autowired
	private IUploadFileService uploadFileService;
	
	@Secured("ROLE_USER")
	@GetMapping("/listar")
	public String listarPacientes(Model model, Authentication authentication, HttpServletRequest request, Locale locale) {
		
		List<Paciente> pacientes = pacienteService.findAll();
		
		model.addAttribute("titulo", "Pacientes");
		model.addAttribute("pacientes", pacientes);
		return "pacientes";
		
	}
	
	@GetMapping("/form/{id}")
	public String displayAsignarTerapeuta(@PathVariable(value = "id") Long id, Map<String, Object> model, @RequestParam(name = "item_id[]", required = false) Long[] itemId,  Model modelo , RedirectAttributes flash) {
		Paciente paciente = pacienteService.findOne(id);
		
		modelo.addAttribute("pacientes", paciente);

		for (int i = 0; i < itemId.length; i++) {
			Terapeuta terapeuta = terapeutaService.findTerapeutaById(itemId[i]);
			paciente.addTerapeuta(terapeuta);
		}
		
		if (paciente == null) {
			flash.addFlashAttribute("error", "El cliente no existe en la base de datos");
			return "redirect:/paciente/listar";
		}
		model.put("paciente", paciente);
		model.put("titulo", "Terapeuta para paciente: " + paciente.getUsername());
		
		pacienteService.save(paciente);
		
		return "redirect:/paciente/listar";
	}
	
	@PostMapping("/form1")
	public String asignarTerapeuta(@Valid Paciente paciente, BindingResult result, Model model,
			@RequestParam(name = "item_id[]", required = false) Long[] itemId, 
			RedirectAttributes flash, SessionStatus status) {
		
		Paciente paciente = pacienteService.findOne(id);
		
		for (int i = 0; i < itemId.length; i++) {
			Terapeuta terapeuta = terapeutaService.findTerapeutaById(itemId[i]);
			paciente.addTerapeuta(terapeuta);
		}
		
		pacienteService.save(paciente);
		
		
		return "redirect:/paciente/listar";
	}
	
	@Secured("ROLE_USER")
	@GetMapping("/listar/{id}")
	public String verPacienteTerapeuta(@PathVariable(value="id") Long id, Map<String, Object> model, RedirectAttributes flash) {
		List<Paciente> pacientes = pacienteService.findByTerapeutaId(id);
		
		
		return "pacientes";
	}
	
	@GetMapping(value = "/cargar-terapeuta/{term}", produces = { "application/json" })
	public @ResponseBody List<Terapeuta> cargarTerapeuta(@PathVariable String term) {
		return terapeutaService.findTerapeutaByNombre(term);
	}
	
	@Secured("ROLE_USER")
	@GetMapping(value = "/ver/{id}")
	public String ver(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash) {
		Paciente paciente = pacienteService.findOne(id);
		if (paciente == null) {
			flash.addFlashAttribute("error", "El cliente no existe en la base de datos");
			return "redirect:/paciente/listar";
		}
		model.put("paciente", paciente);
		model.put("titulo", "Detalle paciente: " + paciente.getUsername());
		return "verPaciente";
	}
	
	
	
	
	@Secured("ROLE_ADMIN")
	@GetMapping("/")
	public String displayCrearPaciente(Model model) {
		model.addAttribute("paciente", new Paciente());
		return "crearPaciente";
	}
	
	
	@Secured("ROLE_ADMIN")
	@PostMapping("/crear")
	public String crearPaciente(@Valid Paciente paciente, BindingResult result, Model model,
			@RequestParam(name="turno_id[]", required = false)Long[] turnoId, @RequestParam("file") MultipartFile foto, RedirectAttributes flash, SessionStatus status) {
//		if (result.hasErrors()) {
//			model.addAttribute("titulo", "Crear terapeuta");
//			return "crearTerapeuta";
//		}
		
		
		if(foto.isEmpty()) {
			model.addAttribute("titulo", "Crear Paciente");
			model.addAttribute("error", "Error: El perfil del paciente no puede no tener foto.");
			return "crearPaciente";
		}
		
		if(!foto.isEmpty()) {
			if(paciente.getId()!=null && paciente.getId() > 0 && paciente.getFoto() != null
					&& paciente.getFoto().length() >0) {
				uploadFileService.delete(paciente.getFoto());
			}
			String uniqueFilename = null;
			try {
				uniqueFilename = uploadFileService.copy(foto);
			} catch (IOException e) {
				e.printStackTrace();
			}
			flash.addAttribute("info", "Se ha subido correctamente la foto de perfil de "+paciente.getApellido()+".");
			paciente.setFoto(uniqueFilename);
		}
		
		flash.addFlashAttribute("success", "Paciente creado con éxito.");
		pacienteService.save(paciente);
		status.setComplete();
		return "redirect:/paciente/listar";
	}
	
	@GetMapping(value = "/uploads/{filename:.+}")
	public ResponseEntity<Resource> verFoto(@PathVariable String filename) {
//		Resource recurso = uploadFileService.load(filename);
		Resource recurso = null;
		try {
			recurso = uploadFileService.load(filename);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"")
				.body(recurso);
	}
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/eliminar/{id}")
	public String eliminar(@PathVariable(value = "id") Long id, RedirectAttributes flash) {
		if (id>0){
			Paciente paciente = pacienteService.findOne(id);
			pacienteService.darDeBaja(paciente);
			flash.addFlashAttribute("success", "Terapeuta: "+paciente.getApellido()+" dado de baja.");
			
		}
		return "redirect:/terapeuta/listar";
	}
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/eliminar")
	public String eliminar(RedirectAttributes flash) {

		pacienteService.deleteAll();
		flash.addFlashAttribute("success", "Todas las terapeutas fueron eliminadas con éxito");

		return "redirect:/receta/listar";
	}
	
	
	
}
