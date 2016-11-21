import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Archivo {

	static JFileChooser fc = new JFileChooser();

	public static String abrir(String extension) {
		inicializar(extension);
		int a = fc.showOpenDialog(fc);
		guardarDir();
		return a == JFileChooser.APPROVE_OPTION ? fc.getSelectedFile().toString() : null;
	}

	public static String guardar(String extension) {
		inicializar(extension);
		int g = fc.showSaveDialog(fc);
		guardarDir();
		if (g == JFileChooser.APPROVE_OPTION) {
			String nombre = fc.getSelectedFile().toString();
			return !nombre.endsWith(extension) ? nombre += extension : nombre;
		}
		return null;
	}

	private static void guardarDir() {
		try {
			Files.write(Paths.get("dir.ini"), fc.getCurrentDirectory().toString().getBytes());
		} catch (Exception e) {
		}
	}
	
	private static void inicializar(String extension) {
		String formato = extension.substring(1);
		fc.setFileFilter(new FileNameExtensionFilter(formato.toUpperCase(), formato));
		try {
			Scanner s = new Scanner(new File("dir.ini"));
			fc.setCurrentDirectory(new File(s.nextLine()));
			s.close();
		} catch (Exception e) {
		}
	}
}
