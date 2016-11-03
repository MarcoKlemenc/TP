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
		menuArchivo.add(agregarItem("Abrir", KeyEvent.VK_1));
		menuArchivo.add(agregarItem("Guardar", KeyEvent.VK_2));
		menuArchivo.add(agregarItem("Exportar", KeyEvent.VK_3));
		menuArchivo.add(agregarItem("Cambiar escala", KeyEvent.VK_4));
		menuArchivo.add(agregarItem("Cambiar orientación", KeyEvent.VK_5));
		return menuBar;
	}

	public void actionPerformed(ActionEvent e) {
		String origen = ((JMenuItem) e.getSource()).getText();
		if (origen == "Abrir") {
			String archivo = Archivo.abrir(".txt");
			if (archivo != null) {
				List<Habitacion> piso = new ArrayList<Habitacion>();
				String escalaBackup = dibujo.getEscala();
				List<Habitacion> pisoBackup = dibujo.getPiso();
				try {
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
						String puntos = st.nextToken();
						StringTokenizer to = new StringTokenizer(puntos, "&");
						while (to.hasMoreTokens()) {
							h.obtenerBaldosa(Integer.parseInt(to.nextToken()), Integer.parseInt(to.nextToken()))
									.cambiarPasar();
						}
					}
					dibujo.setPiso(piso);
					dibujo.repaint();
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(this, "Archivo no válido", "Error", JOptionPane.ERROR_MESSAGE);
					dibujo.setEscala(escalaBackup);
					dibujo.setPiso(pisoBackup);
					dibujo.repaint();
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
					List<Point> noPasar = area.getNoPasar();
					ListIterator<Point> t = noPasar.listIterator();
					while (t.hasNext()) {
						Point p = t.next();
						export += (int) p.getX() + "&" + (int) p.getY() + "&";
					}
					export = export.substring(0, export.length() - 1);
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
			dibujo.repaint();
		} else {
			try {
				dibujo.setOrientacion(Integer.parseInt(JOptionPane.showInputDialog("Introduzca la nueva orientación")));
				dibujo.repaint();
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "La próxima elegí bien la orientación, pelotudo.", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public void itemStateChanged(ItemEvent e) {
	}

	public FrameDemo() {
		setTitle("SACALAMANODAICARAJO");
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
