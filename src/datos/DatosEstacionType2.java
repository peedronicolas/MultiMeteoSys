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

public class DatosEstacionType2 extends DatosEstacion {

	// Datos METEOROLOGICOS
	private double sensacionTermica;
	private int dirViento;

	// CONSTRUCTOR:
	public DatosEstacionType2(String localizacion) {
		super(localizacion);
	}

	@Override
	public void actualizaVariables() {

		actualizarTemperatura();

		// En grados CELSIUS
		sensacionTermica = Math.random() * ((getTemperatura() + 5) + 1 - (getTemperatura() - 5))
				+ (getTemperatura() - 5);

		// Redondeamos de dos decimales el valor de temperatura:
		sensacionTermica = Math.rint(sensacionTermica * 100) / 100;

		dirViento = (int) (Math.random() * 360); // Valor entre 0 y 360
	}

	public double getSensacionTermica() {
		return sensacionTermica;
	}

	public int getDirViento() {
		return dirViento;
	}

	public void setSensacionTermica(double sensacionTermica) {
		this.sensacionTermica = sensacionTermica;
	}

	public void setDirViento(int dirViento) {
		this.dirViento = dirViento;
	}

	@Override
	public String toString() {
		String str = "-Servidor de " + this.getLocalizacion() + "\n";
		str += "-Temperatura: " + getTemperatura() + "°" + getUnidadTemperatura() + "\n";
		str += "-Sensacion Termica: " + sensacionTermica + "°" + getUnidadTemperatura() + "\n";
		str += "-Direccion del viento: " + dirViento + "°" + "\n";
		return str;
	}

	@Override
	public String toHTML() {
		String str = "<table class=\"tb\" style=\"width:300px;\">";
		str += "<tr><th>Servidor de " + getLocalizacion() + "</th><th>Valor</th></tr>";
		str += "<tr><td>Temperatura</td><td>" + getTemperatura() + "°" + getUnidadTemperatura() + "</td></tr>";
		str += "<tr><td>Sensación Termica</td><td>" + sensacionTermica + "°" + getUnidadTemperatura() + "</td></tr>";
		str += "<tr><td>Direccion del viento</td><td>" + dirViento + "°" + "</td></tr>";
		str += "</table>";
		return str;
	}

	@Override
	public String serializeJSON() {
		return new GsonBuilder().setPrettyPrinting().create().toJson(this);
	}

	public static DatosEstacionType2 deserializeJSON(String message) {
		return new Gson().fromJson(message, DatosEstacionType2.class);
	}

	@Override
	public String serializeXML() {
		String str = "";
		str += "<DatosEstacionType2>" + "\n";
		str += "\t" + "<localizacion>" + this.getLocalizacion() + "</localizacion>" + "\n";
		str += "\t" + "<temperatura>" + getTemperatura() + "</temperatura>" + "\n";
		str += "\t" + "<unidadesTemperatura>" + getUnidadTemperatura() + "</unidadesTemperatura>" + "\n";
		str += "\t" + "<sensacionTermica>" + sensacionTermica + "</sensacionTermica>" + "\n";
		str += "\t" + "<dirViento>" + dirViento + "</dirViento>" + "\n";
		str += "</DatosEstacionType2>";
		return str;
	}

	public static DatosEstacionType2 deserializeXML(String message) {
		try {

			// Validamos el mensaje con el esquema y a continuacion lo parseamos:
			DatosEstacion.validateXML(message, "EsquemasXML/DatosEstacionType2.xsd");

			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			saxParserFactory.setValidating(true);
			SAXParser saxParser = saxParserFactory.newSAXParser();
			DatosEstacionType2_Handler handler = new DatosEstacionType2_Handler();
			saxParser.parse(new InputSource(new StringReader(message)), handler);
			return handler.getDatosEstacion();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}

class DatosEstacionType2_Handler extends DefaultHandler {

	private DatosEstacionType2 datosEstacion;
	private StringBuilder buffer = new StringBuilder();

	public DatosEstacionType2 getDatosEstacion() {
		return datosEstacion;
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		buffer.append(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		switch (qName) {
		case "DatosEstacionType2":
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
		case "sensacionTermica":
			datosEstacion.setSensacionTermica(Double.parseDouble(buffer.toString()));
			break;
		case "dirViento":
			datosEstacion.setDirViento(Integer.parseInt(buffer.toString()));
			break;
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		switch (qName) {

		case "DatosEstacionType2":
			datosEstacion = new DatosEstacionType2("");
			break;

		case "localizacion":
		case "temperatura":
		case "unidadesTemperatura":
		case "sensacionTermica":
		case "dirViento":
			buffer.delete(0, buffer.length());
			break;
		}
	}
}