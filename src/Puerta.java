import java.awt.geom.Area;

public class Puerta extends Superficie {

	private Habitacion h1, h2;
	private boolean vertical;

	public Puerta(int x, int y, int largo, int alto, Habitacion h1, Habitacion h2, boolean vertical) {
		super(x, y, largo, alto);
		this.h1 = h1;
		this.h2 = h2;
		this.vertical = vertical;
	}

	public boolean intersecta(Puerta p) {
		if (equals(p)) {
			return false;
		} else {
			Area a = new Area(getForma());
			a.intersect(new Area(p.getForma()));
			return !a.isEmpty();
		}
	}

	public Habitacion getH1() {
		return h1;
	}

	public Habitacion getH2() {
		return h2;
	}

	public boolean isVertical() {
		return vertical;
	}

	public String toString() {
		return x + "Ç" + y + "Ç" + largo + "Ç" + alto + "Ç" + h1.getId() + "Ç" + h2.getId() + "Ç" + vertical + "Ç";
	}

}
