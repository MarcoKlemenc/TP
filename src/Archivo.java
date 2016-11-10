import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Archivo {

	public static String abrir(String extension) {
		JFileChooser fc = new JFileChooser();
		String formato = extension.substring(1);
		fc.setFileFilter(new FileNameExtensionFilter(formato.toUpperCase(), formato));
		try {
			Scanner s = new Scanner(new File("dir.ini"));
			fc.setCurrentDirectory(new File(s.nextLine()));
			s.close();
		} catch (FileNotFoundException e) {
		}
		return fc.showOpenDialog(fc) == JFileChooser.APPROVE_OPTION ? fc.getSelectedFile().toString() : null;
	}

	public static String guardar(String extension) {
		JFileChooser fc = new JFileChooser();
		String formato = extension.substring(1);
		fc.setFileFilter(new FileNameExtensionFilter(formato.toUpperCase(), formato));
		int d = fc.showSaveDialog(fc);
		try {
			Files.write(Paths.get("dir.ini"), fc.getCurrentDirectory().toString().getBytes());
		} catch (IOException e) {
		}
		if (d == JFileChooser.APPROVE_OPTION) {
			String nombre = fc.getSelectedFile().toString();
			return !nombre.endsWith(extension) ? nombre += extension : nombre;
		}
		return null;
	}

}
