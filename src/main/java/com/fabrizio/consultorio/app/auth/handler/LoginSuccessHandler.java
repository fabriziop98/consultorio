package com.fabrizio.consultorio.app.auth.handler;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.SessionFlashMapManager;

@Component
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {


//	toda este componente es para manejar el post login
	
	@Autowired
	private LocaleResolver localeResolver;
	
	@SuppressWarnings("unused")
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		SessionFlashMapManager flashMapManager = new SessionFlashMapManager();

		FlashMap flashMap = new FlashMap();
		
//		forma para cuando el mensaje tiene una variable de objeto que hay que mostrar
		Locale locale = localeResolver.resolveLocale(request);

		flashMap.put("success", "Hola "+authentication.getName() +", ¡Te damos la bienvenida!");

		flashMapManager.saveOutputFlashMap(flashMap, request, response);

		if (authentication != null) {
			logger.info("El usuario '" + authentication.getName() + "' ha iniciado sesión con éxito");
		}

		super.onAuthenticationSuccess(request, response, authentication);
	}
}
