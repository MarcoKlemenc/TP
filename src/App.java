import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.imageio.ImageIO;
import javax.swing.JMenuBar;
import javax.swing.JFrame;

public class App extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	private Dibujo dibujo = new Dibujo();
	private String nombre = null;

	private void abrir() {
		if (nombre != null) {
			Piso piso = dibujo.getPiso();
			int escalaBackup = dibujo.getEscala();
			String unidadBackup = dibujo.getUnidad();
			int orientacionBackup = dibujo.getOrientacion();
			try {
				dibujo.eliminar();
				BufferedReader br = new BufferedReader(new FileReader(nombre));
				String line = br.readLine();
				br.close();
				StringTokenizer s = new StringTokenizer(line, "$");
				StringTokenizer st = new StringTokenizer(s.nextToken(), "|");
				dibujo.setEscala(Integer.parseInt(st.nextToken()));
				dibujo.setUnidad(st.nextToken());
				dibujo.setOrientacion(Integer.parseInt(st.nextToken()));
				while (st.hasMoreTokens()) {
					dibujo.getPiso().agregarHabitacion(new Habitacion(st));
				}
				if (s.hasMoreTokens()) {
					st = new StringTokenizer(s.nextToken(), "%");
					while (st.hasMoreTokens()) {
						StringTokenizer to = new StringTokenizer(st.nextToken(), "Ç");
						if (to.countTokens() > 1) {
							while (to.hasMoreTokens()) {
								dibujo.getPiso().getTrayectorias().add(new Trayectoria(to, dibujo));
							}
						}
					}
				}
				if (s.hasMoreTokens()) {
					st = new StringTokenizer(s.nextToken(), "Ç");
					while (st.hasMoreTokens()) {
						dibujo.getPiso().agregarPuerta(st);
					}
				}
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "Archivo no válido", "Error", JOptionPane.ERROR_MESSAGE);
				try {
					dibujo.setEscala(escalaBackup);
					dibujo.setOrientacion(orientacionBackup);
				} catch (Exception ex) {
				}
				dibujo.setUnidad(unidadBackup);
				dibujo.setPiso(piso);
			}
		}
	}

	private void guardar() {
		if (nombre != null) {
			String export = dibujo.getEscala() + "|" + dibujo.getUnidad() + "|" + dibujo.getOrientacion() + "|";
			for (Habitacion h : dibujo.getPiso().getHabitaciones()) {
				export += h.toString();
			}
			export += "$";
			List<Trayectoria> trayectorias = dibujo.getPiso().getTrayectorias();
			if (trayectorias.isEmpty()) {
				export += "!";
			}
			for (Trayectoria t : trayectorias) {
				export += t.toString();
			}
			export += "$";
			List<Puerta> puertas = dibujo.getPiso().getPuertas();
			if (puertas.isEmpty()) {
				export += "!";
			}
			for (Puerta p : puertas) {
				export += p.toString();
			}
			try {
				Files.write(Paths.get(nombre), export.substring(0, export.length() - 1).getBytes());
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "Acceso denegado", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private JMenuItem agregarItem(String texto) {
		JMenuItem item = new JMenuItem(texto);
		item.addActionListener(this);
		return item;
	}

	private JMenuBar barraMenu() {
		JMenuBar barra = new JMenuBar();
		barra.add(agregarItem("Nuevo"));
		barra.add(agregarItem("Abrir"));
		barra.add(agregarItem("Guardar"));
		barra.add(agregarItem("Guardar como"));
		barra.add(agregarItem("Exportar"));
		barra.add(agregarItem("Escala"));
		barra.add(agregarItem("Orientación"));
		barra.add(agregarItem("Modo"));
		return barra;
	}

	public void actionPerformed(ActionEvent e) {
		String texto = null;
		String opcion = ((JMenuItem) e.getSource()).getText();
		if (opcion == "Nuevo") {
			dibujo.eliminar();
		} else if (opcion == "Abrir") {
			nombre = Archivo.abrir(".txt");
			abrir();
		} else if (opcion == "Guardar") {
			if (nombre == null) {
				nombre = Archivo.guardar(".txt");
			}
			guardar();
		} else if (opcion == "Guardar como") {
			nombre = Archivo.guardar(".txt");
			guardar();
		} else if (opcion == "Exportar") {
			String archivo = Archivo.guardar(".jpg");
			if (archivo != null) {
				BufferedImage image = new BufferedImage(getContentPane().getWidth(), getContentPane().getHeight(),
						BufferedImage.TYPE_INT_RGB);
				getContentPane().print(image.getGraphics());
				try {
					ImageIO.write(image, "jpeg", new File(archivo));
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(this, "Acceso denegado", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		} else if (opcion == "Escala") {
			try {
				texto = JOptionPane.showInputDialog("Introduzca la nueva escala");
				StringTokenizer st = new StringTokenizer(texto, " ");
				if (st.countTokens() == 2) {
					dibujo.setEscala(Integer.parseInt(st.nextToken()));
					dibujo.setUnidad(st.nextToken());
				} else {
					throw new Exception();
				}
			} catch (Exception ex) {
				if (texto != null) {
					JOptionPane.showMessageDialog(this, "Escala inválida", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		} else if (opcion == "Orientación") {
			try {
				texto = JOptionPane.showInputDialog("Introduzca la nueva orientación");
				dibujo.setOrientacion(Integer.parseInt(texto));
			} catch (Exception ex) {
				if (texto != null) {
					JOptionPane.showMessageDialog(this, "Orientación inválida", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		} else {
			setTitle("Modo actual: " + dibujo.cambiarModo());
		}
		dibujo.repaint();
	}

	private App() {
		setJMenuBar(this.barraMenu());
		setSize(800, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		add(dibujo, BorderLayout.CENTER);
		setTitle("Modo actual: habitación");
		setVisible(true);
	}

	public static void main(String[] args) {
		new App();
	}
}
