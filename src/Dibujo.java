import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

class Dibujo extends JComponent {

	private static final long serialVersionUID = 1L;
	private Piso piso = new Piso();
	private Point inicio, fin, trayP;
	private int desvioX, desvioY, orientacion;
	private Habitacion actual, temp, trayH;
	private Camino orig, dest;
	private boolean shift, ctrl;
	private Puerta pTemp = null;
	private String escala = "5m";

	private boolean contienePuertaV(Habitacion h1, Habitacion h2, MouseEvent e) {
		return h1.contiene(e.getX() - 3, e.getY() - 1) && h2.contiene(e.getX() + 3, e.getY() - 1);
	}

	private boolean contienePuertaH(Habitacion h1, Habitacion h2, MouseEvent e) {
		return h1.contiene(e.getX() - 1, e.getY() - 3) && h2.contiene(e.getX() - 1, e.getY() + 3);
	}

	public Dibujo() {

		this.setFocusable(true);
		this.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.isShiftDown()) {
					shift = true;
				} else if (e.isControlDown()) {
					ctrl = true;
				}
			}

			public void keyReleased(KeyEvent e) {
				shift = false;
				ctrl = false;
			}
		});

		this.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {
				List<Habitacion> l = piso.contiene(e);
				if (SwingUtilities.isLeftMouseButton(e)) {
					Puerta p = piso.contieneP(e);
					if (p != null) {
						piso.eliminarPuerta(p);
						return;
					}
					if (l.size() == 1) {
						actual = l.get(0);
						Baldosa b = actual.contieneB2(e);
						if (orig != null && dest == null) {
							dest = new Camino(piso.baldosaDentro(b), b.getCoordenadas());
							if (!orig.equals(dest)) {
								piso.generarTrayectoria(orig, dest);
							}
							orig = null;
							trayP = null;
							trayH = null;
						} else {
							if (piso.eliminarTrayectoria(e, b)) {
								return;
							}
							orig = new Camino(piso.baldosaDentro(b), b.getCoordenadas());
							trayP = new Point(b.getFila(), b.getColumna());
							trayH = actual;
						}
						dest = null;
					} else if (l.size() == 2 && p == null) {
						Habitacion h1 = l.get(0);
						Habitacion h2 = l.get(1);
						if (contienePuertaV(h1, h2, e) || contienePuertaV(h2, h1, e)) {
							piso.agregarPuerta(e.getX() - 3, e.getY(), 7, 30, h1, h2);
						} else if (contienePuertaH(h1, h2, e) || contienePuertaH(h2, h1, e)) {
							piso.agregarPuerta(e.getX(), e.getY() - 3, 30, 7, h1, h2);
						}
					}
				}
			}

			public void mousePressed(MouseEvent e) {
				List<Habitacion> l = piso.contiene(e);
				if (SwingUtilities.isLeftMouseButton(e)) {
					if (l.size() == 1) {
						actual = l.get(0);
						desvioX = e.getX() - actual.getX();
						desvioY = e.getY() - actual.getY();
					} else if (l.size() == 0) {
						inicio = new Point(e.getX(), e.getY());
					}
				} else if (SwingUtilities.isRightMouseButton(e)) {
					if (l.size() == 1) {
						piso.eliminarHabitacion(l.get(0));
					}
				} else if (SwingUtilities.isMiddleMouseButton(e)) {
					if (l.size() == 1) {
						actual = l.get(0);
						actual.contieneB2(e).cambiarPasar();
					}
				}
				repaint();
			}

			public void mouseReleased(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e) && actual == null && temp != null && temp.getLargo() >= 2
						&& temp.getAlto() >= 2) {
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

			public void mouseMoved(MouseEvent e) {
				List<Habitacion> l = piso.contiene(e);
				if (l.size() == 2) {
					Habitacion h1 = l.get(0);
					Habitacion h2 = l.get(1);
					if (contienePuertaV(h1, h2, e) || contienePuertaV(h2, h1, e)) {
						pTemp = new Puerta(e.getX() - 3, e.getY(), 7, 30, h1, h2);
					} else if (contienePuertaH(h1, h2, e) || contienePuertaH(h2, h1, e)) {
						pTemp = new Puerta(e.getX(), e.getY() - 3, 30, 7, h1, h2);
					}
				} else {
					pTemp = null;
				}
				repaint();
			}

			public void mouseDragged(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e)) {
					Puerta p = piso.contieneP(e);
					if (p != null) {
						p.setAlto(e.getY() - p.getY());
					}
					if (actual == null) {
						fin = new Point(e.getX(), e.getY());
					} else {
						for (Puerta pu : piso.getPuertas()) {
							if ((pu.getH1() == actual || pu.getH2() == actual) && !ctrl) {
								actual = null;
								return;
							}
						}
						int x = e.getX() - actual.getX();
						int y = e.getY() - actual.getY();
						List<Habitacion> l = piso.cruza(actual);
						if (shift && !ctrl) {
							if (actual.getLargo() <= 2 || actual.getAlto() <= 2) {
								piso.getHabitaciones().remove(actual);
								repaint();
								return;
							}
							actual.setLargo(x);
							actual.setAlto(y);
							if (l.size() > 0) {
								for (Habitacion h : l) {
									if (actual.intersecta(h.bordeIzq())
											&& (!actual.intersecta(h.bordeArr()) || actual.getX() + actual.getLargo()
													- h.getX() < actual.getY() + actual.getAlto() - h.getY())) {
										actual.setLargo(h.getX() - actual.getX());
									} else if (actual.intersecta(h.bordeArr())
											&& (!actual.intersecta(h.bordeIzq()) || actual.getX() + actual.getLargo()
													- h.getX() >= actual.getY() + actual.getAlto() - h.getY())) {
										actual.setAlto(h.getY() - actual.getY());
									}
								}
							}
						} else if (!shift && ctrl) {
							actual.setLado(Math.max(2, x));
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
		Set<Trayectoria> tr = new HashSet<Trayectoria>();
		for (Trayectoria t : piso.getTrayectorias()) {
			g2.setPaint(colores[col += (col == 7) ? -7 : 1]);
			for (Camino c : t.getCamino()) {
				Point p = c.getPunto();
				Baldosa b = c.getHabitacion().obtenerBaldosa(p.getX(), p.getY());
				if (b != null) {
					g2.fill(b.getInterior());
				} else {
					tr.add(t);
				}
			}
		}
		piso.getTrayectorias().removeAll(tr);
		if (trayP != null) {
			g2.setPaint(new Color(192, 192, 192, 128));
			for (Habitacion h : piso.getHabitaciones()) {
				if (h == trayH) {
					g2.fill(h.obtenerBaldosa(trayP.getX(), trayP.getY()).getForma());
				}
			}
		}
		for (Puerta p : piso.getPuertas()) {
			g2.setPaint(Color.BLACK);
			g2.fill(p.getForma());
		}
		if (pTemp != null) {
			g2.setPaint(new Color(0, 0, 0, 128));
			g2.fill(pTemp.getForma());
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
	}

	public void eliminar() {
		piso = new Piso();
		orientacion = 0;
		escala = "5m";
		trayP = null;
		trayH = null;
		orig = null;
		actual = null;
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
