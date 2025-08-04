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

public class DatosEstacionType1 extends DatosEstacion {

	// Datos METEOROLOGICOS
	private int probabilidadPrecipitacion;
	private int precipitacion;

	// CONSTRUCTOR:
	public DatosEstacionType1(String localizacion) {
		super(localizacion);
	}

	@Override
	public void actualizaVariables() {

		actualizarTemperatura();

		probabilidadPrecipitacion = (int) (Math.random() * 100); // Entre 0 y 100

		precipitacion = (int) (Math.random() * 50); // Valor entre 0 y 50
	}

	public int getProbabilidadPrecipitacion() {
		return probabilidadPrecipitacion;
	}

	public int getPrecipitacion() {
		return precipitacion;
	}

	public void setProbabilidadPrecipitacion(int probabilidadPrecipitacion) {
		this.probabilidadPrecipitacion = probabilidadPrecipitacion;
	}

	public void setPrecipitacion(int precipitacion) {
		this.precipitacion = precipitacion;
	}

	@Override
	public String toString() {
		String str = "-Servidor de " + this.getLocalizacion() + "\n";
		str += "-Temperatura: " + getTemperatura() + "°" + getUnidadTemperatura() + "\n";
		str += "-Prob. de precipitacion: " + probabilidadPrecipitacion + "%" + "\n";
		str += "-Precipitacion: " + precipitacion + "mm" + "\n";
		return str;
	}

	@Override
	public String toHTML() {
		String str = "<table class=\"tb\" style=\"width:300px;\">";
		str += "<tr><th>Servidor de " + getLocalizacion() + "</th><th>Valor</th></tr>";
		str += "<tr><td>Temperatura</td><td>" + getTemperatura() + "°" + getUnidadTemperatura() + "</td></tr>";
		str += "<tr><td>Prob. Precipitacion</td><td>" + probabilidadPrecipitacion + "%" + "</td></tr>";
		str += "<tr><td>Precipitacion</td><td>" + precipitacion + "mm" + "</td></tr>";
		str += "</table>";
		return str;
	}

	@Override
	public String serializeJSON() {
		return new GsonBuilder().setPrettyPrinting().create().toJson(this);
	}

	public static DatosEstacionType1 deserializeJSON(String message) {
		return new Gson().fromJson(message, DatosEstacionType1.class);
	}

	@Override
	public String serializeXML() {
		String str = "";
		str += "<DatosEstacionType1>" + "\n";
		str += "\t" + "<localizacion>" + this.getLocalizacion() + "</localizacion>" + "\n";
		str += "\t" + "<temperatura>" + getTemperatura() + "</temperatura>" + "\n";
		str += "\t" + "<unidadesTemperatura>" + getUnidadTemperatura() + "</unidadesTemperatura>" + "\n";
		str += "\t" + "<probabilidadPrecipitacion>" + probabilidadPrecipitacion + "</probabilidadPrecipitacion>" + "\n";
		str += "\t" + "<precipitacion>" + precipitacion + "</precipitacion>" + "\n";
		str += "</DatosEstacionType1>";
		return str;
	}

	public static DatosEstacionType1 deserializeXML(String message) {
		try {

			// Validamos el mensaje con el esquema y a continuacion lo parseamos:
			DatosEstacion.validateXML(message, "EsquemasXML/DatosEstacionType1.xsd");

			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			saxParserFactory.setValidating(true);
			SAXParser saxParser = saxParserFactory.newSAXParser();
			DatosEstacionType1_Handler handler = new DatosEstacionType1_Handler();
			saxParser.parse(new InputSource(new StringReader(message)), handler);
			return handler.getDatosEstacion();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}

class DatosEstacionType1_Handler extends DefaultHandler {

	private DatosEstacionType1 datosEstacion;
	private StringBuilder buffer = new StringBuilder();

	public DatosEstacionType1 getDatosEstacion() {
		return datosEstacion;
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		buffer.append(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		switch (qName) {
		case "DatosEstacionType1":
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
		case "probabilidadPrecipitacion":
			datosEstacion.setProbabilidadPrecipitacion(Integer.parseInt(buffer.toString()));
			break;
		case "precipitacion":
			datosEstacion.setPrecipitacion(Integer.parseInt(buffer.toString()));
			break;
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		switch (qName) {

		case "DatosEstacionType1":
			datosEstacion = new DatosEstacionType1("");
			break;

		case "localizacion":
		case "temperatura":
		case "unidadesTemperatura":
		case "probabilidadPrecipitacion":
		case "precipitacion":
			buffer.delete(0, buffer.length());
			break;
		}
	}
}
