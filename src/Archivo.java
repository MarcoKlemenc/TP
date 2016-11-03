import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Archivo {

	public static String abrir(String extension) {
		JFileChooser fc = new JFileChooser();
		String formato = extension.substring(1);
		fc.setFileFilter(new FileNameExtensionFilter(formato.toUpperCase(), formato));
		return fc.showOpenDialog(fc) == JFileChooser.APPROVE_OPTION ? fc.getSelectedFile().toString() : null;
	}

	public static String guardar(String extension) {
		JFileChooser fc = new JFileChooser();
		String formato = extension.substring(1);
		fc.setFileFilter(new FileNameExtensionFilter(formato.toUpperCase(), formato));
		if (fc.showSaveDialog(fc) == JFileChooser.APPROVE_OPTION) {
			String nombre = fc.getSelectedFile().toString();
			return !nombre.endsWith(extension) ? nombre += extension : nombre;
		}
		return null;
	}

}
