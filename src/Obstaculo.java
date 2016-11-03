public class Obstaculo {

	private String tipo;
	private int x;
	private int y;
	private int largo;
	private int alto;

	public Obstaculo(String tipo, int x, int y, int largo, int alto) {

		this.tipo = tipo;
		this.x = x;
		this.y = y;
		this.largo = largo;
		this.alto = alto;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
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
