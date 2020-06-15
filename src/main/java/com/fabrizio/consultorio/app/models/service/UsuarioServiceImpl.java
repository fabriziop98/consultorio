package com.fabrizio.consultorio.app.models.service;


import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fabrizio.consultorio.app.models.dao.IPacienteDao;
import com.fabrizio.consultorio.app.models.dao.ITerapeutaDao;
import com.fabrizio.consultorio.app.models.dao.ITurnoDao;
import com.fabrizio.consultorio.app.models.dao.IUsuarioDao;
import com.fabrizio.consultorio.app.models.entity.Paciente;
import com.fabrizio.consultorio.app.models.entity.Rol;
import com.fabrizio.consultorio.app.models.entity.Terapeuta;
import com.fabrizio.consultorio.app.models.entity.Turno;
import com.fabrizio.consultorio.app.models.entity.Usuario;
import com.fabrizio.util.FileUtil;

@Service
public class UsuarioServiceImpl implements IUsuarioService{
	
	@Autowired
	private AmazonUpload amazonUpload;
	
	@Autowired
	private IUsuarioDao usuarioDao;
	
	@Autowired
	private ITurnoDao turnoDao;
	
	@Autowired
	private ITerapeutaDao terapeutaDao;
	
	@Autowired
	private IPacienteDao pacienteDao;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@SuppressWarnings("unused")
	@Autowired(required=false)
	private Usuario usuario;

	@Override
	@Transactional(readOnly = true)
	public List<Usuario> findAll() {
		return usuarioDao.findAll();
	}

	@Override
	@Transactional
	public void save(Usuario usuario) throws Exception {
		if (usuarioDao.porMail(usuario.getMail()) != null) {
			throw new Exception();
		}
		String foto = usuario.getFoto();
		
		String uniqueFilename = UUID.randomUUID().toString();
		if (!foto.isEmpty()) {
			File archivo = new File(uniqueFilename+"_"+ usuario.getUsername());
			String pathFoto = archivo.getPath();
			usuario.setFoto(pathFoto);

			try (FileOutputStream out = new FileOutputStream(archivo)) {
				boolean guardarArchivo = archivo.exists() || archivo.createNewFile();

				if (guardarArchivo) {
					foto = foto.split(",")[1];
					byte[] data = Base64.getDecoder().decode(foto.getBytes(StandardCharsets.UTF_8));
					IOUtils.write(data, out);
				}

			} catch (Exception e) {
				System.out.println("Error al guardar la foto de usuario."+ e);
			}
		} else {
			usuario.setFoto(FileUtil.RUTA_IMAGENES+"fotoDefault");
		}
		
		usuario.setFechaAlta(new Date());
		
		switch(usuario.getRol()) {
		case TERAPEUTA:
			usuario.setRol(Rol.TERAPEUTA);
			Terapeuta terapeuta = new Terapeuta();
			terapeuta.setFechaAlta(usuario.getFechaAlta());
			terapeuta.setUsername(usuario.getUsername());
			terapeuta.setPassword(passwordEncoder.encode(usuario.getPassword()));
			terapeuta.setMail(usuario.getMail());
			terapeuta.setFoto(usuario.getFoto());
			terapeuta.setId(usuario.getId());
			terapeuta.setApellido(usuario.getApellido());
			terapeuta.setUsuario(usuario);
			terapeutaDao.save(terapeuta);
			break;
		case ADMINISTRADOR:
			usuario.setRol(Rol.ADMINISTRADOR);
			break;
		case PACIENTE:
			usuario.setRol(Rol.PACIENTE);
			Paciente paciente = new Paciente();
			paciente.setFechaAlta(usuario.getFechaAlta());
			paciente.setUsername(usuario.getUsername());
			paciente.setPassword(passwordEncoder.encode(usuario.getPassword()));
			paciente.setMail(usuario.getMail());
			paciente.setApellido(usuario.getApellido());
			paciente.setId(usuario.getId());
			paciente.setFoto(usuario.getFoto());
			paciente.setUsuario(usuario);
			pacienteDao.save(paciente);
			break;
		case USUARIO:
			break;
		default:
			usuario.setRol(Rol.USUARIO);
			break;		
		}
		usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
		usuarioDao.save(usuario);
	}
	
