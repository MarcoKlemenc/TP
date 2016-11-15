import java.awt.Point;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class Habitacion extends Superficie {

	private double lado;
	private List<Baldosa> baldosas;

	public Habitacion(double x, double y, double largo, double alto) {
		super(x, y, largo, alto);
	}

	public boolean contieneB(Baldosa b) {
		for (Baldosa b1 : baldosas) {
			if (b == b1) {
				return true;
			}
		}
		return false;
	}

	public List<Point> getNoPasar() {
		List<Point> noPasar = new ArrayList<Point>();
		if (baldosas != null) {
			for (Baldosa b : baldosas) {
				if (!b.isPasar()) {
					noPasar.add(b.getCoordenadas());
				}
			}
		}
		return noPasar;
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

	Rectangle2D getColision() {
		return new Rectangle2D.Double(x, y, largo + 1, alto + 1);
	}

	public void generarBaldosas() {
		List<Point> noPasar = getNoPasar();
		this.baldosas = new ArrayList<Baldosa>();
		int columna = 1;
		for (double i = x; i < x + largo; i += lado) {
			int fila = 1;
			for (double j = y; j < y + alto; j += lado) {
				Baldosa b = new Baldosa(i, j, Math.min(x + largo - i, lado), Math.min(y + alto - j, lado), fila,
						columna);
				if (noPasar.contains(b.getCoordenadas())) {
					b.cambiarPasar();
				}
				baldosas.add(b);
				fila++;
			}
			columna++;
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
		Trayectoria t = new Trayectoria();
		while (filaA != filaB || columnaA != columnaB) {
			Baldosa b = obtenerBaldosa(filaA, columnaA);
			if (b.isPasar()) {
				t.agregarBaldosa(this, b.getCoordenadas());
				if (Math.abs(filaA - filaB) > Math.abs(columnaA - columnaB)) {
					filaA -= Math.signum(filaA - filaB);
				} else if (Math.abs(filaA - filaB) < Math.abs(columnaA - columnaB)) {
					columnaA -= Math.signum(columnaA - columnaB);
				} else {
					filaA -= Math.signum(filaA - filaB);
					columnaA -= Math.signum(columnaA - columnaB);
				}
			} else {
				if (obtenerBaldosa(filaA, columnaA - 1).isPasar()) {
					columnaA--;
					while (!obtenerBaldosa(filaA, columnaA + 1).isPasar()) {
						t.agregarBaldosa(this, filaA, columnaA);
						filaA--;
					}
				} else if (obtenerBaldosa(filaA - 1, columnaA).isPasar()) {
					filaA--;
					while (!obtenerBaldosa(filaA + 1, columnaA).isPasar()) {
						t.agregarBaldosa(this, filaA, columnaA);
						columnaA++;
					}
				} else {
					if (filaA > 1 && obtenerBaldosa(filaA - 1, columnaA).isPasar()) {
						filaA--;
					} else if (filaA < alto / lado && obtenerBaldosa(filaA + 1, columnaA).isPasar()) {
						filaA++;
					} else if (columnaA > 1 && obtenerBaldosa(filaA, columnaA - 1).isPasar()) {
						columnaA--;
					} else if (columnaA < largo / lado && obtenerBaldosa(filaA, columnaA + 1).isPasar()) {
						columnaA++;
					}
				}

			}
		}
		t.agregarBaldosa(this, filaA, columnaA);
		return t;
	}

	public List<Baldosa> getBaldosas() {
		return baldosas;
	}

	public void setBaldosas(List<Baldosa> baldosas) {
		this.baldosas = baldosas;
	}

	public double getLado() {
		return lado;
	}

	public void setLado(double lado) {
		this.lado = lado;
	}

}
