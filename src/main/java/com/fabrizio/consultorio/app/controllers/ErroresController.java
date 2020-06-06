package com.fabrizio.consultorio.app.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import static com.fabrizio.util.Texto.ERROR_LABEL;
import static com.fabrizio.util.Texto.TITULOERROR_LABEL;
import static com.fabrizio.util.Texto.DETALLEERROR_LABEL;

@Controller
@RequestMapping("/error")
public class ErroresController implements ErrorController {

	@GetMapping()
	public ModelAndView renderErrorPage(HttpServletRequest httpRequest) {

		ModelAndView mv = new ModelAndView(ERROR_LABEL);
//		String errorMsg = "";
		int httpErrorCode = getErrorCode(httpRequest);

		switch (httpErrorCode) {
		case 400:
			mv.addObject(TITULOERROR_LABEL, "Error 400");
			mv.addObject(ERROR_LABEL, "La petición es incorrecta");
			mv.addObject(DETALLEERROR_LABEL, "Es todo lo que sabemos.");
			break;
		case 401:
			mv.addObject(TITULOERROR_LABEL, "Error 401");
			mv.addObject(ERROR_LABEL, "No tienes los permisos para entrar a esta página");
			mv.addObject(DETALLEERROR_LABEL, "Intenta ingresando con un usuario que tenga los permisos correspondientes.");
			break;
		case 403:
			mv.addObject(TITULOERROR_LABEL, "Error 403");
			mv.addObject(ERROR_LABEL, "No tienes los permisos para entrar a esta página");
			mv.addObject(DETALLEERROR_LABEL, "Intenta ingresando con un usuario que tenga los permisos correspondientes.");
			break;
		case 404:
			mv.addObject(TITULOERROR_LABEL, "Error 404");
			mv.addObject(ERROR_LABEL, "Página no encontrada");
			mv.addObject(DETALLEERROR_LABEL, "Prueba ingresando otra url o vuelve al principio.");
			break;
		case 500:
			mv.addObject(TITULOERROR_LABEL, "Error 500");
			mv.addObject(ERROR_LABEL, "Error en el servidor");
			mv.addObject(DETALLEERROR_LABEL, "Estamos trabajando para solucionar este problema.");
			break;
		default:
			mv.addObject(TITULOERROR_LABEL, "Error");
			mv.addObject(ERROR_LABEL, "Ocurrió un error");
			mv.addObject(DETALLEERROR_LABEL, "Esto es algo inesperado.");
			break;
		}

//		mv.addObject("codigo", httpErrorCode);
//		mv.addObject("mensaje", errorMsg);
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