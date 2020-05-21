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

import com.fabrizio.consultorio.app.models.entity.Usuario;
import com.fabrizio.consultorio.app.models.service.IUsuarioService;
import com.fabrizio.consultorio.app.models.service.IUploadFileService;

@Controller
@RequestMapping("/usuario")

public class UsuarioController {

	@Autowired
	private IUsuarioService usuarioService;
	@Autowired
	private IUploadFileService uploadFileService;
	
	@PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR')")
	@GetMapping("/listar")
	public String listarUsuarios(Model model, Authentication authentication, HttpServletRequest request, Locale locale) {
		
		List<Usuario> usuarios = usuarioService.findAll();
		
		model.addAttribute("titulo", "Usuarios");
		model.addAttribute("usuarios", usuarios);
		return "usuarios";
			
	}
	
//	@PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR')")
	@GetMapping("/")
	public String displayCrearUsuario(Model model) {
		model.addAttribute("usuario", new Usuario());
		return "crearUsuario";
	}
	
	
//	@PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR')")
	@PostMapping("/crear")
	public String crearUsuario(@Valid Usuario usuario, BindingResult result, Model model,
			@RequestParam("file") MultipartFile foto, RedirectAttributes flash, SessionStatus status) {
//		if (result.hasErrors()) {
//			model.addAttribute("titulo", "Crear usuario");
//			return "crearUsuario";
//		}
		
		
		if(foto.isEmpty()) {
			usuario.setFoto("usuario.png");
		}
		
		if(!foto.isEmpty()) {
			if(usuario.getId()!=null && usuario.getId() > 0 && usuario.getFoto() != null
					&& usuario.getFoto().length() >0) {
				uploadFileService.delete(usuario.getFoto());
			}
			String uniqueFilename = null;
			try {
				uniqueFilename = uploadFileService.copy(foto);
			} catch (IOException e) {
				e.printStackTrace();
			}
			flash.addAttribute("info", "Se ha subido correctamente la foto de perfil de "+usuario.getUsername()+".");
			usuario.setFoto(uniqueFilename);
		}
		
		flash.addFlashAttribute("success", "Usuario creado con éxito.");
		try {
			usuarioService.save(usuario);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		status.setComplete();
		return "redirect:/usuario/listar";
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR','ROLE_TERAPEUTA','ROLE_PACIENTE')")
	@GetMapping(value = "/ver/{id}")
	public String ver(@PathVariable(value = "id") Long id,  Map<String, Object> model, RedirectAttributes flash) {
		Usuario usuario = usuarioService.findOne(id);
		if (usuario == null) {
			flash.addFlashAttribute("error", "El cliente no existe en la base de datos");
			return "redirect:/usuario/listar";
		}
		model.put("usuario", usuario);
		model.put("titulo", "Detalle usuario: " + usuario.getUsername() +" "+ usuario.getApellido());
//		if(usuario.getRol().equals(Rol.PACIENTE)) {
//			model.put("nombreTerapeuta", usuario.getTerapeutas().toString().replace("[", "").replace("]", ""));
//			model.put("turnos", turnoService.listarSorted(turnoService.listarFuturos(usuario)));
//		}
		
		
		return "verUsuario";
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR')")
	@RequestMapping(value = "/eliminar/{id}")
	public String eliminar(@PathVariable(value = "id") Long id, RedirectAttributes flash) {
		if (id>0){
			Usuario usuario = usuarioService.findOne(id);
			usuarioService.darDeBaja(usuario);
			flash.addFlashAttribute("success", "Terapeuta: "+usuario.getApellido()+" dado de baja.");
			
		}
		return "redirect:/terapeuta/listar";
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR')")
	@RequestMapping(value = "/eliminar")
	public String eliminar(RedirectAttributes flash) {

		usuarioService.deleteAll();
		flash.addFlashAttribute("success", "Todas las terapeutas fueron eliminadas con éxito");

		return "redirect:/receta/listar";
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR','ROLE_PACIENTE','ROLE_TERAPEUTA')")
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
	
	
}
