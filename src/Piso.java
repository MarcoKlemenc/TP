import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Piso {

	private List<Habitacion> habitaciones = new ArrayList<Habitacion>();
	private List<Puerta> puertas = new ArrayList<Puerta>();
	private List<Trayectoria> trayectorias = new ArrayList<Trayectoria>();
	private Map<Habitacion, Set<Habitacion>> adyacencias = new HashMap<Habitacion, Set<Habitacion>>();
	private Grafo grafo = null;

	public boolean eliminarTrayectoria(MouseEvent e, Baldosa b) {
		for (Trayectoria t : trayectorias) {
			for (Habitacion h : habitaciones) {
				if (t.buscar(h, b.getCoordenadas()) && t.getHabitaciones().contains(h)
						&& h.contiene(e.getX(), e.getY())) {
					trayectorias.remove(t);
					return true;
				}
			}
		}
		return false;
	}

	public void generarTrayectoria(Camino orig, Camino dest) {
		Habitacion h1 = orig.getHabitacion();
		Habitacion h2 = dest.getHabitacion();
		if (h1 == h2) {
			trayectorias
					.add(h1.generarTrayectoria(orig.getFila(), orig.getColumna(), dest.getFila(), dest.getColumna()));
		} else if (h1 != h2 && adyacencias.get(h1).contains(h2)) {
			Trayectoria t = null;
			for (Puerta p : puertas) {
				if (p.getH1() == h1 && p.getH2() == h2 || p.getH1() == h2 && p.getH2() == h1) {
					for (Baldosa b : h1.getBaldosas()) {
						if (b.contiene(p.getX(), p.getY()) || b.contiene(p.getX() + p.getLargo(), p.getY())
								|| b.contiene(p.getX(), p.getY() + p.getAlto())) {
							t = h1.generarTrayectoria(orig.getFila(), orig.getColumna(), b.getFila(), b.getColumna());
						}
					}
					for (Baldosa b : h2.getBaldosas()) {
						if (b.contiene(p.getX(), p.getY()) || b.contiene(p.getX() + p.getLargo(), p.getY())
								|| b.contiene(p.getX(), p.getY() + p.getAlto())) {
							t.anexar(h2.generarTrayectoria(b.getFila(), b.getColumna(), dest.getFila(),
									dest.getColumna()));
						}
					}
				}
				break;
			}
			trayectorias.add(t);
		} else {
			Set<Habitacion> cerca = new HashSet<Habitacion>();
			cerca.add(h2);
			cerca.addAll(adyacencias.get(h2));
			cerca.retainAll(adyacencias.get(h1));
			if (!cerca.isEmpty()) {
				Iterator<Habitacion> azar = cerca.iterator();
				Habitacion h3 = azar.next();
				Baldosa inicioTemp = null;
				Trayectoria t = null;
				for (Puerta p : puertas) {
					if (p.getH1() == h1 && p.getH2() == h3 || p.getH1() == h3 && p.getH2() == h1) {
						for (Baldosa b : h1.getBaldosas()) {
							if (b.contiene(p.getX(), p.getY()) || b.contiene(p.getX() + p.getLargo(), p.getY())
									|| b.contiene(p.getX(), p.getY() + p.getAlto())) {
								t = h1.generarTrayectoria(orig.getFila(), orig.getColumna(), b.getFila(),
										b.getColumna());
							}
						}
						for (Baldosa b : h3.getBaldosas()) {
							if (b.contiene(p.getX(), p.getY()) || b.contiene(p.getX() + p.getLargo(), p.getY())
									|| b.contiene(p.getX(), p.getY() + p.getAlto())) {
								inicioTemp = b;
							}
						}
					}
				}
				for (Puerta p : puertas) {
					if (p.getH1() == h2 && p.getH2() == h3 || p.getH1() == h3 && p.getH2() == h2) {
						for (Baldosa b : h3.getBaldosas()) {
							if (b.contiene(p.getX(), p.getY()) || b.contiene(p.getX() + p.getLargo(), p.getY())
									|| b.contiene(p.getX(), p.getY() + p.getAlto())) {
								t.anexar(h3.generarTrayectoria(inicioTemp.getFila(), inicioTemp.getColumna(),
										b.getFila(), b.getColumna()));
							}
						}
						for (Baldosa b : h2.getBaldosas()) {
							if (b.contiene(p.getX(), p.getY()) || b.contiene(p.getX() + p.getLargo(), p.getY())
									|| b.contiene(p.getX(), p.getY() + p.getAlto())) {
								t.anexar(h2.generarTrayectoria(b.getFila(), b.getColumna(), dest.getFila(),
										dest.getColumna()));
							}
						}
					}
				}
				trayectorias.add(t);
			}
		}
	}

	public void agregarPuerta(int x, int y, int largo, int alto, Habitacion h1, Habitacion h2) {
		puertas.add(new Puerta(x, y, largo, alto, h1, h2));
		if (!adyacencias.containsKey(h1)) {
			adyacencias.put(h1, new HashSet<Habitacion>());
		}
		adyacencias.get(h1).add(h2);
		if (!adyacencias.containsKey(h2)) {
			adyacencias.put(h2, new HashSet<Habitacion>());
		}
		adyacencias.get(h2).add(h1);
	}

	public void eliminarPuerta(Puerta p) {
		puertas.remove(p);

	}

	public Habitacion buscarId(int id) {
		for (Habitacion h : habitaciones) {
			if (h.getId() == id) {
				return h;
			}
		}
		return null;
	}

	public Habitacion baldosaDentro(Baldosa b) {
		for (Habitacion h : habitaciones) {
			if (h.contieneB(b)) {
				return h;
			}
		}
		return null;
	}

	public List<Habitacion> contiene(MouseEvent e) {
		List<Habitacion> l = new ArrayList<Habitacion>();
		for (Habitacion h : habitaciones) {
			if (h.contiene(e.getX(), e.getY())) {
				l.add(h);
			}
		}
		return l;
	}

	public Puerta contieneP(MouseEvent e) {
		for (Puerta p : puertas) {
			if (p.contiene(e.getX(), e.getY())) {
				return p;
			}
		}
		return null;
	}

	public List<Habitacion> cruza(Habitacion o) {
		List<Habitacion> l = new ArrayList<Habitacion>();
		for (Habitacion h : habitaciones) {
			if (o.intersecta(h)) {
				l.add(h);
			}
		}
		return l;
	}

	public List<Habitacion> getHabitaciones() {
		return habitaciones;
	}

	public void setHabitaciones(List<Habitacion> habitaciones) {
		this.habitaciones = habitaciones;
	}

	public List<Puerta> getPuertas() {
		return puertas;
	}

	public List<Trayectoria> getTrayectorias() {
		return trayectorias;
	}

	public Map<Habitacion, Set<Habitacion>> getAdyacencias() {
		return adyacencias;
	}

	public Grafo getGrafo() {
		return grafo;
	}

	public void setGrafo(Grafo grafo) {
		this.grafo = grafo;
	}

}
