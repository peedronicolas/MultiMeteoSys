package server_HTTP_HTTPS;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.GsonBuilder;

import cliente.ClientController;
import datos.DatosEstacion;
import otros.Message;

public class GestorAPI_Rest {

	// ATRIBUTOS:
	private HashMap<String, DatosEstacion> registro;
	private ClientController clientController;

	// CONSTRUCTOR:
	public GestorAPI_Rest(HashMap<String, DatosEstacion> registro, ClientController clientController) {
		this.registro = registro;
		this.clientController = clientController;
	}

	private String createResponse(String codeJSON) {
		String response = "HTTP/1.1 200 OK\r\n";
		response += "Connection: close\r\n";
		response += "Content-Type: application/json; charset=utf-8\r\n";
		response += "Content-length: " + codeJSON.length() + "\r\n";
		response += "\r\n";
		response += codeJSON;
		return response;
	}

	// METODOS:
	public String processURL(String URL) {

		if (URL.equals("meteorologia")) {
			return createResponse(
					new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(registro.values()));

		} else if (URL.startsWith("time?")) {

			Pattern pat = Pattern.compile("cod=(.*)&est=(.*)&time=(.*)");
			Matcher mat = pat.matcher(URL);
			if (mat.find()) {
				Message respuesta = clientController
						.processComand(mat.group(1) + " " + mat.group(2) + " time " + mat.group(3));
				if (respuesta != null)
					return createResponse(respuesta.serializeJSON());
			}

		} else if (URL.startsWith("stop?")) {

			Pattern pat = Pattern.compile("cod=(.*)&est=(.*)");
			Matcher mat = pat.matcher(URL);
			if (mat.find()) {
				Message respuesta = clientController.processComand(mat.group(1) + " " + mat.group(2) + " stop");
				if (respuesta != null)
					return createResponse(respuesta.serializeJSON());
			}

		} else if (URL.startsWith("start?")) {

			Pattern pat = Pattern.compile("cod=(.*)&est=(.*)");
			Matcher mat = pat.matcher(URL);
			if (mat.find()) {
				Message respuesta = clientController.processComand(mat.group(1) + " " + mat.group(2) + " start");
				if (respuesta != null)
					return createResponse(respuesta.serializeJSON());
			}

		} else if (URL.startsWith("codeJSON?")) {

			Pattern pat = Pattern.compile("cod=(.*)&est=(.*)");
			Matcher mat = pat.matcher(URL);
			if (mat.find()) {
				Message respuesta = clientController.processComand(mat.group(1) + " " + mat.group(2) + " code json");
				if (respuesta != null)
					return createResponse(respuesta.serializeJSON());
			}

		}

		else if (URL.startsWith("codeXML?")) {

			Pattern pat = Pattern.compile("cod=(.*)&est=(.*)");
			Matcher mat = pat.matcher(URL);
			if (mat.find()) {
				Message respuesta = clientController.processComand(mat.group(1) + " " + mat.group(2) + " code xml");
				if (respuesta != null)
					return createResponse(respuesta.serializeJSON());
			}

		} else if (URL.startsWith("tempC?")) {

			Pattern pat = Pattern.compile("cod=(.*)&est=(.*)");
			Matcher mat = pat.matcher(URL);
			if (mat.find()) {
				Message respuesta = clientController.processComand(mat.group(1) + " " + mat.group(2) + " ut c");
				if (respuesta != null)
					return createResponse(respuesta.serializeJSON());
			}

		}

		else if (URL.startsWith("tempK?")) {

			Pattern pat = Pattern.compile("cod=(.*)&est=(.*)");
			Matcher mat = pat.matcher(URL);
			if (mat.find()) {
				Message respuesta = clientController.processComand(mat.group(1) + " " + mat.group(2) + " ut k");
				if (respuesta != null)
					return createResponse(respuesta.serializeJSON());
			}

		} else if (URL.startsWith("tempF?")) {

			Pattern pat = Pattern.compile("cod=(.*)&est=(.*)");
			Matcher mat = pat.matcher(URL);
			if (mat.find()) {
				Message respuesta = clientController.processComand(mat.group(1) + " " + mat.group(2) + " ut f");
				if (respuesta != null)
					return createResponse(respuesta.serializeJSON());
			}

		}

		// Si llega aqui ha habido algun tipo de error:
		String codeJSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
				.toJson(new Message("ERROR"));
		String response = "HTTP/1.1 404 Not Found\r\n";
		response += "Connection: close\r\n";
		response += "Content-Type: application/json; charset=utf-8\r\n";
		response += "Content-length: " + codeJSON.length() + "\r\n";
		response += "\r\n";
		response += codeJSON;
		return response;
	}
}