package com.fabrizio.consultorio.app.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/error")
public class ErroresController implements ErrorController {

	@GetMapping()
	public ModelAndView renderErrorPage(HttpServletRequest httpRequest) {

		ModelAndView mv = new ModelAndView("error");
		String errorMsg = "";
		int httpErrorCode = getErrorCode(httpRequest);

		switch (httpErrorCode) {
		case 400:
			mv.addObject("tituloError", "Error 400");
			mv.addObject("error", "La petición es incorrecta");
			mv.addObject("detalleError", "Es todo lo que sabemos.");
			break;
		case 401:
			mv.addObject("tituloError", "Error 401");
			mv.addObject("error", "No tienes los permisos para entrar a esta página");
			mv.addObject("detalleError", "Intenta ingresando con un usuario que tenga los permisos correspondientes.");
			break;
		case 403:
			mv.addObject("tituloError", "Error 403");
			mv.addObject("error", "No tienes los permisos para entrar a esta página");
			mv.addObject("detalleError", "Intenta ingresando con un usuario que tenga los permisos correspondientes.");
			break;
		case 404:
			mv.addObject("tituloError", "Error 404");
			mv.addObject("error", "Página no encontrada");
			mv.addObject("detalleError", "Prueba ingresando otra url o vuelve al principio.");
			break;
		case 500:
			mv.addObject("tituloError", "Error 500");
			mv.addObject("error", "Error en el servidor");
			mv.addObject("detalleError", "Estamos trabajando para solucionar este problema.");
			break;
		default:
			mv.addObject("tituloError", "Error");
			mv.addObject("error", "Ocurrió un error");
			mv.addObject("detalleError", "Esto es algo inesperado.");
			break;
		}

		mv.addObject("codigo", httpErrorCode);
		mv.addObject("mensaje", errorMsg);
		return mv;
	}


	private int getErrorCode(HttpServletRequest httpRequest) {
		Integer codigo = (Integer) httpRequest.getAttribute("javax.servlet.error.status_code");
		if (codigo != null) {
			return codigo;
		}

		return 0;
	}

	@Override
	public String getErrorPath() {
		return "/errores/error_404.html";
	}
}