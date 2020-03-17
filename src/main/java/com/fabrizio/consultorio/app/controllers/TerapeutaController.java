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
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fabrizio.consultorio.app.models.entity.Paciente;
import com.fabrizio.consultorio.app.models.entity.Terapeuta;
import com.fabrizio.consultorio.app.models.service.ITerapeutaService;
import com.fabrizio.consultorio.app.models.service.IUploadFileService;

@Controller
@RequestMapping("/terapeuta")
public class TerapeutaController {

	@Autowired
	private ITerapeutaService terapeutaService;
	
	@Autowired
	private IUploadFileService uploadFileService;
	
	@Secured("ROLE_USER")
	@GetMapping("/listar")
	public String listarTerapeutas(Model model, Authentication authentication, HttpServletRequest request, Locale locale) {
		
		List<Terapeuta> terapeutas = terapeutaService.findAll();
		
		model.addAttribute("titulo", "Terapeutas");
		model.addAttribute("terapeutas", terapeutas);
		return "terapeutas";
		
	}
	
	@Secured("ROLE_ADMIN")
	@GetMapping("/")
	public String displayCrearTerapeuta(Model model) {
		model.addAttribute("terapeuta", new Terapeuta());
		return "crearTerapeuta";
	}
	
	@Secured("ROLE_ADMIN")
	@PostMapping("/crear")
	public String crearTerapeuta(@Valid Terapeuta terapeuta, BindingResult result, Model model,
			@RequestParam(name="paciente_id[]", required = false)Long[] pacienteId, @RequestParam("file") MultipartFile foto, RedirectAttributes flash, SessionStatus status) {
//		if (result.hasErrors()) {
//			model.addAttribute("titulo", "Crear terapeuta");
//			return "crearTerapeuta";
//		}
		
		
		if(foto.isEmpty()) {
			model.addAttribute("titulo", "Crear Terapeuta");
			model.addAttribute("error", "Error: El perfil de la terapeuta no puede no tener foto.");
			return "crearTerapeuta";
		}
		
		if(!foto.isEmpty()) {
			if(terapeuta.getId()!=null && terapeuta.getId() > 0 && terapeuta.getFoto() != null
					&& terapeuta.getFoto().length() >0) {
				uploadFileService.delete(terapeuta.getFoto());
			}
			String uniqueFilename = null;
			try {
				uniqueFilename = uploadFileService.copy(foto);
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("la foto llego aca!");
			flash.addAttribute("info", "Se ha subido correctamente la foto de perfil de "+terapeuta.getApellido()+".");
			terapeuta.setFoto(uniqueFilename);
		}
		
		flash.addFlashAttribute("success", "Terapeuta creada con éxito.");
		terapeutaService.save(terapeuta);
		status.setComplete();
		return "redirect:/terapeuta/listar";
	}
	
	@Secured("ROLE_USER")
	@PostMapping(value = "/terapeuta/{id}")
	public String asignaPaciente(@PathVariable(value = "id") Long id, Map<String, Object> model,@RequestParam(name = "paciente_id[]", required = false) Long[] pacienteId,
			RedirectAttributes flash) {
		Terapeuta terapeuta = terapeutaService.findOne(id);
		if (terapeuta == null) {
			flash.addFlashAttribute("error", "El terapeuta no existe en la base de datos");
			return "redirect:/paciente/listar";
		}
		
		for (int i = 0; i < pacienteId.length; i++) {
			Paciente paciente = terapeutaService.findPacienteById(pacienteId[i]);
			terapeuta.addPaciente(paciente);
		}
		
		model.put("terapeuta", terapeuta);
		model.put("titulo", "Detalle terapeuta: " + terapeuta.getUsername());
		return "verPaciente";
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
			Terapeuta terapeuta = terapeutaService.findOne(id);
			terapeutaService.darDeBaja(terapeuta);
			flash.addFlashAttribute("success", "Terapeuta: "+terapeuta.getApellido()+" dada de baja.");
			
		}
		return "redirect:/terapeuta/listar";
	}
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/eliminar")
	public String eliminar(RedirectAttributes flash) {

		terapeutaService.deleteAll();
		flash.addFlashAttribute("success", "Todas las terapeutas fueron eliminadas con éxito");

		return "redirect:/receta/listar";
	}
	
	
}
