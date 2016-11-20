import java.awt.geom.Rectangle2D;
import java.util.StringTokenizer;

public class Componente {

	protected int x, y, largo, alto;

	public Componente(StringTokenizer s) {
		if (this instanceof Habitacion) {
			Habitacion.setIdActual(Integer.parseInt(s.nextToken()));
		}
		this.x = Integer.parseInt(s.nextToken());
		this.y = Integer.parseInt(s.nextToken());
		this.largo = Integer.parseInt(s.nextToken());
		this.alto = Integer.parseInt(s.nextToken());
	}

	public Componente(int x, int y, int largo, int alto) {
		this.x = x;
		this.y = y;
		this.largo = largo;
		this.alto = alto;
	}

	public boolean contiene(int x, int y) {
		return x >= this.x && x <= this.x + largo && y >= this.y && y <= this.y + alto;
	}

	public Rectangle2D getForma() {
		return new Rectangle2D.Float(x, y, largo, alto);
	}

	public Rectangle2D getInterior() {
		return new Rectangle2D.Float(x + 1, y + 1, largo - 1, alto - 1);
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getLargo() {
		return largo;
	}

	public void setLargo(int largo) {
		this.largo = largo;
	}

	public int getAlto() {
		return alto;
	}

	public void setAlto(int alto) {
		this.alto = alto;
	}
}
