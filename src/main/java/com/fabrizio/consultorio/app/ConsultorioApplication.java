package com.fabrizio.consultorio.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.fabrizio.consultorio.app.models.service.IUploadFileService;

@SpringBootApplication
public class ConsultorioApplication {
	
	@Autowired
	IUploadFileService uploadFileService;
	
	@SuppressWarnings("unused")
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(ConsultorioApplication.class, args);
	}

}
