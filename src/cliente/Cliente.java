package cliente;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.HashMap;

import datos.DatosEstacion;
import datos.DatosEstacionType1;
import datos.DatosEstacionType2;
import datos.DatosEstacionType3;
import otros.GestorLogs;
import server_Email.ServidorEmail;
import server_HTTP_HTTPS.ServidorWeb;
import vistas.VistaCliente;

public class Cliente extends Thread {

	// ATRIBUTOS:
	private static final String separadorMsg = " - ";
	private DatagramSocket socket;
	private byte[] buf = new byte[1080];
	private GestorLogs gestorLogs = null; // Para gestionar los log en el fichero de logs
	private ClientController clientClontroller = null; // Para la implementacion de los mensajes de control
	private VistaCliente vista = null;
	private HashMap<String, DatosEstacion> registro; // Para tener almacenado el ultimo registro de cada etsacion
														// meteorologica

	// CONSTRUCTOR:
	public Cliente(int puerto_Difusion) {

		try {
			socket = new DatagramSocket(puerto_Difusion);
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.gestorLogs = new GestorLogs("clientLog.txt");
		this.vista = new VistaCliente();
		this.vista.lanzar();
		this.clientClontroller = new ClientController(gestorLogs, vista);

		System.out.println("Cliente escuchando en el puerto " + puerto_Difusion + ":\n");
		vista.writeInDifusion("Cliente escuchando en el puerto " + puerto_Difusion + ":\n");

		this.vista.setClientController(clientClontroller);

		this.registro = new HashMap<>();

		// Lanzamos el servicio HTTP, HTTPS, y de email
		new ServidorWeb().lanzar(registro, clientClontroller);
		new ServidorEmail(registro).start();
	}

	@Override
	public void run() {
		while (true) {

			// Creamos el paquete donde vamos a rebicir mensajes de estaciones
			DatagramPacket packet = new DatagramPacket(buf, buf.length);

			try {
				socket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}

			// Extraemos el mensaje del paquete
			String received = new String(packet.getData(), 0, packet.getLength());

			vista.writeInDifusion("Mensaje recibido de la IP " + packet.getAddress().getCanonicalHostName() + ":"
					+ packet.getPort() + ":");

			// Dividimos el mensaje para extraer cada parte:
			String[] messageParts = received.split(separadorMsg);
			int port_Control = Integer.parseInt(messageParts[0]);
			String typeDatosEstacion = messageParts[1];
			String tipoSerializacion = messageParts[2];
			String message = messageParts[3];

			// Añadimos el mensaje recibido al fichero de logs
			gestorLogs.addContenido(message);

			// Mostramos la info por pantalla
			vista.writeInDifusion(
					"IP/Puerto Control: " + packet.getAddress().getCanonicalHostName() + ":" + port_Control);
			vista.writeInDifusion("Tipo DatosEstacion: " + typeDatosEstacion);
			vista.writeInDifusion("Tipo serializacion: " + tipoSerializacion);
			vista.writeInDifusion("Contenido mensaje:");

			// Deserializamos el contenido del mensaje:
			DatosEstacion datosEstacion = null;
			if (tipoSerializacion.equals("XML")) {

				if (typeDatosEstacion.equals("DatosEstacionType1")) {
					datosEstacion = DatosEstacionType1.deserializeXML(message);
				} else if (typeDatosEstacion.equals("DatosEstacionType2")) {
					datosEstacion = DatosEstacionType2.deserializeXML(message);
				} else if (typeDatosEstacion.equals("DatosEstacionType3")) {
					datosEstacion = DatosEstacionType3.deserializeXML(message);
				} else {
					System.err.println("Tipo de datos no valido");
					System.exit(1);
				}

			} else if (tipoSerializacion.equals("JSON")) {

				if (typeDatosEstacion.equals("DatosEstacionType1")) {
					datosEstacion = DatosEstacionType1.deserializeJSON(message);
				} else if (typeDatosEstacion.equals("DatosEstacionType2")) {
					datosEstacion = DatosEstacionType2.deserializeJSON(message);
				} else if (typeDatosEstacion.equals("DatosEstacionType3")) {
					datosEstacion = DatosEstacionType3.deserializeJSON(message);
				} else {
					System.err.println("Tipo de datos no valido");
					System.exit(1);
				}

			} else {
				System.err.println("Tipo de serializacion no valido");
				System.exit(1);
			}

			// Registramos la estacion para poder controlarla
			clientClontroller.addEstacion(datosEstacion.getLocalizacion(),
					new InetSocketAddress(packet.getAddress().getCanonicalHostName(), port_Control));

			// Guardamos el ultimo mensaje recibido por las estacion en el registro:
			registro.put(datosEstacion.getLocalizacion(), datosEstacion);

			// Mostramos los datos meteorologicos
			vista.writeInDifusion(datosEstacion.toString() + "\n");
		}
	}
}