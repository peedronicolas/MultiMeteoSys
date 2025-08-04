package cliente;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.HashMap;

import otros.GestorLogs;
import otros.Message;
import vistas.VistaCliente;

public class ClientController {

	// CONSTANTES:
	private static final String STOP_SERVER = "STOP_SERVER";
	private static final String START_SERVER = "START_SERVER";
	private static final String SET_TIME = "SET_TIME";
	private static final String SET_UNIDADES_CELSIUS = "SET_UNIDADES_CELSIUS";
	private static final String SET_UNIDADES_KELVIN = "SET_UNIDADES_KELVIN";
	private static final String SET_UNIDADES_FAHRENHEIT = "SET_UNIDADES_FAHRENHEIT";
	private static final String SET_CODIFICACION_JSON = "SET_CODIFICACION_JSON";
	private static final String SET_CODIFICACION_XML = "SET_CODIFICACION_XML";

	private static final int MAX_INTENTOS = 5; // Numero de veces que se intentara reenviar un mensaje de control si no
												// se recibe confirmacion de la estacion
	private static final int TIMEOUT = 1000; // Tiempo que vamos a esperar para reenviar un mensaje de control si no se
												// recibe su confirmacion
	private static final int PACKET_MAX_SIZE = 1080;

	private static final String separadorMsg = " - ";

	// ATRIBUTOS
	private DatagramSocket socket;
	private HashMap<String, InetSocketAddress> infoEstaciones;
	private byte[] buf = new byte[PACKET_MAX_SIZE];
	private String tipoSerializacionCliente = null;
	private GestorLogs gestorLogs = null;
	private VistaCliente vista;

	// CONSTRUCTOR:
	public ClientController(GestorLogs gestorLogs, VistaCliente vista) {

		try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}

