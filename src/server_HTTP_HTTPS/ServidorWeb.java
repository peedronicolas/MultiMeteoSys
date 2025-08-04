package server_HTTP_HTTPS;

import java.net.*;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.util.HashMap;

import javax.net.ServerSocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.TrustManagerFactory;

import cliente.ClientController;
import datos.DatosEstacion;

import java.io.*;

// Clase principal que lanza en paralelo un servidor HTTP y otro HTTPS
public class ServidorWeb {

	public void lanzar(HashMap<String, DatosEstacion> registro, ClientController clientController) {

		ServidorHTTP serverHTTP;
		try {
			serverHTTP = new ServidorHTTP(8080, registro, clientController);
			serverHTTP.start();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		ServidorHTTPS serverHTTPS;
		try {
			serverHTTPS = new ServidorHTTPS(4433, registro, clientController);
			serverHTTPS.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class ServidorHTTP extends Thread {

	// Inicializacion:
	private ServerSocket socket = null;
	private Socket cliente = null;
	private HashMap<String, DatosEstacion> registro;
	private ClientController clientController;

	// Constructor:
	public ServidorHTTP(int PORT, HashMap<String, DatosEstacion> registro, ClientController clientController)
			throws Exception {

		socket = new ServerSocket(PORT);
		this.registro = registro;
		this.clientController = clientController;
		System.out.println("*************************************************");
		System.out.println("** The server HTTP is running on the port " + PORT + " **");
		System.out.println("*************************************************");
		System.out.println();
	}

	// Run:
	@Override
	public void run() {
		while (true) {
			try {

				// Aceptamos peticiones del cliente:
				cliente = socket.accept();
				// Creamos un nuevo manejador de peticion para el nuevo cliente:
				new GestorPeticionServer(cliente, registro, clientController).start();

			} catch (IOException e) {
				System.out.println(e);
			}
		}
	}
}

class ServidorHTTPS extends Thread {

	// Inicializacion:
	private SSLServerSocket socket = null;
	private Socket cliente = null;
	private HashMap<String, DatosEstacion> registro;
	private ClientController clientController;

	private char[] keystorepwd = "alumno".toCharArray();
	private final String keystore = "certs/servidor.ks";
	private final String cacert = "certs/cacert.pem";

	// Constructor:
	public ServidorHTTPS(int PORT, HashMap<String, DatosEstacion> registro, ClientController clientController)
			throws Exception {

		this.registro = registro;
		this.clientController = clientController;
		KeyStore ks = KeyStore.getInstance("JKS");
		ks.load(new FileInputStream(keystore), keystorepwd);
		KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
		kmf.init(ks, keystorepwd);
		KeyStore ksTrust = KeyStore.getInstance("PKCS12");
		ksTrust.load(null, null);

		ksTrust.setCertificateEntry("ca",
				CertificateFactory.getInstance("X509").generateCertificate(new FileInputStream(cacert)));

		TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
		tmf.init(ksTrust);
		SSLContext sslctx = SSLContext.getInstance("TLS");
		sslctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
		ServerSocketFactory ssf = sslctx.getServerSocketFactory();
		socket = (SSLServerSocket) ssf.createServerSocket(PORT);
		socket.setNeedClientAuth(true);

		System.out.println("**************************************************");
		System.out.println("** The server HTTPS is running on the port " + PORT + " **");
		System.out.println("**************************************************");
		System.out.println();
	}

	// Run:
	@Override
	public void run() {
		try {

			while (true) {
				// Aceptamos peticiones del cliente:
				cliente = socket.accept();
				// Creamos un nuevo manejador de peticion para el nuevo cliente:
				new GestorPeticionServer(cliente, registro, clientController).start();
			}

		} catch (IOException e) {
			System.err.println(e);
		}
	}
}