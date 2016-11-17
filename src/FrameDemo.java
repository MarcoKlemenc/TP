import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ListIterator;
import java.util.StringTokenizer;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.imageio.ImageIO;
import javax.swing.JMenuBar;
import javax.swing.JFrame;

public class FrameDemo extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	private Dibujo dibujo = new Dibujo();
	private String nombre = null;

	private void abrir() {
		if (nombre != null) {
			Piso piso = dibujo.getPiso();
			String escalaBackup = dibujo.getEscala();
			int orientacionBackup = dibujo.getOrientacion();
			Camino origBackup = dibujo.getOrig();
			Point trayPBackup = dibujo.getTrayP();
			Habitacion trayHBackup = dibujo.getTrayH();
			Habitacion actualBackup = dibujo.getActual();
			try {
				dibujo.eliminar();
				BufferedReader br = new BufferedReader(new FileReader(nombre));
				String line = br.readLine();
				br.close();
				StringTokenizer s = new StringTokenizer(line, "$");
				StringTokenizer st = new StringTokenizer(s.nextToken(), "|");
				dibujo.setEscala(st.nextToken());
				dibujo.setOrientacion(Integer.parseInt(st.nextToken()));
				while (st.hasMoreTokens()) {
					Habitacion.setIdActual(Integer.parseInt(st.nextToken()));
					Habitacion h = new Habitacion(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()),
							Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
					h.setLado(Integer.parseInt(st.nextToken()));
					h.generarBaldosas();
					dibujo.getPiso().getHabitaciones().add(h);
					StringTokenizer to = new StringTokenizer(st.nextToken(), "&");
					if (to.countTokens() > 1) {
						while (to.hasMoreTokens()) {
							h.obtenerBaldosa(Integer.parseInt(to.nextToken()), Integer.parseInt(to.nextToken()))
									.cambiarPasar();
						}
					}
				}
				if (s.hasMoreTokens()) {
					st = new StringTokenizer(s.nextToken(), "%");
					while (st.hasMoreTokens()) {
						StringTokenizer to = new StringTokenizer(st.nextToken(), "Ç");
						if (to.countTokens() > 1) {
							Trayectoria t = new Trayectoria();
							while (to.hasMoreTokens()) {
								t.agregarBaldosa(dibujo.getPiso().buscarId(Integer.parseInt(to.nextToken())),
										Integer.parseInt(to.nextToken()), Integer.parseInt(to.nextToken()));
							}
							dibujo.getPiso().getTrayectorias().add(t);
						}
					}
				}
				if (s.hasMoreTokens()) {
					st = new StringTokenizer(s.nextToken(), "Ç");
					while (st.hasMoreTokens()) {
						dibujo.getPiso().agregarPuerta(Integer.parseInt(st.nextToken()),
								Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()),
								Integer.parseInt(st.nextToken()),
								dibujo.getPiso().buscarId(Integer.parseInt(st.nextToken())),
								dibujo.getPiso().buscarId(Integer.parseInt(st.nextToken())),
								Boolean.valueOf(st.nextToken()));
					}
				}
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "Archivo no válido", "Error", JOptionPane.ERROR_MESSAGE);
				dibujo.setEscala(escalaBackup);
				try {
					dibujo.setOrientacion(orientacionBackup);
				} catch (Exception ex) {
				}
				dibujo.setPiso(piso);
				dibujo.setTrayP(trayPBackup);
				dibujo.setTrayH(trayHBackup);
				dibujo.setOrig(origBackup);
				dibujo.setActual(actualBackup);
			}
		}
	}

	private void guardar() {
		if (nombre != null) {
			ListIterator<Habitacion> i = dibujo.getPiso().getHabitaciones().listIterator();
			String export = dibujo.getEscala() + "|" + dibujo.getOrientacion() + "|";
			while (i.hasNext()) {
				Habitacion area = i.next();
				export += area.toString();
				ListIterator<Point> it = area.getNoPasar().listIterator();
				if (!it.hasNext()) {
					export += "!";
				}
				while (it.hasNext()) {
					Point p = it.next();
					export += (int) p.getX() + "&" + (int) p.getY() + "&";
				}
				export += "|";
			}
			export += "$";
			ListIterator<Trayectoria> it = dibujo.getPiso().getTrayectorias().listIterator();
			if (!it.hasNext()) {
				export += "!";
			} else {
				while (it.hasNext()) {
					Trayectoria t = it.next();
					ListIterator<Camino> ite = t.getCamino().listIterator();
					while (ite.hasNext()) {
						export += ite.next().toString();
					}
					export += "%";
				}
			}
			export += "$";
			ListIterator<Puerta> ite = dibujo.getPiso().getPuertas().listIterator();
			if (!ite.hasNext()) {
				export += "!";
			} else {
				while (ite.hasNext()) {
					export += ite.next().toString();
				}
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
		String origen = ((JMenuItem) e.getSource()).getText();
		if (origen == "Nuevo") {
			dibujo.eliminar();
		} else if (origen == "Abrir") {
			nombre = Archivo.abrir(".txt");
			abrir();
		} else if (origen == "Guardar") {
			if (nombre == null) {
				nombre = Archivo.guardar(".txt");
			}
			guardar();
		} else if (origen == "Guardar como") {
			nombre = Archivo.guardar(".txt");
			guardar();
		} else if (origen == "Exportar") {
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
		} else if (origen == "Escala") {
			dibujo.setEscala(JOptionPane.showInputDialog("Introduzca la nueva escala"));
		} else if (origen == "Orientación") {
			String texto = null;
			try {
				texto = JOptionPane.showInputDialog("Introduzca la nueva orientación");
				dibujo.setOrientacion(Integer.parseInt(texto));
			} catch (Exception ex) {
				if (texto != null) {
					JOptionPane.showMessageDialog(this, "Orientación inválida", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		} else {
			dibujo.cambiarModo();
		}
		dibujo.repaint();
	}

	public FrameDemo() {
		setTitle("CUTUCUCHILLO");
		setJMenuBar(this.barraMenu());
		setSize(800, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		add(dibujo, BorderLayout.CENTER);
		setVisible(true);
	}

	public static void main(String[] args) {
		new FrameDemo();
	}
}
