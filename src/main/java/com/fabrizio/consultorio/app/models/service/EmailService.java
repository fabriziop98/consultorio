package com.fabrizio.consultorio.app.models.service;

import java.util.Arrays;
import java.util.List;

import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.fabrizio.consultorio.app.models.entity.UsuarioConsulta;

@Service
public class EmailService {

	@Autowired
	JavaMailSender sender;

	@Autowired
	@Value("${com.fabrizio.consultorio.app.models.service.emailService.from}")
	private String from;

	private Logger log = LoggerFactory.getLogger(EmailService.class);

	public void formularioContacto(UsuarioConsulta usuario) {

		StringBuilder sb = new StringBuilder("&iexcl;Hola, " + usuario.getNombre() + "!"
				+ "<br/>&iexcl;Gracias por tu inter&eacute;s en nosotros!<br>Nos pondremos en contacto contigo en breve.");
		StringBuilder sbRegistro = new StringBuilder("&iexcl;Nuevo interesado!: " + usuario.getNombre()
				+ "<br/>Email: " + usuario.getEmail() + "<br>Tel&eacute;fono: " + usuario.getTelefono() + "<br>Consulta: "
				+ usuario.getConsulta());
//	        StringBuilder url = new StringBuilder();
//	        url.append("https://entramate.org/usuario/validar/" + usuario.getId());
//	        sb.append(url);
//	        sb.append("\">&Eacute;ste elance</a>");
		enviar(usuario.getEmail(), "Gracias por tu consulta", sb.toString());
		enviar("marcela_mateu@hotmail.com", "Nuevo interesado", sbRegistro.toString());

	}

	private void enviar(String to, String asunto, String mensaje) {
		envio(Arrays.asList(to), asunto, mensaje);
	}

	private void envio(List<String> to, String asunto, String cuerpo) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				log.info("Enviando e-mail con asunto '{}' a '{}'", asunto, to);
				try {
					MimeMessage mensaje = sender.createMimeMessage();
					MimeMessageHelper helper = new MimeMessageHelper(mensaje, "UTF-8");
					helper.setFrom(from, "Fonoaudiología Marcela Mateu");
					helper.setTo(to.toArray(new String[] {}));
					helper.setSubject(asunto);
					helper.setText(cuerpo, true);

					String htmlMessage = "";
					htmlMessage += cuerpo;
	                   
					htmlMessage += "<html><br/><small><em>Gracias tu inter&eacute;s en nuestro consultorio. </em></small><br/>";
//					 htmlMessage += "<br/><img th:href=\"@{/images/logoCorto.png}\" width=\"100%\" /><br/>";
					htmlMessage += "</html>";
					MimeBodyPart messageBodyPart = new MimeBodyPart();
					messageBodyPart.setContent(htmlMessage, "text/html");

					Multipart multipart = new MimeMultipart();
					multipart.addBodyPart(messageBodyPart);

					mensaje.setContent(multipart);

					sender.send(mensaje);
				} catch (Exception ex) {
					log.error(ex.getMessage(), ex);
				}
			}
		}).start();
	}

}
