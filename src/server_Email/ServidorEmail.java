package server_Email;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;

import datos.DatosEstacion;

public class ServidorEmail extends Thread {

	// ATRIBUTOS:
	private static final String FROM = "pedro.nicolasg@um.es";
	private static final String FROMNAME = "Servicio Meteorológico";
	private static final String SUBJECT = "Informe Meteorológico";

	private static final String SMTP_USERNAME = "pedro.nicolasg";
	private static final String PASSWORD = "";
	private static final String SMTP_HOST = "smtp.um.es";
	private static final int SMTP_PORT = 587;

	private static final String IMAP_HOST = "imap.um.es";
	private static final int IMAP_PORT = 993;

	private HashMap<String, DatosEstacion> registro;

	public ServidorEmail(HashMap<String, DatosEstacion> registro) {
		this.registro = registro;
	}

	// MÉTODOS:

	// Funcion que crea el codigo HTML que enviara el servidor de email como
	// respuesta a una peticion de datos meteorologicos
	private static String createHTMLcode(HashMap<String, DatosEstacion> registro) {

		String htmlCode = "";
		htmlCode += "<!DOCTYPE html>";
		htmlCode += "<html>";
		htmlCode += "<head><style>";
		htmlCode += ".tb { border-collapse: collapse; }";
		htmlCode += ".tb th, .tb td { padding: 5px; border: solid 2px #000; }";
		htmlCode += ".tb th { background-color: lightblue; }";
		htmlCode += ".tb td { background-color: white; }";
		htmlCode += "</style></head>";
		htmlCode += "<body>";
		htmlCode += "<h1 align = left>Servidor de DATOS METEOROLÓGICOS</h1>";
		htmlCode += "<h2>Últimos datos meteorologícos recibidos:</h2>";

		// Añadimos la tabla de cada una de las estaciones
		for (String key : registro.keySet())
			htmlCode += registro.get(key).toHTML() + "<br>";

		htmlCode += "</body></html>";
		return htmlCode;
	}

	public void replyMail() throws Exception {

		// Creamos el objeto con las propiedades de la conexion
		Properties props = System.getProperties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.port", SMTP_PORT);
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.auth.login.disable", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.starttls.required", "true");

		Session session = Session.getDefaultInstance(props); // Creamos la sesion
		Transport transport = session.getTransport(); // Creamos el transporte para enviar mensajes

		// Obtenemos los mensajes NO LEIDOS
		Store store = session.getStore("imaps");
		store.connect(IMAP_HOST, IMAP_PORT, FROM, PASSWORD);
		Folder inbox = store.getFolder("INBOX");
		inbox.open(Folder.READ_WRITE);

		Message[] allUnreadMessages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));

		// Filtramos de los mensajes NO LEIDOS aquellos cuyo asunto es 'meteorologia'
		LinkedList<Message> messages = new LinkedList<>();
		for (Message message : allUnreadMessages)
			if (message.getSubject().equals("meteorologia"))
				messages.add(message);

		// Respondemos a cada uno de los mensajes (si los hay)
		for (Message message : messages) {

			// Creamos la respuesta con la informacion especifica
			MimeMessage msg = new MimeMessage(session);
			msg = (MimeMessage) message.reply(false);
			msg.setFrom(new InternetAddress(FROM, FROMNAME));
			msg.setReplyTo(message.getReplyTo());
			msg.setSubject(SUBJECT);

			// Declaramos el multipart de la respuesta y cada una de las partes
			MimeMultipart multiPart = new MimeMultipart();
			BodyPart htmlPart = new MimeBodyPart();
			BodyPart adjunto = null;

			// Declaramos la parte del HTML
			htmlPart.setContent(createHTMLcode(registro), "text/html; charset=utf-8");
			multiPart.addBodyPart(htmlPart);

			// Declaramos cada una de las partes relacionacon con los archivos json adjuntos
			for (String reg : registro.keySet()) {
				adjunto = new MimeBodyPart();
				adjunto.setDataHandler(new DataHandler(registro.get(reg).serializeJSON(), "application/json"));
				adjunto.setFileName(reg + ".json");
				multiPart.addBodyPart(adjunto);
			}

			// Establecemos el contenido de la respuesta
			msg.setContent(multiPart);

			// Enviamos la respuesta
			try {
				System.out.println("Enviando el email a '" + message.getFrom()[0] + "'");

				// Nos conectamos al servidor SMTP con las creedenciales
				transport.connect(SMTP_HOST, SMTP_USERNAME, PASSWORD);

				// Enviamos la respuesta
				transport.sendMessage(msg, msg.getAllRecipients());

				// Marcamos el email de la bandeja de entrada como leido
				inbox.setFlags(new Message[] { message }, new Flags(Flags.Flag.SEEN), true);
				System.out.println("Email enviado.\n");
			}

			catch (Exception e) {
				System.out.println("El email no ha sido enviado.");
				System.out.println("Error message: " + e.getMessage());
			} finally {
				// Cerramos y terminamos la conexion
				transport.close();
			}
		}

		inbox.close(false);
		store.close();
	}

	@Override
	public void run() {

		System.out.println("*********************************");
		System.out.println("** The EMAIL server is running **");
		System.out.println("*********************************");
		System.out.println();

		while (true)
			try {

				// Comprobamos si hay emails de solicitud y en ese caso respondemos
				replyMail();

				// Esperamos 15 seg.
				sleep(15000);

			} catch (Exception e) {
				e.printStackTrace();
			}
	}
}