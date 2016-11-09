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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

class Dibujo extends JComponent {

	private static final long serialVersionUID = 1L;
	private List<Habitacion> piso = new ArrayList<Habitacion>();
	private List<Puerta> puertas = new ArrayList<Puerta>();
	private Map<Habitacion, Set<Habitacion>> adyacencias = new HashMap<Habitacion, Set<Habitacion>>();
	private Point inicio, fin;
	private int desvioX, desvioY;
	private Integer orientacion = 0;
	private Habitacion actual, temp;
	private Baldosa orig, dest;
	private List<Trayectoria> trayectorias = new ArrayList<Trayectoria>();
	private boolean shift, ctrl;
	private String escala = "5m";

	private List<Habitacion> contiene(MouseEvent e) {
		List<Habitacion> l = new ArrayList<Habitacion>();
		for (Habitacion h : piso) {
			if (h.contiene(e.getX(), e.getY())) {
				l.add(h);
			}
		}
		return l;
	}

	private Baldosa contieneB(MouseEvent e) {
		for (Baldosa b : actual.getBaldosas()) {
			if (b.contiene(e.getX(), e.getY())) {
				return b;
			}
		}
		return null;
	}

	private List<Habitacion> cruza(Habitacion o) {
		List<Habitacion> l = new ArrayList<Habitacion>();
		for (Habitacion h : piso) {
			if (o.intersecta(h)) {
				l.add(h);
			}
		}
		return l;
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
				List<Habitacion> l = contiene(e);
				if (SwingUtilities.isLeftMouseButton(e)) {
					if (l.size() == 1) {
						actual = l.get(0);
						Baldosa b = contieneB(e);
						if (orig != null && dest == null) {
							dest = b;
							trayectorias.add(actual.generarTrayectoria(orig.getFila(), orig.getColumna(),
									dest.getFila(), dest.getColumna()));
							orig = null;
						} else {
							// mejorar para que borre más de una, si se cruzan?
							for (Trayectoria t : trayectorias) {
								if (t.getCamino().contains(b)) {
									trayectorias.remove(t);
									return;
								}
							}
							orig = b;
						}
						dest = null;
					} else if (l.size() == 2) {
						Habitacion h1 = l.get(0);
						Habitacion h2 = l.get(1);
						puertas.add(new Puerta(e.getX() - 3, e.getY(), 7, 30, h1, h2));
						adyacencias.get(h1).add(h2);
						adyacencias.get(h2).add(h1);
					}
				} else if (SwingUtilities.isMiddleMouseButton(e)) {
					if (l.size() == 1) {
						actual = l.get(0);
						contieneB(e).cambiarPasar();
					}
				}
			}

