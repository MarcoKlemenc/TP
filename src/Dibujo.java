import java.awt.Color;
import java.awt.Font;
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
	private Point inicio, fin, puntoOrig;
	private Puerta puertaActual;
	private int desvioX, desvioY, orientacion, modo, escala = 5;
	private Habitacion actual, temp, habitacionOrig, seleccionada;
	private Recorrido orig, dest;
	private String unidad = "m";
	private String[] modos = { "habitación", "trayectoria", "obstáculo" };
	private Trayectoria frente;

	public Dibujo() {

		this.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {
				List<Habitacion> l = piso.getHabitaciones(e);
				if (seleccionada != null && l.size() == 1 && l.get(0) == seleccionada) {
					piso.eliminarHabitacion(l.get(0));
					seleccionada = null;
					repaint();
					return;
				}
				if (l.size() == 0) {
					seleccionada = null;
					frente = null;
				}
				Puerta p = piso.getPuerta(e);
				if (p != null) {
					piso.eliminarPuerta(p);
				} else if (l.size() >= 1) {
					for (Habitacion h1 : piso.getHabitaciones()) {
						for (Habitacion h2 : piso.getHabitaciones()) {
							if ((piso.contienePuertaV(h1, h2, e) || piso.contienePuertaV(h2, h1, e))
									&& h1.intersecta(h2)) {
								int x = Math.min(h1.getX(), h2.getX());
								piso.agregarPuerta(x + (x == h1.getX() ? h1.getLargo() : h2.getLargo()) - 5, e.getY(),
										11, Math.min(30, Math.min(h1.getY() + h1.getAlto(), h2.getY() + h2.getAlto())
												- e.getY()),
										h1, h2, true);
								return;
							} else if ((piso.contienePuertaH(h1, h2, e) || piso.contienePuertaH(h2, h1, e))
									&& h1.intersecta(h2)) {
								int y = Math.min(h1.getY(), h2.getY());
								piso.agregarPuerta(e.getX(), y + (y == h1.getY() ? h1.getAlto() : h2.getAlto()) - 5,
										Math.min(30, Math.min(h1.getX() + h1.getLargo(), h2.getX() + h2.getLargo())
												- e.getY()),
										11, h1, h2, false);
								return;
							}
						}
					}
					actual = l.get(0);
					Baldosa b = actual.contieneBaldosa(e);
					if (modo == 0) {
						seleccionada = l.get(0);
					} else if (modo == 1) {
						if (orig != null && dest == null && b.isPasar()) {
							dest = new Recorrido(piso.baldosaDentro(b), b.getCoordenadas());
							if (!orig.equals(dest)) {
								piso.generarTrayectoria(orig, dest);
							}
							orig = null;
							puntoOrig = null;
							habitacionOrig = null;
						} else if (b.isPasar()) {
							Trayectoria seleccion = piso.getTrayectoria(e, b);
							if (seleccion == null) {
								orig = new Recorrido(piso.baldosaDentro(b), b.getCoordenadas());
								puntoOrig = new Point(b.getFila(), b.getColumna());
								habitacionOrig = actual;
							}
							if (frente == seleccion) {
								piso.eliminarTrayectoria(e, b);
								frente = null;
							} else {
								frente = seleccion;
							}
						}
						dest = null;
					} else {
						for (Trayectoria t : piso.getTrayectorias()) {
							for (Recorrido r : t.getCamino()) {
								if (r.getHabitacion() == actual && r.getCoordenadas().equals(b.getCoordenadas())) {
									return;
								}
							}
						}
						if (b.getCoordenadas().equals(puntoOrig)) {
							orig = null;
							puntoOrig = null;
							habitacionOrig = null;
						}
						b.cambiarPasar();
					}
				}
				repaint();
			}

			public void mousePressed(MouseEvent e) {
				List<Habitacion> l = piso.getHabitaciones(e);
				if (!(l.size() == 1 && l.get(0) == seleccionada)) {
					seleccionada = null;
					actual = null;
				}
				puertaActual = piso.getPuerta(e);
				if (puertaActual == null) {
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
				if (actual == null && temp != null && temp.getLargo() >= 2 && temp.getAlto() >= 2
						&& piso.cruza(temp).size() == 0) {
					piso.agregarHabitacion(temp);
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
				if (puertaActual != null) {
					Habitacion h1 = puertaActual.getH1();
					Habitacion h2 = puertaActual.getH2();
					if (puertaActual.isVertical()) {
						puertaActual.setAlto(Math.min(Math.max(2, e.getY() - puertaActual.getY()),
								Math.min(h1.getY() + h1.getAlto(), h2.getY() + h2.getAlto()) - puertaActual.getY()));
					} else {
						puertaActual.setLargo(Math.min(Math.max(2, e.getX() - puertaActual.getX()),
								Math.min(h1.getX() + h1.getLargo(), h2.getX() + h2.getLargo()) - puertaActual.getX()));
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
								if (actual.intersecta(h.getBordeIzq())
										&& (!actual.intersecta(h.getBordeArr()) || aux(h))) {
									actual.setLargo(h.getX() - actual.getX());
								} else if (actual.intersecta(h.getBordeArr())
										&& (!(actual.intersecta(h.getBordeIzq()) && aux(h)))) {
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
									if (actual.intersecta(h.getBordeDer())) {
										actual.setX(Math.max(h.getX() + h.getLargo(), x));
										chocoX = true;
									} else if (actual.intersecta(h.getBordeIzq())) {
										actual.setX(Math.min(h.getX() - actual.getLargo(), x));
										chocoX = true;
									} else {
										actual.setX(x);
									}
								}
								if (!chocoY) {
									if (actual.intersecta(h.getBordeAba())) {
										actual.setY(Math.max(h.getY() + h.getAlto(), y));
										chocoY = true;
									} else if (actual.intersecta(h.getBordeArr())) {
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
					seleccionada.setLado(Math.max(2, Math.min(seleccionada.getLado() + e.getWheelRotation() * 4,
							Math.min(seleccionada.getLargo(), seleccionada.getAlto()))));
					seleccionada.generarBaldosas();
				}
				repaint();
			}
		});
	}

	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		int altura = g2.getFontMetrics().getHeight() / 2;
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setFont(new Font("Calibri", Font.PLAIN, 16));
		g2.setPaint(Color.WHITE);
		g2.fillRect(20, 20, getWidth() - 40, getHeight() - 40);
		g2.setPaint(new Color(238, 238, 238));
		for (int i = 20 - 1; i < getWidth() - 20; i += 100) {
			for (int j = 20 - 1; j < getHeight() - 20; j += 100) {
				g2.draw(new Rectangle2D.Float(i, j, 100, 100));
			}
		}
		Color[] colores = { new Color(192, 255, 255), new Color(192, 255, 192), new Color(255, 255, 192),
				new Color(255, 192, 192), new Color(255, 192, 255), new Color(192, 192, 255) };
		int color = -1;
		int colorFrente = -1;
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
			g2.setPaint(colores[color += (color == 5) ? -5 : 1]);
			if (frente != t) {
				for (Recorrido r : t.getCamino()) {
					Point p = r.getCoordenadas();
					Baldosa b = r.getHabitacion().getBaldosa(p.getX(), p.getY());
					if (b != null) {
						g2.fill(b.getInterior());
					} else {
						tt.add(t);
					}
				}
			} else {
				colorFrente = color;
			}
		}
		if (frente != null) {
			for (Recorrido r : frente.getCamino()) {
				Point p = r.getCoordenadas();
				Baldosa b = r.getHabitacion().getBaldosa(p.getX(), p.getY());
				if (b != null) {
					g2.setPaint(colores[colorFrente]);
					g2.fill(b.getInterior());
					g2.setPaint(Color.WHITE);
					g2.draw(b.getForma());
					g2.drawLine(b.getX(), b.getY(), b.getX() + b.getLargo(), b.getY() + b.getAlto());
					g2.drawLine(b.getX() + b.getLargo(), b.getY(), b.getX(), b.getY() + b.getAlto());
				} else {
					tt.add(frente);
				}
			}
		}
		piso.getTrayectorias().removeAll(tt);
		if (seleccionada != null) {
			g2.setPaint(new Color(0, 192, 0, 128));
			g2.fill(seleccionada.getInterior());
			g2.setPaint(Color.BLACK);
			String medida = darMedida(seleccionada.getLargo());
			int desvio = g2.getFontMetrics().stringWidth(medida) / 2;
			escribir(g2, seleccionada.getX() + seleccionada.getLargo() / 2 - desvio, seleccionada.getY() + altura + 4,
					0, medida);
			medida = darMedida(seleccionada.getAlto());
			escribir(g2, seleccionada.getX() + altura + 4, seleccionada.getY() + seleccionada.getAlto() / 2 + desvio,
					270, medida);
			escribir(g2, 20, getHeight() - 3, 0, "Baldosas: " + darMedida(seleccionada.getLado()));
		}
		if (puntoOrig != null && piso.getHabitaciones().contains(habitacionOrig)) {
			Baldosa b = habitacionOrig.getBaldosa(puntoOrig.getX(), puntoOrig.getY());
			if (b != null) {
				g2.setPaint(new Color(192, 192, 192, 128));
				g2.fill(b.getForma());
			} else {
				habitacionOrig = null;
				puntoOrig = null;
			}
		}
		for (Puerta p : piso.getPuertas()) {
			g2.setPaint(Color.BLACK);
			g2.fill(p.getForma());
		}
		if (inicio != null && fin != null) {
			g2.setPaint(Color.BLACK);
			int largo = Math.abs(inicio.x - fin.x);
			int alto = Math.abs(inicio.y - fin.y);
			temp = new Habitacion(Math.min(inicio.x, fin.x), Math.min(inicio.y, fin.y), largo, alto,
					Math.min(largo, alto) / 4 + 1);
			g2.draw(temp.getForma());
			g2.setPaint(piso.cruza(temp).size() != 0 ? new Color(255, 0, 0, 64) : new Color(0, 255, 0, 32));
			g2.fill(temp.getForma());
		}
		g2.setPaint(Color.BLACK);
		g2.drawLine(20, 19, 119, 19);
		g2.drawString(String.valueOf(escala) + " " + unidad, 20, 18);
		int o = orientacion;
		int desvio = g2.getFontMetrics().stringWidth(String.valueOf(o)) / 2;
		escribir(g2, getWidth() / 2 - desvio, 13, 0, o);
		desvio = g2.getFontMetrics().stringWidth(String.valueOf(o += (o >= 270) ? -270 : 90)) / 2;
		escribir(g2, getWidth() - altura - 4, getHeight() / 2 - desvio, 90, o);
		desvio = g2.getFontMetrics().stringWidth(String.valueOf(o += (o >= 270) ? -270 : 90)) / 2;
		escribir(g2, getWidth() / 2 - desvio, getHeight() - 3, 0, o);
		desvio = g2.getFontMetrics().stringWidth(String.valueOf(o += (o >= 270) ? -270 : 90)) / 2;
		escribir(g2, altura + 4, getHeight() / 2 + desvio, 270, o);
	}

	public String cambiarModo() {
		seleccionada = null;
		frente = null;
		return modos[modo += (modo == 2) ? -2 : 1];
	}

	public void eliminar() {
		piso = new Piso();
		orientacion = 0;
		escala = 5;
		unidad = "m";
		puntoOrig = null;
		habitacionOrig = null;
		orig = null;
		actual = null;
		seleccionada = null;
	}

	private boolean aux(Habitacion h) {
		return actual.getX() + actual.getLargo() - h.getX() < actual.getY() + actual.getAlto() - h.getY();
	}

	private String darMedida(int valor) {
		String medida = Integer.toString(valor * escala);
		medida = new StringBuilder(medida).insert(medida.length() - 2, ".").toString();
		if (medida.charAt(0) == '.') {
			medida = new StringBuilder(medida).insert(0, "0").toString();
		}
		return medida + " " + unidad;
	}

	private void escribir(Graphics2D g2, int x, int y, int angulo, int valor) {
		escribir(g2, x, y, angulo, String.valueOf(valor));
	}

	private void escribir(Graphics2D g2, int x, int y, int angulo, String valor) {
		g2.translate(x, y);
		g2.rotate(Math.toRadians(angulo));
		g2.drawString(valor, 0, 0);
		g2.rotate(-Math.toRadians(angulo));
		g2.translate(-x, -y);
	}

	public int getEscala() {
		return escala;
	}

	public void setEscala(int escala) throws Exception {
		if (escala > 0) {
			this.escala = escala;
		} else {
			throw new Exception();
		}
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

	public Point getPuntoOrig() {
		return puntoOrig;
	}

	public void setPuntoOrig(Point puntoOrig) {
		this.puntoOrig = puntoOrig;
	}

	public Habitacion getActual() {
		return actual;
	}

	public void setActual(Habitacion actual) {
		this.actual = actual;
	}

	public Habitacion getHabitacionOrig() {
		return habitacionOrig;
	}

	public void setHabitacionOrig(Habitacion habitacionOrig) {
		this.habitacionOrig = habitacionOrig;
	}

	public Recorrido getOrig() {
		return orig;
	}

	public void setOrig(Recorrido orig) {
		this.orig = orig;
	}

	public String getUnidad() {
		return unidad;
	}

	public void setUnidad(String unidad) {
		this.unidad = unidad;
	}
}
