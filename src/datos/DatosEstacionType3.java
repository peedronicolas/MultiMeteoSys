package datos;

import java.io.StringReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class DatosEstacionType3 extends DatosEstacion {

	// Datos METEOROLOGICOS
	private int velViento;
	private int dirViento;

	// CONSTRUCTOR:
	public DatosEstacionType3(String localizacion) {
		super(localizacion);
	}

	@Override
	public void actualizaVariables() {

		actualizarTemperatura();

		velViento = (int) (Math.random() * 20); // Valor entre 0 y 20 km/h

		dirViento = (int) (Math.random() * 360); // Valor entre 0 y 360
	}

	public int getVelViento() {
		return velViento;
	}

	public int getDirViento() {
		return dirViento;
	}

	public void setVelViento(int velViento) {
		this.velViento = velViento;
	}

	public void setDirViento(int dirViento) {
		this.dirViento = dirViento;
	}

	@Override
	public String toString() {
		String str = "-Servidor de " + this.getLocalizacion() + "\n";
		str += "-Temperatura: " + getTemperatura() + "°" + getUnidadTemperatura() + "\n";
		str += "-Velocidad del viento: " + velViento + "km/h" + "\n";
		str += "-Direccion del viento: " + dirViento + "°" + "\n";
		return str;
	}

	@Override
	public String toHTML() {
		String str = "<table class=\"tb\" style=\"width:300px;\">";
		str += "<tr><th>Servidor de " + getLocalizacion() + "</th><th>Valor</th></tr>";
		str += "<tr><td>Temperatura</td><td>" + getTemperatura() + "°" + getUnidadTemperatura() + "</td></tr>";
		str += "<tr><td>Velocidad del viento</td><td>" + velViento + "km/h" + "</td></tr>";
		str += "<tr><td>Direccion del viento</td><td>" + dirViento + "°" + "</td></tr>";
		str += "</table>";
		return str;
	}

	@Override
	public String serializeJSON() {
		return new GsonBuilder().setPrettyPrinting().create().toJson(this);
	}

	public static DatosEstacionType3 deserializeJSON(String message) {
		return new Gson().fromJson(message, DatosEstacionType3.class);
	}

	@Override
	public String serializeXML() {
		String str = "";
		str += "<DatosEstacionType3>" + "\n";
		str += "\t" + "<localizacion>" + this.getLocalizacion() + "</localizacion>" + "\n";
		str += "\t" + "<temperatura>" + getTemperatura() + "</temperatura>" + "\n";
		str += "\t" + "<unidadesTemperatura>" + getUnidadTemperatura() + "</unidadesTemperatura>" + "\n";
		str += "\t" + "<velViento>" + velViento + "</velViento>" + "\n";
		str += "\t" + "<dirViento>" + dirViento + "</dirViento>" + "\n";
		str += "</DatosEstacionType3>";
		return str;
	}

	public static DatosEstacionType3 deserializeXML(String message) {
		try {

			// Validamos el mensaje con el esquema y a continuacion lo parseamos:
			DatosEstacion.validateXML(message, "EsquemasXML/DatosEstacionType3.xsd");

			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			saxParserFactory.setValidating(true);
			SAXParser saxParser = saxParserFactory.newSAXParser();
			DatosEstacionType3_Handler handler = new DatosEstacionType3_Handler();
			saxParser.parse(new InputSource(new StringReader(message)), handler);
			return handler.getDatosEstacion();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}

class DatosEstacionType3_Handler extends DefaultHandler {

	private DatosEstacionType3 datosEstacion;
	private StringBuilder buffer = new StringBuilder();

	public DatosEstacionType3 getDatosEstacion() {
		return datosEstacion;
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		buffer.append(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		switch (qName) {
		case "DatosEstacionType3":
			break;
		case "localizacion":
			datosEstacion.setLocalizacion(buffer.toString());
			break;
		case "temperatura":
			datosEstacion.setTemperatura(Double.parseDouble(buffer.toString()));
			break;
		case "unidadesTemperatura":
			datosEstacion.setUnidadTemperatura(buffer.toString());
			break;
		case "velViento":
			datosEstacion.setVelViento(Integer.parseInt(buffer.toString()));
			break;
		case "dirViento":
			datosEstacion.setDirViento(Integer.parseInt(buffer.toString()));
			break;
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		switch (qName) {

		case "DatosEstacionType3":
			datosEstacion = new DatosEstacionType3("");
			break;

		case "localizacion":
		case "temperatura":
		case "unidadesTemperatura":
		case "velViento":
		case "dirViento":
			buffer.delete(0, buffer.length());
			break;
		}
	}
}