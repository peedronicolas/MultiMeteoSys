package otros;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import cliente.Cliente;
import datos.DatosEstacionType1;
import datos.DatosEstacionType2;
import datos.DatosEstacionType3;
import estacion.Estacion;

public class Main {
	public static void main(String[] args) {

		// Será "enp6s0" si estoy por cable y "wlp0s20f3" si estoy por WiFi
		String nombreInterfaz = "enp6s0";

		String ipUnicast = null;
		String ipBroadcast = null;

		// Obtenemos de la interfaz las direcciones IP.
		try {
			Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
			while (en.hasMoreElements()) {
				NetworkInterface ni = en.nextElement();
				if (ni.getName().equals(nombreInterfaz)) {
					ipBroadcast = ni.getInterfaceAddresses().get(1).getBroadcast().getCanonicalHostName();
					ipUnicast = ni.getInterfaceAddresses().get(1).getAddress().getCanonicalHostName();
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}

		// Creamos los servidores y los lanzamos:
		int puertoBroadcast = 9000;

		Estacion e1 = new Estacion(ipBroadcast, puertoBroadcast, ipUnicast, 5000, new DatosEstacionType1("Murcia"));
		Estacion e2 = new Estacion(ipBroadcast, puertoBroadcast, ipUnicast, 5001, new DatosEstacionType2("Valencia"));
		Estacion e3 = new Estacion(ipBroadcast, puertoBroadcast, ipUnicast, 5002, new DatosEstacionType3("Alicante"));

		e1.start();
		e2.start();
		e3.start();

		e2.setTime(6000);
		e3.setTime(7000);

		// Creamos y lanzamos un cliente
		new Cliente(puertoBroadcast).start();
	}
}
