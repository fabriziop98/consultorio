package com.fabrizio.consultorio.app;

import java.nio.file.Paths;
import java.util.Locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@Configuration
public class MvcConfig implements WebMvcConfigurer  {
	
	
//	para crear in controlador que su unica funcion va a ser cargar la vista de perimiso denegadoi
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/error_403").setViewName("/error");
	}
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	
//	resuelve el locale, donde se va a guardar el parametro de nuestro lenguaje, se va a guardar en nuestra sesion
	@Bean
	public LocaleResolver localeResolver() {
		SessionLocaleResolver localeResolver = new SessionLocaleResolver();
		localeResolver.setDefaultLocale(new Locale("es","ES"));
		return localeResolver;
	}
	
//	interceptor se encarga de modificar el lenguaje cada vez que pasemos el parametro lang por url
//	TODAVIA NO CONFIGURADO
	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor localeInterceptor = new LocaleChangeInterceptor();
		localeInterceptor.setParamName("lang");
		return localeInterceptor;
	}
	
//	para registrar el interceptor
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(localeChangeInterceptor());
	}
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		
		
		WebMvcConfigurer.super.addResourceHandlers(registry);

		String resourcePath = Paths.get("archivos").toAbsolutePath().toUri().toString();
		registry.addResourceHandler("/archivos/**")
		.addResourceLocations(resourcePath);
		
	}


}
