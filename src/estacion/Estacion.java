package estacion;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import datos.DatosEstacion;
import otros.GestorLogs;
import vistas.VistaEstacion;

public class Estacion extends Thread {

	// ATRIBUTOS
	private static final String separadorMsg = " - ";
	private boolean pararEstacion = false; // Para pausar/reanudar la estacion
	private int time = 5000;// Frecuencia de envio de los mensajes, 5seg por defecto
	private int port_Difusion; // Puerto por el que enviara los mensajes de difusion
	private int port_Control; // Puerto por el que se escucharan los mensajes de control

	private DatagramSocket socket;
	private InetAddress addressBroadcast;

	private DatosEstacion datosEstacion; // Datos meteorologicos de esta estacion
	private String tipoSerializacion = "JSON"; // Por defecto JSON, se puede cambiar a XML
	private GestorLogs gestorLogs = null; // Para gestionar el fichero de logs de la estacion
	private EstacionController estacionController; // Controlador de la estacion para gestionar los mensajes de control
	private VistaEstacion vista = null; // Vista grafica de la estacion y controlador

	// CONSTRUCTOR
	public Estacion(String ip_Broadcast, int port_Difusion, String ip_Unicast, int port_Control,
			DatosEstacion datosEstacion) {

		try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}

		try {
			addressBroadcast = InetAddress.getByName(ip_Broadcast);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		this.port_Difusion = port_Difusion;
		this.port_Control = port_Control;
		this.datosEstacion = datosEstacion;
		this.gestorLogs = new GestorLogs("estacion" + datosEstacion.getLocalizacion() + ".txt");
		this.vista = new VistaEstacion(datosEstacion.getLocalizacion());
		this.vista.lanzar(); // Lanzamos la vista de la estacion
		this.estacionController = new EstacionController(this, ip_Unicast, port_Control, tipoSerializacion, gestorLogs,
				vista);

		estacionController.start(); // Lanzamos el controlador de la estacion
	}

	// Envia el mensaje pasado como parametro a la dir IP y puerto broadcast
	public void sendMessageDifusion(String message) {

		byte[] buf = message.getBytes();
		DatagramPacket packet = new DatagramPacket(buf, buf.length, addressBroadcast, port_Difusion);

		try {
			socket.send(packet);
			vista.writeInDifusion("- Mensaje enviado:");
			vista.writeInDifusion(new String(buf) + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// METODOS:
	synchronized void pararEstacion() {
		pararEstacion = true;
	}

	synchronized void renaudarEstacion() {
		pararEstacion = false;
		notify();
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public DatosEstacion getDatosEstacion() {
		return datosEstacion;
	}

	public void setSerializacionJSON() {
		this.tipoSerializacion = "JSON";
		// Cambiamos tambien la serializacion de los mensajes de control
		estacionController.setTipoSerializacion(tipoSerializacion);
	}

	public void setSerializacionXML() {
		this.tipoSerializacion = "XML";
		// Cambiamos tambien la serializacion de los mensajes de control
		estacionController.setTipoSerializacion(tipoSerializacion);
	}

	public void setUnidadesTemperaturaCelsius() {
		datosEstacion.setUnidadesTemperaturaCelsius();
	}

	public void setUnidadesTemperaturaKelvin() {
		datosEstacion.setUnidadesTemperaturaKelvin();
	}

	public void setUnidadesTemperaturaFahrenheit() {
		datosEstacion.setUnidadesTemperaturaFahrenheit();
	}

	@Override
	public void run() {

		System.out.println("** Servidor CORRIENDO, enviado a " + addressBroadcast.getCanonicalHostName()
				+ " por el puerto " + port_Difusion + " **\n");
		vista.writeInDifusion("** Servidor CORRIENDO, enviado a " + addressBroadcast.getCanonicalHostName()
				+ " por el puerto " + port_Difusion + " **\n");

		while (true) {

			// Esperamos el tiempo establecido para cumplir con la frecuencia de envio
			try {
				sleep(time);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// Paramos el hilo si se solicita
			synchronized (this) {
				while (pararEstacion) {
					try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

			// Actualizamos los datos meteorologicos de la estacion:
			datosEstacion.actualizaVariables();

			// Creamos el mensaje

			// Sera el puerto que utiliza el servidor para control, un separador, el tipo de
			// serializacion que se esta usando, otro separador y por ultimo el codigo JSON
			// o XML segun corresponda
			String message = Integer.toString(port_Control);
			message += separadorMsg + datosEstacion.getClass().getSimpleName();
			message += separadorMsg + tipoSerializacion + separadorMsg;
			if (tipoSerializacion.equals("JSON")) {
				message += datosEstacion.serializeJSON();
			} else {
				if (tipoSerializacion.equals("XML"))
					message += datosEstacion.serializeXML();
				else {
					System.err.println("Tipo de serializacion no valido");
					System.exit(1);
				}
			}

			// Enviamos el mensaje broadcast
			sendMessageDifusion(message);
		}
	}
}