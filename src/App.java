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

	public static void main(String[] args) {
		new App();
	}

	private App() {
		setJMenuBar(this.barraMenu());
		setSize(800, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		add(dibujo, BorderLayout.CENTER);
		setTitle("Modo actual: habitación");
		setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		String texto = null;
		switch (((JMenuItem) e.getSource()).getText()) {
		case "Nuevo":
			dibujo.eliminar();
			break;
		case "Abrir":
			nombre = Archivo.abrir(".txt");
			abrir();
			break;
		case "Guardar":
			if (nombre == null) {
				nombre = Archivo.guardar(".txt");
			}
			guardar();
			break;
		case "Guardar como":
			nombre = Archivo.guardar(".txt");
			guardar();
			break;
		case "Exportar":
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
			break;
		case "Escala":
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
			break;
		case "Orientación":
			try {
				texto = JOptionPane.showInputDialog("Introduzca la nueva orientación");
				dibujo.setOrientacion(Integer.parseInt(texto));
			} catch (Exception ex) {
				if (texto != null) {
					JOptionPane.showMessageDialog(this, "Orientación inválida", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
			break;
		default:
			setTitle("Modo actual: " + dibujo.cambiarModo());
			break;
		}
		dibujo.repaint();
	}

	private void abrir() {
		if (nombre != null) {
			Piso piso = dibujo.getPiso();
			int escala = dibujo.getEscala();
			String unidad = dibujo.getUnidad();
			int orientacion = dibujo.getOrientacion();
			try {
				dibujo.eliminar();
				BufferedReader r = new BufferedReader(new FileReader(nombre));
				String linea = r.readLine();
				r.close();
				StringTokenizer t1 = new StringTokenizer(linea, "$");
				StringTokenizer t2 = new StringTokenizer(t1.nextToken(), "|");
				dibujo.setEscala(Integer.parseInt(t2.nextToken()));
				dibujo.setUnidad(t2.nextToken());
				dibujo.setOrientacion(Integer.parseInt(t2.nextToken()));
				while (t2.hasMoreTokens()) {
					dibujo.getPiso().agregarHabitacion(new Habitacion(t2));
				}
				if (t1.hasMoreTokens()) {
					t2 = new StringTokenizer(t1.nextToken(), "%");
					while (t2.hasMoreTokens()) {
						StringTokenizer t3 = new StringTokenizer(t2.nextToken(), "Ç");
						if (t3.countTokens() > 1) {
							while (t3.hasMoreTokens()) {
								dibujo.getPiso().getTrayectorias().add(new Trayectoria(t3, dibujo));
							}
						}
					}
				}
				if (t1.hasMoreTokens()) {
					t2 = new StringTokenizer(t1.nextToken(), "Ç");
					while (t2.hasMoreTokens()) {
						dibujo.getPiso().agregarPuerta(t2);
					}
				}
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "Archivo no válido", "Error", JOptionPane.ERROR_MESSAGE);
				try {
					dibujo.setEscala(escala);
					dibujo.setOrientacion(orientacion);
				} catch (Exception ex) {
				}
				dibujo.setUnidad(unidad);
				dibujo.setPiso(piso);
			}
		}
	}

	private void guardar() {
		if (nombre != null) {
			String linea = dibujo.getEscala() + "|" + dibujo.getUnidad() + "|" + dibujo.getOrientacion() + "|";
			for (Habitacion h : dibujo.getPiso().getHabitaciones()) {
				linea += h.toString();
			}
			linea += "$";
			List<Trayectoria> trayectorias = dibujo.getPiso().getTrayectorias();
			if (trayectorias.isEmpty()) {
				linea += "!";
			}
			for (Trayectoria t : trayectorias) {
				linea += t.toString();
			}
			linea += "$";
			List<Puerta> puertas = dibujo.getPiso().getPuertas();
			if (puertas.isEmpty()) {
				linea += "!";
			}
			for (Puerta p : puertas) {
				linea += p.toString();
			}
			try {
				Files.write(Paths.get(nombre), linea.substring(0, linea.length() - 1).getBytes());
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
}
