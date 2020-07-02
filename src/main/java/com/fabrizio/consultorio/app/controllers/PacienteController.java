package com.fabrizio.consultorio.app.controllers;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
import com.fabrizio.consultorio.app.models.entity.Pdf;
import com.fabrizio.consultorio.app.models.entity.Terapeuta;
import com.fabrizio.consultorio.app.models.entity.Turno;
import com.fabrizio.consultorio.app.models.entity.Usuario;
import com.fabrizio.consultorio.app.models.service.IPacienteService;
import com.fabrizio.consultorio.app.models.service.IPdfService;
import com.fabrizio.consultorio.app.models.service.ITerapeutaService;
import com.fabrizio.consultorio.app.models.service.ITurnoService;
import com.fabrizio.consultorio.app.models.service.IUploadFileService;
import com.fabrizio.consultorio.app.models.service.IUsuarioService;

import static com.fabrizio.util.Texto.TITULO_LABEL;
import static com.fabrizio.util.Texto.ERROR_LABEL;
import static com.fabrizio.util.Texto.SUCCESS_LABEL;
import static com.fabrizio.util.Texto.TURNO_LABEL;
import static com.fabrizio.util.Texto.TURNOS_LABEL;
import static com.fabrizio.util.Texto.PACIENTE_LABEL;
import static com.fabrizio.util.Texto.USUARIOID_LABEL;
import static com.fabrizio.util.Texto.PACIENTES_LABEL;
import static com.fabrizio.util.Texto.INFO_LABEL;
import static com.fabrizio.util.Texto.TERAPEUTAS_LABEL;

@Controller
@RequestMapping("/paciente")
public class PacienteController {
	

	@Autowired
	private IPacienteService pacienteService;
	
	@Autowired
	private IPdfService pdfService;
	
	@Autowired
	private ITerapeutaService terapeutaService;
	
	@Autowired
	private IUploadFileService uploadFileService;
	
	@Autowired
	private ITurnoService turnoService;
	
	@Autowired
	private IUsuarioService usuarioService;
	
