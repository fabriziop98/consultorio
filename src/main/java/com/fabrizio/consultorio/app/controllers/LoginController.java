package com.fabrizio.consultorio.app.controllers;

import java.security.Principal;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

	@SuppressWarnings("unused")
	@Autowired
	private MessageSource messageSource;

	@GetMapping("/login")
	public String login(@RequestParam(value = "error", required = false) String error,
			@RequestParam(value = "logout", required = false) String logout, Model model, Principal principal,
			RedirectAttributes flash, Locale locale) {

//		si esto se cumple es porque ya habia iniciado sesion anteriormente

		if (principal != null) {
			flash.addFlashAttribute("info", "Ya haz iniciado sesión anteriormente");
			return "redirect:/inicio/";
		}
		if (error != null) {
			model.addAttribute("error","Error: El usuario o contraseña ingresados son incorrectos.");
		}
		if (logout != null) {
			model.addAttribute("success", "Has cerrado sesión con éxito!");
		}
		return "/inicio";
	}
}
