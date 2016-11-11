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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
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

	private JMenu agregarMenu(String texto, int atajo) {
		JMenu menu = new JMenu(texto);
		menu.setMnemonic(atajo);
		return menu;
	}

	private JMenuItem agregarItem(String texto, int atajo) {
		JMenuItem menuItem = new JMenuItem(texto);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(atajo, ActionEvent.ALT_MASK));
		menuItem.addActionListener(this);
		return menuItem;
	}

	public JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menuArchivo = agregarMenu("Archivo", KeyEvent.VK_A);
		JMenu menuObstaculo = agregarMenu("Obstáculo", KeyEvent.VK_O);
		JMenu menuTrayectoria = agregarMenu("Trayectoria", KeyEvent.VK_T);
		menuBar.add(menuArchivo);
		menuBar.add(menuObstaculo);
		menuBar.add(menuTrayectoria);
		menuArchivo.add(agregarItem("Nuevo", KeyEvent.VK_1));
		menuArchivo.add(agregarItem("Abrir", KeyEvent.VK_2));
		menuArchivo.add(agregarItem("Guardar", KeyEvent.VK_3));
		menuArchivo.add(agregarItem("Exportar", KeyEvent.VK_4));
		menuArchivo.add(agregarItem("Cambiar escala", KeyEvent.VK_5));
		menuArchivo.add(agregarItem("Cambiar orientación", KeyEvent.VK_6));
		return menuBar;
	}

	public void actionPerformed(ActionEvent e) {
		String origen = ((JMenuItem) e.getSource()).getText();
		if (origen == "Nuevo") {
			// faltan cosas
			try {
				dibujo.setOrientacion(0);
			} catch (Exception e1) {
			}
			dibujo.setEscala("5m");
			dibujo.setPiso(new ArrayList<Habitacion>());
			dibujo.setPuertas(new ArrayList<Puerta>());
			dibujo.setTrayectorias(new ArrayList<Trayectoria>());
		} else if (origen == "Abrir") {
			String archivo = Archivo.abrir(".txt");
			if (archivo != null) {
				String escalaBackup = dibujo.getEscala();
				List<Habitacion> pisoBackup = dibujo.getPiso();
				try {
					List<Habitacion> piso = new ArrayList<Habitacion>();
					BufferedReader br = new BufferedReader(new FileReader(archivo));
					String line = br.readLine();
					br.close();
					StringTokenizer st = new StringTokenizer(line, "|");
					dibujo.setEscala(st.nextToken());
					dibujo.setOrientacion(Integer.parseInt(st.nextToken()));
					while (st.hasMoreTokens()) {
						Habitacion h = new Habitacion(Integer.parseInt(st.nextToken()),
								Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()),
								Integer.parseInt(st.nextToken()));
						h.setLado(Integer.parseInt(st.nextToken()));
						h.generarBaldosas();
						piso.add(h);
						StringTokenizer to = new StringTokenizer(st.nextToken(), "&");
						if (to.countTokens() > 1) {
							while (to.hasMoreTokens()) {
								h.obtenerBaldosa(Integer.parseInt(to.nextToken()), Integer.parseInt(to.nextToken()))
										.cambiarPasar();
							}
						}
						to = new StringTokenizer(st.nextToken(), "%");
						while (to.hasMoreTokens()) {
							StringTokenizer tok = new StringTokenizer(to.nextToken(), "Ç");
							if (tok.countTokens() > 1) {
								Trayectoria t = new Trayectoria(h);
								while (tok.hasMoreTokens()) {
									t.agregarBaldosa(Integer.parseInt(tok.nextToken()),
											Integer.parseInt(tok.nextToken()));
								}
								dibujo.getTrayectorias().add(t);
							}
						}
					}
					dibujo.setPiso(piso);
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(this, "Archivo no válido", "Error", JOptionPane.ERROR_MESSAGE);
					dibujo.setEscala(escalaBackup);
					dibujo.setPiso(pisoBackup);
				}
			}
		} else if (origen == "Guardar") {
			String archivo = Archivo.guardar(".txt");
			if (archivo != null) {
				ListIterator<Habitacion> i = dibujo.getPiso().listIterator();
				String export = dibujo.getEscala() + "|" + dibujo.getOrientacion() + "|";
				while (i.hasNext()) {
					Habitacion area = i.next();
					export += area.getX() + "|" + area.getY() + "|" + area.getLargo() + "|" + area.getAlto() + "|"
							+ area.getLado() + "|";
					ListIterator<Point> it = area.getNoPasar().listIterator();
					if (!it.hasNext()) {
						export += "!";
					}
					while (it.hasNext()) {
						Point p = it.next();
						export += (int) p.getX() + "&" + (int) p.getY() + "&";
					}
					export += "|";
					ListIterator<Trayectoria> ite = dibujo.getTrayectorias().listIterator();
					if (!ite.hasNext()) {
						export += "!";
					} else {
						while (ite.hasNext()) {
							Trayectoria t = ite.next();
							if (t.getHabitacion() == area) {
								ListIterator<Point> iter = t.getCamino().listIterator();
								while (iter.hasNext()) {
									Point p = iter.next();
									export += (int) p.getX() + "Ç" + (int) p.getY() + "Ç";
								}
								export += "%";
							}
						}
						export = export.substring(0, export.length() - 1);
					}
					export += "|";
				}
				try {
					Files.write(Paths.get(archivo), export.substring(0, export.length() - 1).getBytes());
				} catch (IOException ex) {
					JOptionPane.showMessageDialog(this, "Acceso denegado", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
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
				texto = JOptionPane.showInputDialog("Introduzca la nueva orientación");
				dibujo.setOrientacion(Integer.parseInt(texto));
			} catch (Exception ex) {
				if (texto != null) {
					JOptionPane.showMessageDialog(this, "Orientación inválida", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		dibujo.repaint();
	}

	public void itemStateChanged(ItemEvent e) {
	}

	public FrameDemo() {
		// setTitle("SACALAMANODAICARAJO");
		setJMenuBar(this.createMenuBar());
		setSize(800, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		add(dibujo, BorderLayout.CENTER);
		setVisible(true);
	}

	public static void main(String[] args) {
		new FrameDemo();
	}
}
