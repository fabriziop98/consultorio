package com.fabrizio.consultorio.app.controllers;

import java.io.File;
import java.io.FileInputStream;
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
	
	@PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR')")
	@GetMapping("/")
	public String displayCrearUsuario(Model model) {
		model.addAttribute("usuario", new Usuario());
		return "crearUsuario";
	}
	
	
	@PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR')")
	@PostMapping("/crear")
	public String crearUsuario(@Valid Usuario usuario, BindingResult result, Model model,
			@RequestParam(required = false, value = "file") String foto, RedirectAttributes flash, SessionStatus status) {
		usuario.setFoto(foto);
		try {
			usuarioService.save(usuario);
		} catch (Exception e) {
			e.printStackTrace();
			flash.addFlashAttribute("error", "Ocurrió un error al intentar crear el usuario.");
			return "redirect:/usuario/";
		}
		status.setComplete();
		flash.addFlashAttribute("success", "Usuario "+usuario.getUsername()+" "+usuario.getApellido()+" creado con éxito.");
		return "redirect:/usuario/listar";
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR')")
	@GetMapping("/editar/{id}")
	public String displayEditarUsuario(@PathVariable Long id, Model model) {
		Usuario usuario = usuarioService.findOne(id);
		model.addAttribute("usuario", usuario);
		return "crearUsuario";
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR')")
	@PostMapping("/editar/{id}")
	public String editarUsuario(@Valid Usuario usuario, @RequestParam(required = false, value = "file") String foto, RedirectAttributes flash) {
		try {
			usuario.setFoto(foto);
			usuarioService.editar(usuario);
		} catch (Exception e) {
			e.printStackTrace();
			flash.addFlashAttribute("error", "Ocurrió un error al intentar editar el usuario.");
			return "redirect:/usuario/listar";
		}
		flash.addFlashAttribute("success", "Usuario editado con éxito.");
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
	
	
	
	@GetMapping("/foto/{id}")
	public ResponseEntity<byte[]> abrirFoto(@PathVariable(name = "id") Long id) {
		final HttpHeaders headers = new HttpHeaders();

		Usuario usuario = usuarioService.findOne(id);

		if (usuario.getFoto() != null) {

			String path = usuario.getFoto();

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








