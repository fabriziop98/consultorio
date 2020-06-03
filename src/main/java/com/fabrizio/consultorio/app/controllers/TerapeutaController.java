package com.fabrizio.consultorio.app.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

import static com.fabrizio.util.Texto.TITULO_LABEL;
import static com.fabrizio.util.Texto.ERROR_LABEL;
import static com.fabrizio.util.Texto.SUCCESS_LABEL;
import static com.fabrizio.util.Texto.TERAPEUTA_LABEL;
import static com.fabrizio.util.Texto.TERAPEUTAS_LABEL;

@Controller
@RequestMapping("/terapeuta")
public class TerapeutaController {

	@Autowired
	private ITerapeutaService terapeutaService;
	
	@Autowired
	private IUploadFileService uploadFileService;
	
	@PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR')")
	@GetMapping("/listar")
	public String listarTerapeutas(Model model, Authentication authentication, HttpServletRequest request, Locale locale) {
		
		List<Terapeuta> terapeutas = terapeutaService.findAll();
		
		model.addAttribute(TITULO_LABEL, "Terapeutas");
		model.addAttribute(TERAPEUTAS_LABEL, terapeutas);
		return TERAPEUTAS_LABEL;
		
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR')")
	@GetMapping("/")
	public String displayCrearTerapeuta(Model model) {
		model.addAttribute(TERAPEUTA_LABEL, new Terapeuta());
		return "crearTerapeuta";
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR')")
	@PostMapping("/crear")
	public String crearTerapeuta(@Valid Terapeuta terapeuta, BindingResult result, Model model,
			@RequestParam(name="paciente_id[]", required = false)Long[] pacienteId, @RequestParam("file") MultipartFile foto, RedirectAttributes flash, SessionStatus status) {
//		if (result.hasErrors()) {
//			model.addAttribute(TITULO_LABEL, "Crear terapeuta");
//			return "crearTerapeuta";
//		}
		
		
		if(foto.isEmpty()) {
			model.addAttribute(TITULO_LABEL, "Crear Terapeuta");
			model.addAttribute(ERROR_LABEL, "Error: El perfil de la terapeuta no puede no tener foto.");
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
		
		flash.addFlashAttribute(SUCCESS_LABEL, "Terapeuta creada con éxito.");
		terapeutaService.save(terapeuta);
		status.setComplete();
		return "redirect:/terapeuta/listar";
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR')")
	@PostMapping(value = "/terapeuta/{id}")
	public String asignaPaciente(@PathVariable(value = "id") Long id, Map<String, Object> model,@RequestParam(name = "paciente_id[]", required = false) Long[] pacienteId,
			RedirectAttributes flash) {
		Terapeuta terapeuta = terapeutaService.findOne(id);
		if (terapeuta == null) {
			flash.addFlashAttribute(ERROR_LABEL, "El terapeuta no existe en la base de datos");
			return "redirect:/paciente/listar";
		}
		
		for (int i = 0; i < pacienteId.length; i++) {
			Paciente paciente = terapeutaService.findPacienteById(pacienteId[i]);
			terapeuta.addPaciente(paciente);
		}
		
		model.put(TERAPEUTA_LABEL, terapeuta);
		model.put(TITULO_LABEL, "Detalle terapeuta: " + terapeuta.getUsername());
		return "verPaciente";
	}
	
	
	@PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR')")
	@RequestMapping(value = "/eliminar/{id}")
	public String eliminar(@PathVariable(value = "id") Long id, RedirectAttributes flash) {
		if (id>0){
			Terapeuta terapeuta = terapeutaService.findOne(id);
			terapeutaService.darDeBaja(terapeuta);
			flash.addFlashAttribute(SUCCESS_LABEL, "Terapeuta: "+terapeuta.getApellido()+" dada de baja.");
			
		}
		return "redirect:/terapeuta/listar";
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR')")
	@RequestMapping(value = "/eliminar")
	public String eliminar(RedirectAttributes flash) {

		terapeutaService.deleteAll();
		flash.addFlashAttribute(SUCCESS_LABEL, "Todas las terapeutas fueron eliminadas con éxito");

		return "redirect:/receta/listar";
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR','ROLE_PACIENTE','ROLE_TERAPEUTA')")
	@GetMapping("/foto/{id}")
	public ResponseEntity<byte[]> abrirFoto(@PathVariable(name = "id") Long id) {
		final HttpHeaders headers = new HttpHeaders();

		Terapeuta terapeuta = terapeutaService.findOne(id);

		if (terapeuta.getFoto() != null) {

			String path = terapeuta.getFoto();

			File file = new File(path);
			String extension = getFileExtension(file);

			if (extension.equals(".jpeg")) {
				MediaType media = MediaType.parseMediaType("image/jpeg");
				headers.setContentType(media);
			} else if (extension.equals(".png")) {
				headers.setContentType(MediaType.IMAGE_PNG);
			} else {
				headers.setContentType(MediaType.IMAGE_JPEG);
			}

			return new ResponseEntity<>(readFileToByteArray(file), headers, HttpStatus.OK);
		} else {
			return null;
		}

	}

	private String getFileExtension(File file) {
		String name = file.getName();
		int lastIndexOf = name.lastIndexOf(".");
		if (lastIndexOf == -1) {
			return "";
		}
		return name.substring(lastIndexOf);
	}
	
	private static byte[] readFileToByteArray(File file) {
		FileInputStream fis;
		byte[] bArray = new byte[(int) file.length()];
		try {
			fis = new FileInputStream(file);
			fis.read(bArray);
			fis.close();

		} catch (IOException ioExp) {
			ioExp.printStackTrace();
		}
		return bArray;
	}
	
}