	@Override
	public void editar(Usuario usuario) throws Exception {
		
		String foto = usuario.getFoto();
		usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
		Usuario u = usuarioDao.findById((usuario.getId())).orElse(null);
		if(!usuario.getMail().equals(u.getMail())) {
			if (usuarioDao.porMail(usuario.getMail()) != null) {
				throw new Exception();
			}
		}
		
		String uniqueFilename = UUID.randomUUID().toString();
		if (!foto.isEmpty()) {
			File archivo = new File(FileUtil.RUTA_IMAGENES +uniqueFilename+"_"+ usuario.getUsername());
			String pathFoto = archivo.getPath();
			usuario.setFoto(pathFoto);

			try (FileOutputStream out = new FileOutputStream(archivo)) {
				boolean guardarArchivo = archivo.exists() || archivo.createNewFile();

				if (guardarArchivo) {
					foto = foto.split(",")[1];
					byte[] data = Base64.getDecoder().decode(foto.getBytes(StandardCharsets.UTF_8));
					IOUtils.write(data, out);
				}

			} catch (Exception e) {
				System.out.println("Error al guardar la foto de usuario."+ e);
			}
		} else {
			usuario.setFoto(u.getFoto());
		}
		usuario.setRol(u.getRol());
		
		if(u.getRol().equals(Rol.TERAPEUTA)) {
			Terapeuta terapeuta = terapeutaDao.findByUsuarioId(usuario.getId());
			terapeuta.setFechaAlta(usuario.getFechaAlta());
			terapeuta.setUsername(usuario.getUsername());
			terapeuta.setPassword(passwordEncoder.encode(usuario.getPassword()));
			terapeuta.setMail(usuario.getMail());
			terapeuta.setFoto(usuario.getFoto());
			terapeuta.setApellido(usuario.getApellido());
			terapeuta.setUsuario(usuario);
			terapeuta.setPacientes(terapeuta.getPacientes());
			terapeutaDao.save(terapeuta);
		}
		if(u.getRol().equals(Rol.PACIENTE)) {
			Paciente paciente = pacienteDao.findByUsuarioId(usuario.getId());
			paciente.setFechaAlta(usuario.getFechaAlta());
			paciente.setUsername(usuario.getUsername());
			paciente.setPassword(passwordEncoder.encode(usuario.getPassword()));
			paciente.setMail(usuario.getMail());
			paciente.setApellido(usuario.getApellido());
			paciente.setFoto(usuario.getFoto());
			paciente.setUsuario(usuario);
			paciente.setTerapeutas(paciente.getTerapeutas());
			paciente.setTurnos(paciente.getTurnos());
			paciente.setPdf(paciente.getPdf());
			pacienteDao.save(paciente);
		}
		
		u = usuario;
		usuarioDao.save(u);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Usuario> findByNombre(String term) {
		return usuarioDao.findByUsernameLikeIgnoreCase(term);
	}

	@Override
	public void detele(Usuario usuario) {
		usuarioDao.delete(usuario);
	}

	@Override
	@Transactional
	public void darDeBaja(Usuario usuario) {
		usuario.setFechaBaja(new Date());
		Terapeuta t = new Terapeuta();
		t = terapeutaDao.findByUsuarioId(usuario.getId());
		if(t!=null) {
			t.setFechaBaja(new Date());
		}
		Paciente p = new Paciente();
		p = pacienteDao.findByUsuarioId(usuario.getId());
		if(p!=null) {
			p.setFechaBaja(new Date());
			for(Turno turno : p.getTurnos()) {
				turnoDao.delete(turno);
			}
		}
		usuarioDao.save(usuario);
	}

	@Override
	public Usuario findOne(Long id) {
		return usuarioDao.findById(id).orElse(null);
	}

	@Override
	public void deleteAll() {
		usuarioDao.deleteAll();
		
	}

	@Override
	public Usuario findByMail(String mail) {
		return usuarioDao.porMail(mail);
	}
	
}
