import java.awt.Point;

public class Baldosa extends Componente {

	private int fila, columna;
	private boolean pasar = true;

	public Baldosa(int x, int y, int largo, int alto, int fila, int columna) {
		super(x, y, largo, alto);
		this.fila = fila;
		this.columna = columna;
	}

	public boolean contiene(Puerta p) {
		return contiene(p.getX(), p.getY()) || contiene(p.getX() + p.getLargo(), p.getY())
				|| contiene(p.getX(), p.getY() + p.getAlto());
	}

	public Point getCoordenadas() {
		return new Point(getFila(), getColumna());
	}

	public boolean isPasar() {
		return pasar;
	}

	public void cambiarPasar() {
		this.pasar ^= true;
	}

	public int getFila() {
		return fila;
	}

	public int getColumna() {
		return columna;
	}

}
