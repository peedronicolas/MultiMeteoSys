package server_HTTP_HTTPS;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cliente.ClientController;
import datos.DatosEstacion;

class GestorPeticionServer extends Thread {

	// ATRIBUTOS:
	private Socket socket;
	private HashMap<String, DatosEstacion> registro;
	private ClientController clientController;

	// CONSTRUCTOR:
	public GestorPeticionServer(Socket s, HashMap<String, DatosEstacion> registro, ClientController clientController) {
		this.socket = s;
		this.registro = registro;
		this.clientController = clientController;
	}

	// Funcion que crea el codigo HTML que genera el servidor como respuesta a
	// partir de el conjunto estaciones con sus ultimos datos meteorologicos
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
		htmlCode += "<body style=\"background-color:grey;\">";
		htmlCode += "<h1 align = center>Programación para las Comunicaciones - PPC</h1>";
		htmlCode += "<h1 align = center>Pedro Nicolas Gomariz</h1>";
		htmlCode += "<br>";
		htmlCode += "<h1 align = center>Servidor de DATOS METEOROLÓGICOS</h1>";
		htmlCode += "<h2>Últimos datos meteorologícos recibidos:</h2>";

		// Añadimos la tabla de cada una de las estaciones
		for (String key : registro.keySet())
			htmlCode += registro.get(key).toHTML() + "<br>";

		htmlCode += "</body></html>";
		return htmlCode;
	}

	@SuppressWarnings("deprecation")
	public void run() {

		DataInputStream sIn;
		PrintStream sOut;

		try {

			sIn = new DataInputStream(socket.getInputStream());
			sOut = new PrintStream(socket.getOutputStream());

			// Leemos la peticion y sacamos el recurso:
			String line = sIn.readLine();
			String request = line + "\r\n";
			while (sIn.available() > 0) {
				line = sIn.readLine();
				request += line + "\r\n";
			}

			String recurso = null;
			Pattern pattern = Pattern.compile("GET (.*) HTTP");
			Matcher matcher = pattern.matcher(request);
			if (matcher.find()) {
				recurso = matcher.group(1);
			} else {
				System.err.println("Error al leer el recurso.");
			}
			if (recurso == null)
				return;

			// Respuesta para enviar al cliente
			String response = "";

			if (recurso.equals("/") || recurso.equals("/index.html")) {

				File archivo = new File("./web/index.html");
				FileReader fr = new FileReader(archivo);
				BufferedReader br = new BufferedReader(fr);
				String codeHTML = "";

				response += "HTTP/1.1 200 OK\r\n";
				response += "Connection: close\r\n";
				response += "Content-Type: text/html; charset=utf-8\r\n";
				response += "Content-length: " + archivo.length() + "\r\n";
				response += "\r\n";

				while ((codeHTML = br.readLine()) != null)
					response += codeHTML;

				br.close();

			} else if (recurso.equals("/meteorologia.html")) {

				response += "HTTP/1.1 200 OK\r\n";
				response += "Connection: close\r\n";
				response += "Content-Type: text/html; charset=utf-8\r\n";
				String codeHTML = createHTMLcode(registro);
				response += "Content-length: " + codeHTML.length() + "\r\n";
				response += "\r\n";
				response += codeHTML;

			} else if (recurso.startsWith("/apirest/")) {

				response += new GestorAPI_Rest(registro, clientController).processURL(recurso.substring(9));

			} else {

				String htmlCode = "<!DOCTYPE html><html>";
				htmlCode += "<body style=\"background-color:grey;\">";
				htmlCode += "<br><br><h1 align = center>ERROR 404</h1>";
				htmlCode += "<h1 align = center>Recurso no ENCONTRADO.</h1>";
				htmlCode += "</body></html>";

				response += "HTTP/1.1 404 Not Found\r\n";
				response += "Connection: close\r\n";
				response += "Content-Type: text/html; charset=utf-8\r\n";
				response += "Content-length: " + htmlCode.length() + "\r\n";
				response += "\r\n";
				response += htmlCode;
			}

			// Enviamos por el socket la respuesta
			sOut.println(response);

			sIn.close();
			sOut.close();
			socket.close();

		} catch (IOException e) {

			try {
				socket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
}