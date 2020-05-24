package com.fabrizio.consultorio.app.models.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadFileServiceImpl implements IUploadFileService {
	private final Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public Resource load(String filename) throws MalformedURLException {
		Resource recurso = null;
		if(filename.endsWith("pdf")) {
			Path pathPdf = pdfPath(filename);
			recurso = new UrlResource(pathPdf.toUri());
			if (!recurso.exists() || !recurso.isReadable()) {
				throw new RuntimeException("Error: no se puede cargar el pdf: " + pathPdf.toString());
			}
		} 
		else {
			Path pathFoto = getPath(filename);
			recurso = new UrlResource(pathFoto.toUri());
			if (!recurso.exists() || !recurso.isReadable()) {
				throw new RuntimeException("Error: no se puede cargar la imagen: " + pathFoto.toString());
			}
		}
		
		return recurso;
	}

	@Override
	public String copy(MultipartFile file) throws IOException {
		String uniqueFilename = UUID.randomUUID().toString() + "___" + file.getOriginalFilename();
		if (file.getContentType().endsWith("pdf")) {
			Path rutaPdf = pdfPath(uniqueFilename);
			log.info("rootPath: " + rutaPdf);
			Files.copy(file.getInputStream(), rutaPdf);
		} else {
			throw new IOException();
			
//			Path rootPath = getPath(uniqueFilename);
//			
//			log.info("rootPath: " + rootPath);
//
//			Files.copy(file.getInputStream(), rootPath);
		}
	

		return uniqueFilename;
	}

	@Override
	public boolean delete(String filename) {
		Path rootPath = getPath(filename);
		File archivo = rootPath.toFile();
		if (archivo.exists() && archivo.canRead()) {
			if (archivo.delete()) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public Path pdfPath(String filename){
		return Paths.get("archivos").resolve(filename).toAbsolutePath();
	}
	
	@Override
	public Path getPath(String filename) {
		return Paths.get("uploads").resolve(filename).toAbsolutePath();
	}

	@Override
	public void deleteAll() {
		FileSystemUtils.deleteRecursively(Paths.get("uploads").toFile());		
	}

	@Override
	public void init() throws IOException {
		Files.createDirectory(Paths.get("uploads"));
		
	}

}
