package com.fabrizio.consultorio.app.models.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface IUploadFileService {

	public Resource load(String filename) throws MalformedURLException ;
	
	public String copy(MultipartFile file) throws IOException;
	
	public boolean delete(String filename);
	
//	para poder borrar e inicializar las fotos cada vez que se levanta la aplicacion
	public void deleteAll();
	
	public void init() throws IOException;
	
	public Path pdfPath(String filename);
	
	public Path getPath(String filename);
	

}
