
import java.awt.geom.Rectangle2D;

public class Superficie {

	protected double x, y, largo, alto;

	public Superficie(double x, double y, double largo, double alto) {
		this.x = x;
		this.y = y;
		this.largo = largo;
		this.alto = alto;
	}

	public boolean contiene(double x, double y) {
		return x >= this.x && x <= this.x + largo && y >= this.y && y <= this.y + alto;
	}

	public Rectangle2D getForma() {
		return new Rectangle2D.Double(x, y, largo, alto);
	}

	public Rectangle2D getInterior() {
		return new Rectangle2D.Double(x + 1, y + 1, largo - 1, alto - 1);
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getLargo() {
		return largo;
	}

	public void setLargo(double largo) {
		this.largo = largo;
	}

	public double getAlto() {
		return alto;
	}

	public void setAlto(double alto) {
		this.alto = alto;
	}
}