			public void mousePressed(MouseEvent e) {
				List<Habitacion> l = contiene(e);
				List<Puerta> puertasBorrar = new ArrayList<Puerta>();
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
						for (Puerta p : puertas) {
							if (p.getH1() == l.get(0) || p.getH2() == l.get(0)) {
								puertasBorrar.add(p);
							}
						}
						for (Set<Habitacion> s : adyacencias.values()) {
							s.remove(l.get(0));
						}
						adyacencias.remove(l.get(0));
						piso.remove(l.get(0));
						puertas.removeAll(puertasBorrar);
					}
				}
				repaint();
			}

			public void mouseReleased(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e) && actual == null && temp.getLargo() >= 2
						&& temp.getAlto() >= 2) {
					Habitacion h = new Habitacion((int) temp.getX(), (int) temp.getY(), (int) temp.getLargo(),
							(int) temp.getAlto());
					if (cruza(h).size() == 0) {
						piso.add(h);
						h.setLado(Math.min(h.getAlto(), h.getLargo()) / 4 + 1);
						h.generarBaldosas();
						adyacencias.put(h, new HashSet<Habitacion>());
					}
				}
				inicio = null;
				fin = null;
				actual = null;
				repaint();
			}
		});
		this.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e)) {
					if (actual == null) {
						fin = new Point(e.getX(), e.getY());
					} else {
						for (Puerta p : puertas) {
							if (p.getH1() == actual || p.getH2() == actual) {
								actual = null;
								return;
							}
						}
						int x = e.getX() - actual.getX();
						int y = e.getY() - actual.getY();
						List<Habitacion> l = cruza(actual);
						if (shift && !ctrl) {
							if (actual.getLargo() <= 2 || actual.getAlto() <= 2) {
								piso.remove(actual);
								repaint();
								return;
							}
							actual.setLargo(x);
							actual.setAlto(y);
							if (l.size() > 0) {
								for (Habitacion h : l) {
									if (actual.contiene(h.getX(), actual.getY() + actual.getAlto())
											&& !actual.contiene(actual.getX() + actual.getLargo(), h.getY())) {
										actual.setLargo(h.getX() - actual.getX());
									} else if (actual.contiene(actual.getX() + actual.getLargo(), h.getY())
											&& !actual.contiene(h.getX(), actual.getY() + actual.getAlto())) {
										actual.setAlto(h.getY() - actual.getY());
									} else if (actual.contiene(actual.getX() + actual.getLargo(), h.getY())
											&& actual.contiene(h.getX(), actual.getY() + actual.getAlto())
											&& actual.getX() + actual.getLargo() - h.getX() >= actual.getY()
													+ actual.getAlto() - h.getY()) {
										actual.setAlto(h.getY() - actual.getY());
									} else if (actual.contiene(h.getX(), actual.getY() + actual.getAlto())
											&& actual.contiene(actual.getX() + actual.getLargo(), h.getY())
											&& actual.getX() + actual.getLargo() - h.getX() < actual.getY()
													+ actual.getAlto() - h.getY()) {
										actual.setLargo(h.getX() - actual.getX());
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
										if (actual.getX() >= h.getX() + h.getLargo()) {
											actual.setX(Math.max(h.getX() + h.getLargo(), x));
											chocoX = true;
										} else if (actual.getX() + actual.getLargo() <= h.getX()) {
											actual.setX(Math.min(h.getX() - actual.getLargo(), x));
											chocoX = true;
										} else if (actual.getX() >= h.getX() && actual.getX() <= h.getX() + h.getLargo()
												&& actual.getY() >= h.getY() && actual.getY() <= h.getY() + h.getAlto()
												&& actual.getX() + actual.getLargo() - h.getX() >= actual.getY()
														+ actual.getAlto() - h.getY()) {
											actual.setX(h.getX() + h.getLargo());
											chocoX = true;
										} else {
											actual.setX(x);
										}
									}
									if (!chocoY) {
										if (actual.getY() >= h.getY() + h.getAlto()) {
											actual.setY(Math.max(h.getY() + h.getAlto(), y));
											chocoY = true;
										} else if (actual.getY() + actual.getAlto() <= h.getY()) {
											actual.setY(Math.min(h.getY() - actual.getAlto(), y));
											chocoY = true;
										} else if (actual.getY() >= h.getY() && actual.getY() <= h.getY() + h.getAlto()
												&& actual.getX() >= h.getX() && actual.getX() <= h.getX() + h.getLargo()
												&& actual.getX() + actual.getLargo() - h.getX() < actual.getY()
														+ actual.getAlto() - h.getY()) {
											actual.setY(h.getY() + h.getAlto());
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
		g2.drawString(orientacion.toString(), getWidth() / 2, 15);
		orientacion += 90;
		if (orientacion >= 360) {
			orientacion -= 360;
		}
		g2.drawString(orientacion.toString(), getWidth() - 20, getHeight() / 2 - 5);
		orientacion += 90;
		if (orientacion >= 360) {
			orientacion -= 360;
		}
		g2.drawString(orientacion.toString(), getWidth() / 2, getHeight() - 5);
		orientacion += 90;
		if (orientacion >= 360) {
			orientacion -= 360;
		}
		g2.drawString(orientacion.toString(), 0, getHeight() / 2 - 5);
		orientacion = orientacionVieja;
		g2.setPaint(Color.WHITE);
		g2.fillRect(20, 20, getWidth() - 40, getHeight() - 40);
		g2.setPaint(new Color(238, 238, 238));
		for (int i = -1; i < getWidth(); i += 100) {
			for (int j = -1; j < getHeight(); j += 100) {
				g2.draw(new Rectangle2D.Float(i, j, 100, 100));
			}
		}
		for (Habitacion h : piso) {
			for (Baldosa b : h.getBaldosas()) {
				Rectangle2D r = b.getForma();
				g2.setPaint(new Color(160, 160, 160));
				g2.draw(r);
				if (!b.isPasar()) {
					g2.fill(r);
				}
			}
			g2.setPaint(Color.BLACK);
			g2.draw(h.getForma());
			for (Trayectoria t : trayectorias) {
				if (t.getHabitacion() == h) {
					g2.setPaint(Color.CYAN);
					for (Baldosa b : t.getCamino()) {
						g2.fillRect(b.getX() + 1, b.getY() + 1, b.getLargo() - 1, b.getAlto() - 1);
					}
				}
			}

		}
		if (orig != null) {
			g2.setPaint(new Color(0, 255, 255, 64));
			g2.fillRect(orig.getX() + 1, orig.getY() + 1, orig.getLargo() - 1, orig.getAlto() - 1);
		}
		for (Puerta p : puertas) {
			g2.setPaint(Color.BLACK);
			g2.fill(p.getForma());
		}
		if (inicio != null && fin != null) {
			g2.setPaint(Color.BLACK);
			temp = new Habitacion(Math.min(inicio.x, fin.x), Math.min(inicio.y, fin.y), Math.abs(inicio.x - fin.x),
					Math.abs(inicio.y - fin.y));
			g2.draw(temp.getForma());
			g2.setPaint(cruza(temp).size() != 0 ? new Color(255, 0, 0, 64) : new Color(0, 255, 0, 32));
			g2.fill(temp.getForma());
		}
		g2.setPaint(Color.BLACK);
		g2.drawLine(20, 20, 99, 20);
		g2.drawString(escala, 20, 18);
	}

	public List<Habitacion> getPiso() {
		return piso;
	}

	public void setPiso(List<Habitacion> piso) {
		this.piso = piso;
	}

	public String getEscala() {
		return escala;
	}

	public void setEscala(String escala) {
		this.escala = escala;
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

}
