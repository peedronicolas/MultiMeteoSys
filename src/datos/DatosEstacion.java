package datos;

import java.io.StringReader;

import javax.xml.XMLConstants;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.InputSource;

public abstract class DatosEstacion {

	// ATRIBUTOS
	private String localizacion; // Localizacion del servidor
	private double temperatura;
	private String unidadTemperatura = "C";
	// C -> CELSIUS
	// K -> KELVIN
	// F -> FAHRENHEIT

	// CONSTRUCTOR
	public DatosEstacion(String localizacion) {
		this.localizacion = localizacion;
	}

	// METODOS ABSTRACTOS
	public abstract void actualizaVariables();

	public abstract String toString();

	public abstract String toHTML();

	public abstract String serializeXML();

	public abstract String serializeJSON();

	// METODOS
	public String getLocalizacion() {
		return localizacion;
	}

	public void setLocalizacion(String localizacion) {
		this.localizacion = localizacion;
	}

	public double getTemperatura() {
		return temperatura;
	}

	public void setTemperatura(double temperatura) {
		this.temperatura = temperatura;
	}

	public String getUnidadTemperatura() {
		return unidadTemperatura;
	}

	public void setUnidadesTemperaturaCelsius() {
		this.unidadTemperatura = "C";
	}

	public void setUnidadesTemperaturaKelvin() {
		this.unidadTemperatura = "K";
	}

	public void setUnidadesTemperaturaFahrenheit() {
		this.unidadTemperatura = "F";
	}

	protected void setUnidadTemperatura(String unidadTemperatura) {
		this.unidadTemperatura = unidadTemperatura;
	}

	public void actualizarTemperatura() {

		// En grados CELSIUS
		temperatura = (Math.random() * 65) - 20; // Valor entre -20 y 45

		// En grados KELVIN
		if (unidadTemperatura.equals("K"))
			temperatura += 273.15;

		// En grados FAHRENHEIT
		if (unidadTemperatura.equals("F"))
			temperatura = temperatura * 1.8 + 32;

		// Redondeamos de dos decimales el valor de temperatura:
		temperatura = Math.rint(temperatura * 100) / 100;
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
}