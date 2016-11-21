import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class Piso {

	private List<Habitacion> habitaciones = new ArrayList<Habitacion>();
	private List<Puerta> puertas = new ArrayList<Puerta>();
	private List<Trayectoria> trayectorias = new ArrayList<Trayectoria>();
	private Map<Habitacion, Set<Habitacion>> adyacencias = new HashMap<Habitacion, Set<Habitacion>>();

	public void agregarHabitacion(Habitacion h) {
		habitaciones.add(h);
		adyacencias.put(h, new HashSet<Habitacion>());
	}

	public void agregarPuerta(StringTokenizer s) {
		agregarPuerta(Integer.parseInt(s.nextToken()), Integer.parseInt(s.nextToken()), Integer.parseInt(s.nextToken()),
				Integer.parseInt(s.nextToken()), getHabitacion(Integer.parseInt(s.nextToken())),
				getHabitacion(Integer.parseInt(s.nextToken())), Boolean.valueOf(s.nextToken()));
	}

	public void agregarPuerta(int x, int y, int largo, int alto, Habitacion h1, Habitacion h2, boolean vertical) {
		puertas.add(new Puerta(x, y, largo, alto, h1, h2, vertical));
		adyacencias.get(h1).add(h2);
		adyacencias.get(h2).add(h1);
	}
	
	public Habitacion baldosaDentro(Baldosa b) {
		for (Habitacion h : habitaciones) {
			if (h.contieneBaldosa(b)) {
				return h;
			}
		}
		return null;
	}

	public boolean contienePuertaV(Habitacion h1, Habitacion h2, MouseEvent e) {
		return h1 != h2 && h1.contiene(e.getX() - 5, e.getY()) && h2.contiene(e.getX() + 5, e.getY());
	}

	public boolean contienePuertaH(Habitacion h1, Habitacion h2, MouseEvent e) {
		return h1 != h2 && h1.contiene(e.getX(), e.getY() - 5) && h2.contiene(e.getX(), e.getY() + 5);
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

	public void eliminarHabitacion(Habitacion h) {
		List<Puerta> pp = new ArrayList<Puerta>();
		for (Puerta p : puertas) {
			if (p.getH1() == h || p.getH2() == h) {
				pp.add(p);
			}
		}
		for (Set<Habitacion> s : adyacencias.values()) {
			s.remove(h);
		}
		List<Trayectoria> tt = new ArrayList<Trayectoria>();
		for (Trayectoria t : trayectorias) {
			if (t.getHabitaciones().contains(h)) {
				tt.add(t);
			}
		}
		puertas.removeAll(pp);
		trayectorias.removeAll(tt);
		adyacencias.remove(h);
		habitaciones.remove(h);
	}

	public void eliminarPuerta(Puerta p) {
		puertas.remove(p);
		Habitacion h1 = p.getH1();
		Habitacion h2 = p.getH2();
		for (Puerta pu : puertas) {
			if (pu.getH1() == h1 && pu.getH2() == h2) {
				return;
			}
		}
		adyacencias.get(h1).remove(h2);
		adyacencias.get(h2).remove(h1);
	}

	public boolean eliminarTrayectoria(MouseEvent e, Baldosa b) {
		Trayectoria t = getTrayectoria(e, b);
		if (t != null) {
			trayectorias.remove(t);
			return true;
		}
		return false;
	}

	public void generarTrayectoria(Recorrido orig, Recorrido dest) {
		Habitacion h1 = orig.getHabitacion();
		Habitacion h2 = dest.getHabitacion();
		if (h1 == h2) {
			trayectorias
					.add(h1.generarTrayectoria(orig.getFila(), orig.getColumna(), dest.getFila(), dest.getColumna()));
		} else if (h1 != h2 && adyacencias.containsKey(h1) && adyacencias.get(h1).contains(h2)) {
			Trayectoria t = null;
			for (Puerta p : puertas) {
				if (p.getH1() == h1 && p.getH2() == h2 || p.getH1() == h2 && p.getH2() == h1) {
					for (Baldosa b : h1.getBaldosas()) {
						if (b.contiene(p)) {
							t = h1.generarTrayectoria(orig.getFila(), orig.getColumna(), b.getFila(), b.getColumna());
						}
					}
					for (Baldosa b : h2.getBaldosas()) {
						if (b.contiene(p)) {
							t.anexar(h2.generarTrayectoria(b.getFila(), b.getColumna(), dest.getFila(),
									dest.getColumna()));
						}
					}
				}
			}
			trayectorias.add(t);
		} else if (adyacencias.containsKey(h1)) {
			Set<Habitacion> cerca = new HashSet<Habitacion>();
			cerca.add(h2);
			cerca.addAll(adyacencias.get(h2));
			cerca.retainAll(adyacencias.get(h1));
			if (!cerca.isEmpty()) {
				Habitacion h3 = (Habitacion) cerca.toArray()[0];
				Baldosa inicioTemp = null;
				Trayectoria t = null;
				for (Puerta p : puertas) {
					if (p.getH1() == h1 && p.getH2() == h3 || p.getH1() == h3 && p.getH2() == h1) {
						for (Baldosa b : h1.getBaldosas()) {
							if (b.contiene(p)) {
								t = h1.generarTrayectoria(orig.getFila(), orig.getColumna(), b.getFila(),
										b.getColumna());
								break;
							}
						}
						for (Baldosa b : h3.getBaldosas()) {
							if (b.contiene(p)) {
								inicioTemp = b;
								break;
							}
						}
					}
				}
				for (Puerta p : puertas) {
					if (p.getH1() == h2 && p.getH2() == h3 || p.getH1() == h3 && p.getH2() == h2) {
						for (Baldosa b : h3.getBaldosas()) {
							if (b.contiene(p)) {
								t.anexar(h3.generarTrayectoria(inicioTemp.getFila(), inicioTemp.getColumna(),
										b.getFila(), b.getColumna()));
								break;
							}
						}
						for (Baldosa b : h2.getBaldosas()) {
							if (b.contiene(p)) {
								t.anexar(h2.generarTrayectoria(b.getFila(), b.getColumna(), dest.getFila(),
										dest.getColumna()));
								break;
							}
						}
					}
				}
				trayectorias.add(t);
			}
		}
	}

	public Habitacion getHabitacion(int id) {
		for (Habitacion h : habitaciones) {
			if (h.getId() == id) {
				return h;
			}
		}
		return null;
	}

	public List<Habitacion> getHabitaciones(MouseEvent e) {
		List<Habitacion> l = new ArrayList<Habitacion>();
		for (Habitacion h : habitaciones) {
			if (h.contiene(e.getX(), e.getY())) {
				l.add(h);
			}
		}
		return l;
	}

	public Puerta getPuerta(MouseEvent e) {
		for (Puerta p : puertas) {
			if (p.contiene(e.getX(), e.getY())) {
				return p;
			}
		}
		return null;
	}

	public Trayectoria getTrayectoria(MouseEvent e, Baldosa b) {
		for (Trayectoria t : trayectorias) {
			for (Habitacion h : habitaciones) {
				if (t.buscar(h, b.getCoordenadas()) && t.getHabitaciones().contains(h)
						&& h.contiene(e.getX(), e.getY())) {
					return t;
				}
			}
		}
		return null;
	}

	public List<Habitacion> getHabitaciones() {
		return habitaciones;
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
}
