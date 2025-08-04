package otros;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GestorLogs {

	// ATRIBUTO:
	private File file = null;

	// CONSTRUCTOR:
	public GestorLogs(String nameFile) {
		// Creamos el fichero de log y si ya existe vaciamos el contenido
		try {
			file = new File("./data/" + nameFile);
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			bw.close();
		} catch (IOException e) {
			System.err.println("Error al crear el fichero de log '" + nameFile + "'");
		}
	}

	// METODO:
	public synchronized void addContenido(String content) {
		// Concatenamos el contenido al fichero de log
		FileWriter fw;
		try {

			fw = new FileWriter(file.getAbsoluteFile(), true);
			fw.write(content + "\n\n");
			fw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
