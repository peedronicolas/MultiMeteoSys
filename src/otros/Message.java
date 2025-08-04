package otros;

import java.io.StringReader;

import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import datos.DatosEstacion;

public class Message {

	// ATRIBUTOS:
	private String operacion = null; // Operacion a realizar
	private String localidad = null; // Localidad de la estacion
	private String argumento = null; // Argumento variable

	// CONSTRUCTORES:
	public Message(String operacion, String localidad, String arg) {
		this.operacion = operacion;
		this.localidad = localidad;
		this.argumento = arg;
	}

	public Message(String operacion, String localidad) {
		this.operacion = operacion;
		this.localidad = localidad;
	}

	public Message(String operacion) {
		this.operacion = operacion;
	}

	public Message() {
	}

	// METODOS:
	public String getOperacion() {
		return operacion;
	}

	public String getLocalidad() {
		return localidad;
	}

	public String getArg() {
		return argumento;
	}

	public void setOperacion(String operacion) {
		this.operacion = operacion;
	}

	public void setLocalidad(String localidad) {
		this.localidad = localidad;
	}

	public void setArg(String arg) {
		this.argumento = arg;
	}

	@Override
	public String toString() {
		return "{" + operacion + ", " + localidad + ", " + argumento + "}";
	}

	public String serializeJSON() {
		return new GsonBuilder().setPrettyPrinting().create().toJson(this);
	}

	public static Message deserializeJSON(String message) {
		return new Gson().fromJson(message, Message.class);
	}

	public String serializeXML() {
		String str = "";
		str += "<Message>" + "\n";
		str += "\t" + "<operacion>" + operacion + "</operacion>" + "\n";
		str += "\t" + "<localidad>" + localidad + "</localidad>" + "\n";
		str += "\t" + "<argumento>" + argumento + "</argumento>" + "\n";
		str += "</Message>";
		return str;
	}

	public static void validateXML(String message, String rutaEsquema) {
		try {

			SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = schemaFactory.newSchema(new StreamSource(rutaEsquema));
			Validator validator = schema.newValidator();
			validator.validate(new SAXSource(new InputSource(new StringReader(message))));

		} catch (Exception e) {
			System.err.println("ERROR al validar el mensaje XML con su esquema.");
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static Message deserializeXML(String message) {
		try {

			// Validamos el mensaje con el esquema y a continuacion lo parseamos:
			DatosEstacion.validateXML(message, "EsquemasXML/Message.xsd");

			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			saxParserFactory.setValidating(true);
			SAXParser saxParser = saxParserFactory.newSAXParser();
			Message_Handler handler = new Message_Handler();
			saxParser.parse(new InputSource(new StringReader(message)), handler);
			return handler.getMessage();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}

class Message_Handler extends DefaultHandler {

	private Message message;
	private StringBuilder buffer = new StringBuilder();

	public Message getMessage() {
		return message;
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		buffer.append(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		switch (qName) {
		case "Message":
			break;
		case "operacion":
			message.setOperacion(buffer.toString());
			break;
		case "localidad":
			if (buffer.toString().equals("null"))
				message.setLocalidad(null);
			else
				message.setLocalidad(buffer.toString());
			break;
		case "argumento":
			if (buffer.toString().equals("null"))
				message.setArg(null);
			else
				message.setArg(buffer.toString());
			break;
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		switch (qName) {

		case "Message":
			message = new Message();
			break;

		case "operacion":
		case "localidad":
		case "argumento":
			buffer.delete(0, buffer.length());
			break;
		}
	}
}
