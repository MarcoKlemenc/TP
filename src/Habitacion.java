import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Habitacion extends Componente {

	private static int idActual = 1;
	private int id, lado;
	private List<Baldosa> baldosas;

	public Habitacion(StringTokenizer s) {
		super(s);
		this.lado = Integer.parseInt(s.nextToken());
		id = idActual++;
		generarBaldosas(new StringTokenizer(s.nextToken(), "&"));
	}

	public Habitacion(int x, int y, int largo, int alto, int lado) {
		super(x, y, largo, alto);
		this.lado = lado;
		id = idActual++;
		generarBaldosas();
	}

	private boolean pasarPor(int filaA, int columnaA) {
		return obtenerBaldosa(filaA, columnaA) != null && obtenerBaldosa(filaA, columnaA).isPasar();
	}

	private int getFilas() {
		int filas = 0;
		for (Baldosa b : baldosas) {
			filas = Math.max(b.getFila(), filas);
		}
		return filas;
	}

	private int getColumnas() {
		int columnas = 0;
		for (Baldosa b : baldosas) {
			columnas = Math.max(b.getColumna(), columnas);
		}
		return columnas;
	}

	public boolean contieneBaldosa(Baldosa b) {
		for (Baldosa b1 : baldosas) {
			if (b == b1) {
				return true;
			}
		}
		return false;
	}

	public Baldosa contieneBaldosa(MouseEvent e) {
		for (Baldosa b : baldosas) {
			if (b.contiene(e.getX(), e.getY())) {
				return b;
			}
		}
		return null;
	}

	private List<Point> getObstaculos() {
		List<Point> obstaculos = new ArrayList<Point>();
		if (baldosas != null) {
			for (Baldosa b : baldosas) {
				if (!b.isPasar()) {
					obstaculos.add(b.getCoordenadas());
				}
			}
		}
		return obstaculos;
	}

	public boolean intersecta(Habitacion h) {
		if (equals(h)) {
			return false;
		} else {
			Area a = new Area(getColision());
			a.intersect(new Area(h.getColision()));
			return !a.isEmpty();
		}
	}

	public boolean intersecta(Rectangle2D r) {
		Area a = new Area(getColision());
		a.intersect(new Area(r));
		return !a.isEmpty();
	}

	public Rectangle2D getColision() {
		return new Rectangle2D.Float(x, y, largo + 1, alto + 1);
	}

	public Rectangle2D bordeIzq() {
		return new Rectangle2D.Float(x, y, 1, alto);
	}

	public Rectangle2D bordeDer() {
		return new Rectangle2D.Float(x + largo, y, 1, alto);
	}

	public Rectangle2D bordeArr() {
		return new Rectangle2D.Float(x, y, largo, 1);
	}

	public Rectangle2D bordeAba() {
		return new Rectangle2D.Float(x, y + alto, largo, 1);
	}

	private void crearBaldosas() {
		baldosas = new ArrayList<Baldosa>();
		int columna = 1;
		for (int i = x; i < x + largo; i += lado) {
			int fila = 1;
			for (int j = y; j < y + alto; j += lado) {
				Baldosa b = new Baldosa(i, j, Math.min(x + largo - i, lado), Math.min(y + alto - j, lado), fila,
						columna);
				baldosas.add(b);
				fila++;
			}
			columna++;
		}
	}

	public void generarBaldosas(StringTokenizer s) {
		crearBaldosas();
		if (s.countTokens() > 1) {
			while (s.hasMoreTokens()) {
				obtenerBaldosa(Integer.parseInt(s.nextToken()), Integer.parseInt(s.nextToken())).cambiarPasar();
			}
		}
	}

	public void generarBaldosas() {
		crearBaldosas();
		for (Point p : getObstaculos()) {
			obtenerBaldosa(p.getX(), p.getY()).cambiarPasar();
		}
	}

	public Baldosa obtenerBaldosa(double fila, double columna) {
		for (Baldosa b : this.baldosas) {
			if (b.getFila() == fila && b.getColumna() == columna) {
				return b;
			}
		}
		return null;
	}

	public Trayectoria generarTrayectoria(int filaA, int columnaA, int filaB, int columnaB) {
		int filaInicial = filaA;
		int columnaInicial = columnaA;
		try {
			int filaAnterior = -1;
			int columnaAnterior = -1;
			int filas = getFilas();
			int columnas = getColumnas();
			Trayectoria t = new Trayectoria();
			while (filaA != filaB || columnaA != columnaB) {
				Baldosa b = obtenerBaldosa(filaA, columnaA);
				if (b.isPasar()) {
					filaAnterior = filaA;
					columnaAnterior = columnaA;
					t.agregarBaldosa(this, filaA, columnaA);
					if (Math.abs(filaA - filaB) > Math.abs(columnaA - columnaB)) {
						filaA -= Math.signum(filaA - filaB);
					} else if (Math.abs(filaA - filaB) < Math.abs(columnaA - columnaB)) {
						columnaA -= Math.signum(columnaA - columnaB);
					} else {
						filaA -= Math.signum(filaA - filaB);
						columnaA -= Math.signum(columnaA - columnaB);
					}
				} else {
					if (pasarPor(filaA, columnaA - 1) && columnaAnterior == columnaA - 1) {
						while (pasarPor(filaA, columnaA - 1) && !obtenerBaldosa(filaA, columnaA).isPasar()) {
							t.agregarBaldosa(this, filaA, columnaA - 1);
							filaA += (filaAnterior > 1) ? -1 : 1;
						}
					} else if (pasarPor(filaA - 1, columnaA) && filaAnterior == filaA - 1) {
						while (pasarPor(filaA - 1, columnaA) && !obtenerBaldosa(filaA, columnaA).isPasar()) {
							t.agregarBaldosa(this, filaA - 1, columnaA);
							columnaA += (columnaAnterior < columnas) ? 1 : -1;
						}
					} else if (pasarPor(filaA, columnaA + 1) && columnaAnterior == columnaA + 1) {
						while (pasarPor(filaA, columnaA + 1) && !obtenerBaldosa(filaA, columnaA).isPasar()) {
							t.agregarBaldosa(this, filaA, columnaA + 1);
							filaA += (filaAnterior < filas) ? 1 : -1;
						}
					} else if (pasarPor(filaA + 1, columnaA) && filaAnterior == filaA + 1) {
						while (pasarPor(filaA + 1, columnaA) && !obtenerBaldosa(filaA, columnaA).isPasar()) {
							t.agregarBaldosa(this, filaA + 1, columnaA);
							columnaA += (columnaAnterior > 1) ? -1 : 1;
						}
					}
				}
			}
			if (pasarPor(filaA, columnaA)) {
				t.agregarBaldosa(this, filaA, columnaA);
			}
			return t;
		} catch (Exception e) {
			return generarTrayectoria(filaB, columnaB, filaInicial, columnaInicial);
		}
	}

	public List<Baldosa> getBaldosas() {
		return baldosas;
	}

	public int getLado() {
		return lado;
	}

	public void setLado(int lado) {
		this.lado = lado;
	}

	public int getId() {
		return id;
	}

	public static void setIdActual(int idActual) {
		Habitacion.idActual = idActual;
	}

	public String toString() {
		String string = id + "|" + x + "|" + y + "|" + largo + "|" + alto + "|" + lado + "|";
		List<Point> obstaculos = getObstaculos();
		if (obstaculos.isEmpty()) {
			string += "!";
		}
		for (Point p : obstaculos) {
			string += (int) p.getX() + "&" + (int) p.getY() + "&";
		}
		return string + "|";
	}

}
