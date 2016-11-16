import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ListIterator;
import java.util.StringTokenizer;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.imageio.ImageIO;
import javax.swing.JMenuBar;
import javax.swing.KeyStroke;
import javax.swing.JFrame;

public class FrameDemo extends JFrame implements ActionListener, ItemListener {

	private static final long serialVersionUID = 1L;
	private Dibujo dibujo = new Dibujo();
	private String nombre = null;

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
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "Acceso denegado", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private JMenu agregarMenu(String texto, int atajo) {
		JMenu menu = new JMenu(texto);
		menu.setMnemonic(atajo);
		return menu;
	}

	private JMenuItem agregarItem(String texto, int atajo) {
		JMenuItem item = new JMenuItem(texto);
		item.setAccelerator(KeyStroke.getKeyStroke(atajo, ActionEvent.ALT_MASK));
		item.addActionListener(this);
		return item;
	}

	private JMenuBar barraMenu() {
		JMenuBar barra = new JMenuBar();
		JMenu menuArchivo = agregarMenu("Archivo", KeyEvent.VK_A);
		JMenu menuEditar = agregarMenu("Editar", KeyEvent.VK_E);
		barra.add(menuArchivo);
		barra.add(menuEditar);
		menuArchivo.add(agregarItem("Nuevo", KeyEvent.VK_1));
		menuArchivo.add(agregarItem("Abrir", KeyEvent.VK_2));
		menuArchivo.add(agregarItem("Guardar", KeyEvent.VK_3));
		menuArchivo.add(agregarItem("Guardar como", KeyEvent.VK_4));
		menuArchivo.add(agregarItem("Exportar", KeyEvent.VK_5));
		menuEditar.add(agregarItem("Cambiar escala", KeyEvent.VK_6));
		menuEditar.add(agregarItem("Cambiar orientaci�n", KeyEvent.VK_7));
		return barra;
	}

	public void actionPerformed(ActionEvent e) {
		String origen = ((JMenuItem) e.getSource()).getText();
		if (origen == "Nuevo") {
			dibujo.eliminar();
		} else if (origen == "Abrir") {
			nombre = Archivo.abrir(".txt");
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
						Habitacion h = new Habitacion(Integer.parseInt(st.nextToken()),
								Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()),
								Integer.parseInt(st.nextToken()));
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
							StringTokenizer to = new StringTokenizer(st.nextToken(), "�");
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
						st = new StringTokenizer(s.nextToken(), "�");
						while (st.hasMoreTokens()) {
							dibujo.getPiso().agregarPuerta(Integer.parseInt(st.nextToken()),
									Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()),
									Integer.parseInt(st.nextToken()),
									dibujo.getPiso().buscarId(Integer.parseInt(st.nextToken())),
									dibujo.getPiso().buscarId(Integer.parseInt(st.nextToken())));
						}
					}
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(this, "Archivo no v�lido", "Error", JOptionPane.ERROR_MESSAGE);
					dibujo.setEscala(escalaBackup);
					try {
						dibujo.setOrientacion(orientacionBackup);
					} catch (Exception exc) {
					}
					dibujo.setPiso(piso);
					dibujo.setTrayP(trayPBackup);
					dibujo.setTrayH(trayHBackup);
					dibujo.setOrig(origBackup);
					dibujo.setActual(actualBackup);
				}
			}
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
		} else if (origen == "Cambiar escala") {
			dibujo.setEscala(JOptionPane.showInputDialog("Introduzca la escala nueva"));
		} else {
			String texto = null;
			try {
				texto = JOptionPane.showInputDialog("Introduzca la nueva orientaci�n");
				dibujo.setOrientacion(Integer.parseInt(texto));
			} catch (Exception ex) {
				if (texto != null) {
					JOptionPane.showMessageDialog(this, "Orientaci�n inv�lida", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		dibujo.repaint();
	}

	public void itemStateChanged(ItemEvent e) {
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
