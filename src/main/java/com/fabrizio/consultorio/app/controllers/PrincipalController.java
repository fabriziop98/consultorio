package com.fabrizio.consultorio.app.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value= {"","/inicio"})
public class PrincipalController {

	@GetMapping("/")
	public String inicio() {
		return "inicio";
	}
	
}