		this.infoEstaciones = new HashMap<>();
		this.gestorLogs = gestorLogs;
		this.vista = vista;
	}

	// Metodos para gestionar el conjunto de estaciones disponibles:
	public void addEstacion(String estacion, InetSocketAddress estacionAddr) {
		infoEstaciones.put(estacion.toUpperCase(), estacionAddr);
		vista.writeInEstaciones(infoEstaciones.keySet().toString());
	}

	public void deleteEstacion(String localidadServer) {
		infoEstaciones.remove(localidadServer.toUpperCase());
		vista.writeInEstaciones(infoEstaciones.keySet().toString());
	}

	public void showEstaciones() {
		vista.writeInControl(infoEstaciones.keySet().toString() + "\n");
	}

	// Metodo para mostrar el menu de ayuda
	public void showHelp() {
		String str = "USO: <tipoSerializacion> <nombreEstacion> <operacion> <argumentoOpcional>" + "\n" + "\n";
		str += "Tipo Serializacion: JSON ó XML" + "\n" + "\n";
		str += "Operaciones:" + "\n";
		str += "-start: Para arrancar un servidor.  ej. 'JSON Murcia start'" + "\n";
		str += "-stop: Para parar un servidor.  ej. 'JSON Murcia stop'" + "\n";
		str += "-time: Para cambiar la frecuencia de envio de un servidor.  ej. 'JSON Murcia time (time millis)'"
				+ "\n";
		str += "-ut: Para cambiar las unidades de la variable temperatura.  ej. 'JSON Murcia ut (C, K ó F)'" + "\n";
		str += "-code: Para cambiar el tipo de codificacion a XML o JSON.  ej. 'JSON Murcia code (JSON ó XML)'" + "\n";
		str += "\n";
		str += "-estaciones: Devuelve una lista con las estaciones disponibles." + "\n";
		str += "-clear c: Para limpiar la pantalla de control." + "\n";
		str += "-clear d: Para limpiar la pantalla de difusion." + "\n";
		str += "-help: Muestra el menú de ayuda." + "\n";
		vista.writeInControl(str);
	}

	// Metodo para procesar el comando recibido
	public Message processComand(String comando) {

		String estacion = null;
		String operacion = null;
		String arg = null;

		try {

			if (comando != null && comando.length() > 0) {

				// Si el comando es clear c limpiamos la ventana de control
				if (comando.equals("clear c")) {
					vista.clearControlWindow();
					return null;
				}

				// Si el comando es clear d limpiamos la ventana de difusion
				if (comando.equals("clear d")) {
					vista.clearDifusionWindow();
					return null;
				}

				// Si el comando es help mostramos la ayuda y salimos
				if (comando.equals("help")) {
					showHelp();
					return null;
				}

				// Si el comando es estaciones mostramos la lista de estaciones disponibles
				if (comando.equals("estaciones")) {
					showEstaciones();
					return null;
				}

				// Dividimos el comando para extraer cada operando
				String[] parts = comando.split(" ");

				// Si el comando tiene menos de 3 operandos o mas de 4 teminamos, ya que tres
				// son obligatorios y el ultimo opcional
				if (parts.length < 3 || parts.length > 4) {
					vista.writeInControl("Comando mal formado." + "\n");
					return null;
				}

				tipoSerializacionCliente = parts[0].toUpperCase();
				estacion = parts[1].toUpperCase();
				operacion = parts[2].toLowerCase();

				// Si introducimos una serializacion no valida finalizamos
				if (!(tipoSerializacionCliente.equals("JSON") || tipoSerializacionCliente.equals("XML"))) {
					vista.writeInControl("Tipo de serializacion no valido." + "\n");
					return null;
				}

				// Comprobamos si el servidor está registrado y obtenemos su direccion si no
				// damos error
				InetSocketAddress estacionAddr;
				if (infoEstaciones.containsKey(estacion))
					estacionAddr = infoEstaciones.get(estacion);
				else {
					vista.writeInControl("La estación '" + estacion + "' no está registrada." + "\n");
					return null;
				}

				// Si vamos a realizar una operacion de time o ut y no indicamos el cuarto
				// parametro el comando esta mal formado
				if ((operacion.equals("time") || operacion.equals("ut") || operacion.equals("code"))
						&& parts.length != 4) {
					vista.writeInControl("Comando mal formado." + "\n");
					return null;
				}

				if (parts.length == 4) {
					arg = parts[3].toUpperCase();
				}

				// Si el operando es 'time' comprobamos que el argumento sea un entero
				if (operacion.equals("time")) {
					try {
						Integer.parseInt(arg);
					} catch (NumberFormatException e) {
						vista.writeInControl("El argumento de 'tiempo' debe ser un numero entero.\n");
						return null;
					}
				}

				// Realizamos lo correspondiente dependiendo de cada opracion
				switch (operacion) {

				case "start": {
					return sendControlMessage(new Message(START_SERVER, estacion), estacionAddr);
				}

				case "stop": {
					return sendControlMessage(new Message(STOP_SERVER, estacion), estacionAddr);
				}

				case "time": {
					return sendControlMessage(new Message(SET_TIME, estacion, arg), estacionAddr);
				}

				case "code": {
					switch (arg) {

					case "JSON": {
						return sendControlMessage(new Message(SET_CODIFICACION_JSON, estacion), estacionAddr);
					}

					case "XML": {
						return sendControlMessage(new Message(SET_CODIFICACION_XML, estacion), estacionAddr);
					}
					default:
						vista.writeInControl("Argumento de codificacion no válido. (JSON o XML)" + "\n");
						return null;
					}
				}

				case "ut": {

					switch (arg) {

					case "C": {
						return sendControlMessage(new Message(SET_UNIDADES_CELSIUS, estacion), estacionAddr);
					}

					case "F": {
						return sendControlMessage(new Message(SET_UNIDADES_FAHRENHEIT, estacion), estacionAddr);
					}

					case "K": {
						return sendControlMessage(new Message(SET_UNIDADES_KELVIN, estacion), estacionAddr);
					}

					default:
						vista.writeInControl("Argumento de temperatura no válido." + "\n");
						return null;
					}
				}
				default:
					vista.writeInControl("Comando no válido." + "\n");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	// Método para enviar un mensaje al cliente
	private Message sendControlMessage(Message mesagge, InetSocketAddress estacionAddr) {
		try {

			// El mensaje sera de tipo "JSON/XML + SEPARADOR + MENSAJE SERIALIZADO"
			String res = tipoSerializacionCliente + separadorMsg;

			if (tipoSerializacionCliente.equals("XML")) {
				res += mesagge.serializeXML();
			} else if (tipoSerializacionCliente.equals("JSON")) {
				res += mesagge.serializeJSON();
			} else {
				System.err.println("Tipo de serializacion no valido");
				System.exit(1);
			}

			// Cremos el paquete con el mensaje de control:
			DatagramPacket packet = new DatagramPacket(res.getBytes(), res.getBytes().length, estacionAddr);

			// Enviamos el paquete teniendo en cuenta que se pueden producir errores:
			for (int i = 0; i < MAX_INTENTOS; i++) {

				socket.send(packet);
				vista.writeInControl("Mensaje enviado: " + mesagge.toString());

				DatagramPacket paqueteRespuesta = new DatagramPacket(buf, buf.length);

				socket.setSoTimeout(TIMEOUT); // Establecemos el temporizador para esperar la respuesta

				try {

					socket.receive(paqueteRespuesta);

					// Procesamos el mensaje de respuesta:
					String respuesta = new String(buf, 0, paqueteRespuesta.getLength());
					String[] responseParts = respuesta.split(separadorMsg);
					String tipoSerializacionEstacion = responseParts[0];
					respuesta = responseParts[1];

					// Almacenamos la respuesta en el fichero de log:
					gestorLogs.addContenido(respuesta);

					if (tipoSerializacionEstacion.equals("XML")) {
						vista.writeInControl(
								"Respuesta Recibida: " + Message.deserializeXML(respuesta).toString() + "\n");
						return Message.deserializeXML(respuesta);
					} else if (tipoSerializacionEstacion.equals("JSON")) {
						vista.writeInControl(
								"Respuesta Recibida: " + Message.deserializeJSON(respuesta).toString() + "\n");
						return Message.deserializeJSON(respuesta);
					} else {
						System.err.println("Tipo de serializacion no valido");
						System.exit(1);
						return null;
					}

				} catch (Exception e) {

					vista.writeInControl("Expira el TIMEOUT");
					// Si expira el timeout el numero de intentos la estacion se borra del mapa
					if (i == MAX_INTENTOS - 1) {
						vista.writeInControl("No hay mas intentos.");
						deleteEstacion(mesagge.getLocalidad());
						vista.writeInControl(
								"La estacion '" + mesagge.getLocalidad() + "' ya no esta disponible." + "\n");
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}