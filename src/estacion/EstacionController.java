package estacion;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

import otros.GestorLogs;
import otros.Message;
import vistas.VistaEstacion;

public class EstacionController extends Thread {

	// CONSTANTES:
	private static final String STOP_SERVER = "STOP_SERVER";
	private static final String OK_STOP_SERVER = "OK_STOP_SERVER";

	private static final String START_SERVER = "START_SERVER";
	private static final String OK_START_SERVER = "OK_START_SERVER";

	private static final String SET_TIME = "SET_TIME";
	private static final String OK_SET_TIME = "OK_SET_TIME";

	private static final String SET_UNIDADES_CELSIUS = "SET_UNIDADES_CELSIUS";
	private static final String OK_SET_UNIDADES_CELSIUS = "OK_SET_UNIDADES_CELSIUS";

	private static final String SET_UNIDADES_KELVIN = "SET_UNIDADES_KELVIN";
	private static final String OK_SET_UNIDADES_KELVIN = "OK_SET_UNIDADES_KELVIN";

	private static final String SET_UNIDADES_FAHRENHEIT = "SET_UNIDADES_FAHRENHEIT";
	private static final String OK_SET_UNIDADES_FAHRENHEIT = "OK_SET_UNIDADES_FAHRENHEIT";

	private static final String SET_CODIFICACION_JSON = "SET_CODIFICACION_JSON";
	private static final String OK_SET_CODIFICACION_JSON = "OK_SET_CODIFICACION_JSON";

	private static final String SET_CODIFICACION_XML = "SET_CODIFICACION_XML";
	private static final String OK_SET_CODIFICACION_XML = "OK_SET_CODIFICACION_XML";

	private static final int PACKET_MAX_SIZE = 1080;
	private double messageDiscardProbability = 0.35; // Probabilidad de que la estacion descarte un paquete [0.0, 1.0]
	private static final String separadorMsg = " - ";

	// ATRIBUTOS:
	private DatagramSocket socket = null;
	private String tipoSerializacionEstacion = null;
	private byte[] buf = new byte[PACKET_MAX_SIZE];
	private GestorLogs gestorLogs = null; // Para gestionar el fichero de logs de la estacion
	private Estacion estacion = null; // Estacion que esta controlando
	private VistaEstacion vista = null; // Vista grafica de la estacion y controlador

	// CONSTRUCTOR:
	public EstacionController(Estacion estacion, String IP, int PORT, String tipoSerializacionEstacion,
			GestorLogs gestorLogs, VistaEstacion vista) {

		try {
			socket = new DatagramSocket(new InetSocketAddress(IP, PORT));
		} catch (SocketException e) {
			System.err.println("La direccion/puerto ya está en uso.");
			System.exit(1);
		}

		this.vista = vista;
		this.estacion = estacion;
		this.tipoSerializacionEstacion = tipoSerializacionEstacion;
		this.gestorLogs = gestorLogs;
	}

	// METODOS:
	public void setTipoSerializacion(String tipoSerializacion) {
		this.tipoSerializacionEstacion = tipoSerializacion;
	}

	@Override
	public void run() {

		System.out.println("Corriendo hilo para RECIBIR mensajes de control.\n");
		vista.writeInControl("Corriendo hilo para RECIBIR mensajes de control.\n");
		while (true) {
			try {

				// Creamos el paquete donde vamos a recibir el mensaje y lo recibimos
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);
				String message = new String(buf, 0, packet.getLength());

				// Extraemos la direccion del cliente
				InetSocketAddress clientAddress = (InetSocketAddress) packet.getSocketAddress();

				// Vemos si el mensaje debe ser descartado por la probabilidad de descarte
				double rand = Math.random();
				if (rand < messageDiscardProbability) {
					vista.writeInControl("Estacion DESCARTA un paquete corrupto...");
					continue;
				}

				// Analizar y procesar la solicitud
				String[] messageParts = message.split(separadorMsg);
				String tipoSerializacionCliente = messageParts[0];
				message = messageParts[1];

				gestorLogs.addContenido(message); // Almacenamos el mensaje en el fichero de logs

				if (tipoSerializacionCliente.equals("XML")) {
					processMessage(Message.deserializeXML(message), clientAddress);
				} else if (tipoSerializacionCliente.equals("JSON")) {
					processMessage(Message.deserializeJSON(message), clientAddress);
				} else {
					System.err.println("Tipo de serializacion no valido");
					System.exit(1);
				}

			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("ERROR al procesar la peticion de un cliente.");
			}
		}
	}

	// Metodo para procesar el mensaje que nos llega del cliente
	public void processMessage(Message msg, InetSocketAddress clientAddr) {
		vista.writeInControl("Mensaje Recibido: " + msg.toString());
		switch (msg.getOperacion()) {

		case STOP_SERVER: {
			estacion.pararEstacion();
			sendACK(msg.getLocalidad(), OK_STOP_SERVER, clientAddr);
			break;
		}

		case START_SERVER: {
			estacion.renaudarEstacion();
			sendACK(msg.getLocalidad(), OK_START_SERVER, clientAddr);
			break;
		}

		case SET_TIME: {
			estacion.setTime(Integer.parseInt(msg.getArg()));
			sendACK(msg.getLocalidad(), OK_SET_TIME, clientAddr);
			break;
		}

		case SET_UNIDADES_CELSIUS: {
			estacion.setUnidadesTemperaturaCelsius();
			sendACK(msg.getLocalidad(), OK_SET_UNIDADES_CELSIUS, clientAddr);
			break;
		}

		case SET_UNIDADES_KELVIN: {
			estacion.setUnidadesTemperaturaKelvin();
			sendACK(msg.getLocalidad(), OK_SET_UNIDADES_KELVIN, clientAddr);
			break;
		}

		case SET_UNIDADES_FAHRENHEIT: {
			estacion.setUnidadesTemperaturaFahrenheit();
			sendACK(msg.getLocalidad(), OK_SET_UNIDADES_FAHRENHEIT, clientAddr);
			break;
		}

		case SET_CODIFICACION_JSON: {
			estacion.setSerializacionJSON();
			sendACK(msg.getLocalidad(), OK_SET_CODIFICACION_JSON, clientAddr);
			break;
		}

		case SET_CODIFICACION_XML: {
			estacion.setSerializacionXML();
			sendACK(msg.getLocalidad(), OK_SET_CODIFICACION_XML, clientAddr);
			break;
		}

		default:
			System.err.println("Opeción no esperada.");
			System.exit(1);
		}
	}

	// Método para enviar un ACK al cliente
	private void sendACK(String localidad, String operacion, InetSocketAddress clientAddr) {

		// Construimos la respuesta:
		String res = tipoSerializacionEstacion + separadorMsg;

		// Construimos el mensaje de la respuesta
		Message msg = new Message(operacion, localidad);

		if (tipoSerializacionEstacion.equals("XML")) {
			res += msg.serializeXML();
		} else if (tipoSerializacionEstacion.equals("JSON")) {
			res += msg.serializeJSON();
		} else {
			System.err.println("Tipo de serializacion no valido");
			System.exit(1);
		}

		// Construimos el paquete de respuesta
		byte[] buf = res.getBytes();
		DatagramPacket packet = new DatagramPacket(buf, buf.length, clientAddr);

		// Enviamos el paquete de respuesta
		try {
			socket.send(packet);
			vista.writeInControl("Respuesta Enviada: " + msg.toString() + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
