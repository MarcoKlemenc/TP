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
		File f = new File("dir.ini");
		if (f.exists()) {
			try {
				Scanner s = new Scanner(f);
				fc.setCurrentDirectory(new File(s.nextLine()));
				s.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		fc.setFileFilter(new FileNameExtensionFilter(formato.toUpperCase(), formato));
		return fc.showOpenDialog(fc) == JFileChooser.APPROVE_OPTION ? fc.getSelectedFile().toString() : null;
	}

	public static String guardar(String extension) {
		JFileChooser fc = new JFileChooser();
		String formato = extension.substring(1);
		File f = new File("dir.ini");
		if (f.exists()) {
			f.delete();
		}
		fc.setFileFilter(new FileNameExtensionFilter(formato.toUpperCase(), formato));
		if (fc.showSaveDialog(fc) == JFileChooser.APPROVE_OPTION) {
			try {
				Files.write(Paths.get("dir.ini"), fc.getCurrentDirectory().toString().getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String nombre = fc.getSelectedFile().toString();
			return !nombre.endsWith(extension) ? nombre += extension : nombre;
		}
		return null;
	}

}