	@PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR','ROLE_TERAPEUTA')")
	@GetMapping("/listar")
	public String listarPacientes(Model model, Authentication authentication, HttpServletRequest request, Locale locale) {
		
		List<Paciente> pacientes = pacienteService.findAll();
		
		model.addAttribute(TITULO_LABEL, "Pacientes");
		model.addAttribute(PACIENTES_LABEL, pacientes);
		return PACIENTES_LABEL;
		
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR')")
	@GetMapping("/")
	public String displayCrearPaciente(Model model) {
		model.addAttribute(PACIENTE_LABEL, new Paciente());
		return "crearPaciente";
	}
	
	
	@PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR')")
	@PostMapping("/crear")
	public String crearPaciente(@Valid Paciente paciente, BindingResult result, Model model,
			@RequestParam("file") MultipartFile foto, RedirectAttributes flash, SessionStatus status) {
		
		if(foto.isEmpty()) {
			model.addAttribute(TITULO_LABEL, "Crear Paciente");
			model.addAttribute(ERROR_LABEL, "Error: El perfil del paciente no puede no tener foto.");
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
			flash.addAttribute(INFO_LABEL, "Se ha subido correctamente la foto de perfil de "+paciente.getApellido()+".");
			paciente.setFoto(uniqueFilename);
		}
		
		flash.addFlashAttribute(SUCCESS_LABEL, "Paciente creado con éxito.");
		pacienteService.save(paciente);
		status.setComplete();
		return "redirect:/paciente/listar";
	}
	
	
	@PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR','ROLE_TERAPEUTA')")
	@PostMapping("/turno/{id}")
	public String asignarTurno(@Valid Turno turno, @PathVariable(value="id") Long idPaciente, Model model,
			@RequestParam(name = TURNOS_LABEL, required = false) String turnoString, 
			RedirectAttributes flash, SessionStatus status) {
		try {
			turnoService.crear(turno, idPaciente, turnoString);
		} catch (Exception e) {
			model.addAttribute(TITULO_LABEL, "Asignar turno");
			model.addAttribute(ERROR_LABEL, "Error: Ha ocurrido un error al crear el turno");
			flash.addFlashAttribute(ERROR_LABEL, "Error: Ha ocurrido un error al crear el turno");
			e.printStackTrace();
			return "redirect:/paciente/ver/{id}";
		}
		flash.addFlashAttribute(SUCCESS_LABEL, "Turno asignado: "+turnoString+", con terapeuta: "+turno.getTerapeuta().getUsername()+" "+turno.getTerapeuta().getApellido());
		status.setComplete();
		return "redirect:/paciente/ver/{id}";
	}
	
	
	@PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR')")
	@PostMapping("/form/{id}")
	public String asignarTerapeuta(@PathVariable(value = "id") Long id, Paciente pacienteModelo, Model model,
			RedirectAttributes flash, SessionStatus status) {
		
		model.addAttribute(PACIENTES_LABEL, pacienteModelo);
		
		if (pacienteModelo.getTerapeutas() == null || pacienteModelo.getTerapeutas().isEmpty()) {
			model.addAttribute(TITULO_LABEL, "Asignar terapeuta");
//			el error se conecta al th:errorclass
			flash.addFlashAttribute(ERROR_LABEL, "Error: El terapeuta es nulo");
			return "asignarTerapeuta";
		}
		
		try {
			pacienteService.asignarTerapeuta(pacienteModelo, id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		flash.addFlashAttribute(SUCCESS_LABEL, "Terapeuta asignado con éxito");
		
		
		
		return "redirect:/paciente/ver/{id}";
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR','ROLE_TERAPEUTA')")
	@GetMapping("/listar/{id}")
	public String verPacienteTerapeuta(@PathVariable(value="id") Long id, Map<String, Object> model, Model modelo, RedirectAttributes flash) {
		List<Paciente> pacientes = pacienteService.findByTerapeutaId(id);
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UserDetails) {
			  String username = ((UserDetails)principal).getUsername();
			  Usuario usuario = usuarioService.findByMail(username);
			  switch(usuario.getRol()) {
			  case TERAPEUTA:
				  model.put(USUARIOID_LABEL, terapeutaService.byUsuarioId(usuario.getId()).getId());
				  break;
			case ADMINISTRADOR:
				break;
			default:
				break;
			  }
			 
			} 
		model.put(TITULO_LABEL, "Pacientes");
		model.put(PACIENTES_LABEL, pacientes);
		modelo.getAttribute("dateTimePicker");
		return PACIENTES_LABEL;
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR','ROLE_PACIENTE','ROLE_TERAPEUTA')")
	@GetMapping(value = "/ver/{id}")
	public String ver(@PathVariable(value = "id") Long id,  Map<String, Object> model, RedirectAttributes flash) {
		Paciente paciente = pacienteService.findOne(id);
		if (paciente == null) {
			flash.addFlashAttribute(ERROR_LABEL, "El cliente no existe en la base de datos");
			return "redirect:/paciente/listar";
		}
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UserDetails) {
		  String username = ((UserDetails)principal).getUsername();
		  Usuario usuario = usuarioService.findByMail(username);
		  switch(usuario.getRol()) {
		  case TERAPEUTA:
			  model.put(USUARIOID_LABEL, terapeutaService.byUsuarioId(usuario.getId()).getId());
//			  log.info("SESION: usuario terapeuta: " + username);
			  break;
		  case PACIENTE:
			  model.put(USUARIOID_LABEL, pacienteService.byUsuarioId(usuario.getId()).getId());
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
		Turno turno = new Turno();
		List<Terapeuta> terapeutas = paciente.getTerapeutas();
		List<Terapeuta> terapeutasDisponibles = terapeutaService.listarDisponibles(paciente);
		model.put(TURNO_LABEL, turno);
		model.put(PACIENTE_LABEL, paciente);
		model.put(TERAPEUTAS_LABEL, terapeutas);
		model.put("terapeutasDisponibles", terapeutasDisponibles);
		model.put(TITULO_LABEL, "Detalle paciente: " + paciente.getUsername() +" "+ paciente.getApellido());
		model.put("nombreTerapeuta", paciente.getTerapeutas().toString().replace("[", "").replace("]", ""));
		model.put(TURNOS_LABEL, turnoService.listarSorted(turnoService.listarFuturos(paciente)));
		return "verPaciente";
	}
	
	
	@PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR','ROLE_PACIENTE','ROLE_TERAPEUTA')")
	@GetMapping(value = "/uploads/{filename:.+}")
	public ResponseEntity<Resource> verFoto(@PathVariable String filename) {
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
	
	
	@PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR','ROLE_TERAPEUTA','ROLE_PACIENTE','ROLE_USUARIO')")
	@GetMapping("/pdf/{id}")
	public String displayPdf(@PathVariable Long id, Model model) {
		Paciente paciente = pacienteService.findOne(id);
		model.addAttribute(PACIENTE_LABEL, paciente);
		return "subirPdf";
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR','ROLE_TERAPEUTA')")
	@PostMapping("/pdf/{id}")
	public String subirPdf(@PathVariable Long id, Model model, @RequestParam("file") MultipartFile archivo, RedirectAttributes flash, SessionStatus status) {
		Paciente paciente = pacienteService.findOne(id);
		Pdf pdf = new Pdf();

		if(archivo.isEmpty()) {
			model.addAttribute(TITULO_LABEL, "Subir informe");
			model.addAttribute(ERROR_LABEL, "Error: El informe no puede ser nulo.");
			return "subirPdf";
		}
		
		if(!archivo.isEmpty()) {
			String uniqueFilename = null;
			
			
			try {
				
				uniqueFilename = uploadFileService.copy(archivo);
			} catch (IOException e) {
				flash.addFlashAttribute(ERROR_LABEL, "El archivo que intentó subir no es un formato permitido (PDF/DOC.");
				e.printStackTrace();
				return "redirect:/paciente/pdf/{id}";
			}
			flash.addFlashAttribute(INFO_LABEL, "Se ha subido correctamente el pdf: "+archivo.getOriginalFilename());
			pdf.setNombre(uniqueFilename);
			paciente.addPdf(pdf);
			pdf.setFechaSubida(new Date());
			pdf.setEliminado(false);
		}
		pacienteService.save(paciente);
		status.setComplete();
		
		return "redirect:/paciente/ver/{id}";
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR','ROLE_PACIENTE','ROLE_TERAPEUTA')")
	@GetMapping("/listadoPdf/{id}")
	public String verPdfPaciente(@PathVariable Long id, Model model) {
		Paciente paciente = pacienteService.findOne(id);
		model.addAttribute(PACIENTE_LABEL, paciente);
		return "verPdf";
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR')")
	@RequestMapping(value = "/pdf/eliminar/{idPdf}/{idPaciente}")
	public String eliminarPdf(@PathVariable(value = "idPdf") Long id,@PathVariable(value = "idPaciente") Long idPaciente,  RedirectAttributes flash, Model model) {
		if (id>0){
			Pdf pdf = pdfService.findOne(id);
			pdfService.darDeBaja(pdf);
			flash.addFlashAttribute(SUCCESS_LABEL, "Informe: "+(pdf.getNombre()).substring(pdf.getNombre().lastIndexOf("_") + 1)+" eliminado.");
			System.out.println(model.getAttribute(PACIENTE_LABEL));
		}
		return "redirect:/paciente/listadoPdf/"+idPaciente;
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR','ROLE_PACIENTE','ROLE_TERAPEUTA')")
	@GetMapping(value = "/listado/pdf/{filename:.+}")
	public ResponseEntity<Resource> verPdf(@PathVariable String filename) {
		Resource recurso = null;
		try {
			recurso = uploadFileService.load(filename);
		} 
		catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return ResponseEntity.ok()
				.body(recurso);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR','ROLE_PACIENTE','ROLE_TERAPEUTA')")
	@GetMapping("/foto/{id}")
	public ResponseEntity<byte[]> abrirFoto(@PathVariable(name = "id") Long id) {
		final HttpHeaders headers = new HttpHeaders();

		Paciente paciente = pacienteService.findOne(id);

		if (paciente.getFoto() != null) {

			String path = paciente.getFoto();

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
	
	
	@PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR')")
	@RequestMapping(value = "/eliminar")
	public String eliminar(RedirectAttributes flash) {

		pacienteService.deleteAll();
		flash.addFlashAttribute(SUCCESS_LABEL, "Todas las terapeutas fueron eliminadas con éxito");

		return "redirect:/receta/listar";
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR')")
	@RequestMapping(value = "/eliminar/{id}")
	public String eliminar(@PathVariable(value = "id") Long id, RedirectAttributes flash) {
		if (id>0){
			Paciente paciente = pacienteService.findOne(id);
			pacienteService.darDeBaja(paciente);
			flash.addFlashAttribute(SUCCESS_LABEL, "Terapeuta: "+paciente.getApellido()+" dado de baja.");
			
		}
		return "redirect:/terapeuta/listar";
	}
	
}
