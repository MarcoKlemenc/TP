import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;

class Dibujo extends JComponent {

	private static final long serialVersionUID = 1L;
	private Piso piso = new Piso();
	private Point inicio, fin, trayP;
	private int desvioX, desvioY, orientacion;
	private Habitacion actual, temp, trayH, seleccionada;
	private Camino orig, dest;
	private Puerta pActual;
	private String escala = "5m";
	private int modo;
	private String[] modos;

	public void cambiarModo() {
		modo += (modo == 2) ? -2 : 1;
	}

	private boolean aux(Habitacion h) {
		return actual.getX() + actual.getLargo() - h.getX() < actual.getY() + actual.getAlto() - h.getY();
	}

	public Dibujo() {

		modos = new String[3];
		modos[0] = "habitación";
		modos[1] = "trayectoria";
		modos[2] = "obstáculo";
		this.setFocusable(true);

		this.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {
				List<Habitacion> l = piso.contiene(e);
				if (seleccionada != null && l.size() == 1 && l.get(0) == seleccionada) {
					piso.eliminarHabitacion(l.get(0));
					seleccionada = null;
					repaint();
					return;
				}
				seleccionada = null;
				Puerta p = piso.contieneP(e);
				if (p != null) {
					piso.eliminarPuerta(p);
				} else if (l.size() >= 1) {
					for (Habitacion h1 : piso.getHabitaciones()) {
						for (Habitacion h2 : piso.getHabitaciones()) {
							if ((piso.contienePuertaV(h1, h2, e) || piso.contienePuertaV(h2, h1, e))
									&& h1.intersecta(h2)) {
								int x = Math.min(h1.getX(), h2.getX());
								int largo = Math.min(h1.getX(), h2.getX()) == h1.getX() ? h1.getLargo() : h2.getLargo();
								piso.agregarPuerta(x + largo - 3, e.getY(), 7, 30, h1, h2, true);
								return;
							} else if ((piso.contienePuertaH(h1, h2, e) || piso.contienePuertaH(h2, h1, e))
									&& h1.intersecta(h2)) {
								int y = Math.min(h1.getY(), h2.getY());
								int alto = Math.min(h1.getY(), h2.getY()) == h1.getY() ? h1.getAlto() : h2.getAlto();
								piso.agregarPuerta(e.getX(), y + alto - 3, 30, 7, h1, h2, false);
								return;
							}
						}
					}
					actual = l.get(0);
					Baldosa b = actual.contieneB2(e);
					if (modo == 0) {
						seleccionada = l.get(0);
					} else if (modo == 1) {
						if (orig != null && dest == null && b.isPasar()) {
							dest = new Camino(piso.baldosaDentro(b), b.getCoordenadas());
							if (!orig.equals(dest)) {
								piso.generarTrayectoria(orig, dest);
							}
							orig = null;
							trayP = null;
							trayH = null;
						} else if (!piso.eliminarTrayectoria(e, b) && b.isPasar()) {
							orig = new Camino(piso.baldosaDentro(b), b.getCoordenadas());
							trayP = new Point(b.getFila(), b.getColumna());
							trayH = actual;
						}
						dest = null;
					} else {
						for (Trayectoria t : piso.getTrayectorias()) {
							for (Camino c : t.getCamino()) {
								if (c.getHabitacion() == actual && c.getPunto().equals(b.getCoordenadas())) {
									return;
								}
							}
						}
						if (b.getCoordenadas().equals(trayP)) {
							orig = null;
							trayP = null;
							trayH = null;
						}
						b.cambiarPasar();
					}
				}
				repaint();
			}

			public void mousePressed(MouseEvent e) {
				List<Habitacion> l = piso.contiene(e);
				if (!(l.size() == 1 && l.get(0) == seleccionada)) {
					seleccionada = null;
					actual = null;
				}
				pActual = piso.contieneP(e);
				if (pActual == null) {
					if (l.size() == 1) {
						actual = l.get(0);
						desvioX = e.getX() - actual.getX();
						desvioY = e.getY() - actual.getY();
					} else if (l.size() == 0) {
						inicio = new Point(e.getX(), e.getY());
					}
				}
				repaint();
			}

			public void mouseReleased(MouseEvent e) {
				if (actual == null && temp != null && temp.getLargo() >= 2 && temp.getAlto() >= 2) {
					Habitacion h = new Habitacion(temp.getX(), temp.getY(), temp.getLargo(), temp.getAlto());
					if (piso.cruza(h).size() == 0) {
						piso.getHabitaciones().add(h);
						h.setLado(Math.min(h.getAlto(), h.getLargo()) / 4 + 1);
						h.generarBaldosas();
					}
				}
				inicio = null;
				fin = null;
				actual = null;
				temp = null;
				repaint();
			}
		});
		this.addMouseMotionListener(new MouseMotionAdapter() {

			public void mouseDragged(MouseEvent e) {
				if (pActual != null) {
					Habitacion h1 = pActual.getH1();
					Habitacion h2 = pActual.getH2();
					if (pActual.isVertical()) {
						int alto = e.getY() - pActual.getY();
						alto = Math.max(2, alto);
						alto = Math.min(alto,
								Math.min(h1.getY() + h1.getAlto(), h2.getY() + h2.getAlto()) - pActual.getY());
						pActual.setAlto(alto);
					} else {
						int largo = e.getX() - pActual.getX();
						largo = Math.max(2, largo);
						largo = Math.min(largo,
								Math.min(h1.getX() + h1.getLargo(), h2.getX() + h2.getLargo()) - pActual.getX());
						pActual.setLargo(largo);
					}
				}
				if (actual == null) {
					fin = new Point(e.getX(), e.getY());
				} else {
					for (Puerta pu : piso.getPuertas()) {
						if ((pu.getH1() == actual || pu.getH2() == actual)) {
							actual = null;
							return;
						}
					}
					int x = e.getX() - actual.getX();
					int y = e.getY() - actual.getY();
					List<Habitacion> l = piso.cruza(actual);
					if (seleccionada != null) {
						if (actual.getLargo() <= 2 || actual.getAlto() <= 2) {
							piso.getHabitaciones().remove(actual);
							seleccionada = null;
							repaint();
							return;
						}
						actual.setLargo(x);
						actual.setAlto(y);
						if (l.size() > 0) {
							for (Habitacion h : l) {
								if (actual.intersecta(h.bordeIzq()) && (!actual.intersecta(h.bordeArr()) || aux(h))) {
									actual.setLargo(h.getX() - actual.getX());
								} else if (actual.intersecta(h.bordeArr())
										&& (!(actual.intersecta(h.bordeIzq()) && aux(h)))) {
									actual.setAlto(h.getY() - actual.getY());
								}
							}
						}
					} else {
						x = e.getX() - desvioX;
						y = e.getY() - desvioY;
						if (l.size() == 0) {
							actual.setX(x);
							actual.setY(y);
						} else {
							boolean chocoX = false;
							boolean chocoY = false;
							for (Habitacion h : l) {
								if (!chocoX) {
									if (actual.intersecta(h.bordeDer())) {
										actual.setX(Math.max(h.getX() + h.getLargo(), x));
										chocoX = true;
									} else if (actual.intersecta(h.bordeIzq())) {
										actual.setX(Math.min(h.getX() - actual.getLargo(), x));
										chocoX = true;
									} else {
										actual.setX(x);
									}
								}
								if (!chocoY) {
									if (actual.intersecta(h.bordeAba())) {
										actual.setY(Math.max(h.getY() + h.getAlto(), y));
										chocoY = true;
									} else if (actual.intersecta(h.bordeArr())) {
										actual.setY(Math.min(h.getY() - actual.getAlto(), y));
										chocoY = true;
									} else {
										actual.setY(y);
									}
								}
							}
						}

					}
					actual.generarBaldosas();
				}
				repaint();
			}
		});

		this.addMouseWheelListener(new MouseWheelListener() {

			public void mouseWheelMoved(MouseWheelEvent e) {
				if (seleccionada != null) {
					int lado = seleccionada.getLado();
					lado += e.getWheelRotation();
					lado = Math.max(2, lado);
					lado = Math.min(lado, Math.min(seleccionada.getLargo(), seleccionada.getAlto()));
					seleccionada.setLado(lado);
					seleccionada.generarBaldosas();
				}
				repaint();
			}

		});
	}

	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		int orientacionVieja = orientacion;
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.drawString(String.valueOf(orientacion), getWidth() / 2, 15);
		orientacion += (orientacion >= 270) ? -270 : 90;
		g2.drawString(String.valueOf(orientacion), getWidth() - 20, getHeight() / 2 - 5);
		orientacion += (orientacion >= 270) ? -270 : 90;
		g2.drawString(String.valueOf(orientacion), getWidth() / 2, getHeight() - 5);
		orientacion += (orientacion >= 270) ? -270 : 90;
		g2.drawString(String.valueOf(orientacion), 0, getHeight() / 2 - 5);
		orientacion = orientacionVieja;
		g2.setPaint(Color.WHITE);
		g2.fillRect(20, 20, getWidth() - 40, getHeight() - 40);
		g2.setPaint(new Color(238, 238, 238));
		for (int i = -1; i < getWidth(); i += 100) {
			for (int j = -1; j < getHeight(); j += 100) {
				g2.draw(new Rectangle2D.Float(i, j, 100, 100));
			}
		}
		Color[] colores = { Color.CYAN, Color.BLUE, Color.GREEN, Color.ORANGE, Color.RED, Color.MAGENTA, Color.PINK,
				Color.YELLOW };
		int col = -1;
		for (Habitacion h : piso.getHabitaciones()) {
			g2.setPaint(new Color(160, 160, 160));
			for (Baldosa b : h.getBaldosas()) {
				Rectangle2D r = b.getForma();
				g2.draw(r);
				if (!b.isPasar()) {
					g2.fill(r);
				}
			}
			g2.setPaint(Color.BLACK);
			g2.draw(h.getForma());
		}
		List<Trayectoria> tt = new ArrayList<Trayectoria>();
		for (Trayectoria t : piso.getTrayectorias()) {
			g2.setPaint(colores[col += (col == 7) ? -7 : 1]);
			for (Camino c : t.getCamino()) {
				Point p = c.getPunto();
				Baldosa b = c.getHabitacion().obtenerBaldosa(p.getX(), p.getY());
				if (b != null) {
					g2.fill(b.getInterior());
				} else {
					tt.add(t);
				}
			}
		}
		piso.getTrayectorias().removeAll(tt);
		if (seleccionada != null) {
			g2.setPaint(new Color(0, 192, 0, 128));
			g2.fill(seleccionada.getInterior());
		}
		if (trayP != null && piso.getHabitaciones().contains(trayH)) {
			g2.setPaint(new Color(192, 192, 192, 128));
			Baldosa b = trayH.obtenerBaldosa(trayP.getX(), trayP.getY());
			if (b != null) {
				g2.fill(b.getForma());
			} else {
				trayH = null;
				trayP = null;
			}
		}
		for (Puerta p : piso.getPuertas()) {
			g2.setPaint(Color.BLACK);
			g2.fill(p.getForma());
		}
		if (inicio != null && fin != null) {
			g2.setPaint(Color.BLACK);
			temp = new Habitacion(Math.min(inicio.x, fin.x), Math.min(inicio.y, fin.y), Math.abs(inicio.x - fin.x),
					Math.abs(inicio.y - fin.y));
			g2.draw(temp.getForma());
			g2.setPaint(piso.cruza(temp).size() != 0 ? new Color(255, 0, 0, 64) : new Color(0, 255, 0, 32));
			g2.fill(temp.getForma());
		}
		g2.setPaint(Color.BLACK);
		g2.drawLine(20, 20, 99, 20);
		g2.drawString(escala, 20, 18);
		g2.drawString("Modo actual: " + modos[modo], 20, getHeight() - 5);
	}

	public void eliminar() {
		piso = new Piso();
		orientacion = 0;
		escala = "5m";
		trayP = null;
		trayH = null;
		orig = null;
		actual = null;
		seleccionada = null;
	}

	public String getEscala() {
		return escala;
	}

	public void setEscala(String escala) {
		this.escala = escala != null ? escala : this.escala;
	}

	public Integer getOrientacion() {
		return orientacion;
	}

	public void setOrientacion(Integer orientacion) throws Exception {
		if (orientacion >= 0 && orientacion <= 359) {
			this.orientacion = orientacion;
		} else {
			throw new Exception();
		}
	}

	public Piso getPiso() {
		return piso;
	}

	public void setPiso(Piso piso) {
		this.piso = piso;
	}

	public Point getTrayP() {
		return trayP;
	}

	public void setTrayP(Point trayP) {
		this.trayP = trayP;
	}

	public Habitacion getActual() {
		return actual;
	}

	public void setActual(Habitacion actual) {
		this.actual = actual;
	}

	public Habitacion getTrayH() {
		return trayH;
	}

	public void setTrayH(Habitacion trayH) {
		this.trayH = trayH;
	}

	public Camino getOrig() {
		return orig;
	}

	public void setOrig(Camino orig) {
		this.orig = orig;
	}

}
