
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.ArrayList;
import java.util.Map.Entry;

public class Dijkstra {

    private final Grafo graph;

    public Dijkstra(Grafo graph) {
        if (graph == null) {
            throw new NullPointerException("The input graph cannot be null.");
        }
        this.graph = graph;
    }

    public class NodoCompator implements Comparator<Nodo>  {
        @Override
        public int compare(Nodo n1, Nodo n2) {
            if (n1.getDistance() > n2.getDistance()) {
                return 1;
            } else {
                return -1;
            }
        }
    };

    public Set<Nodo> findShortest(int source) {
        final Queue<Nodo> queue = new PriorityQueue<Nodo>(10, new NodoCompator());

        for (Entry<Nodo, ArrayList<Arista>> entry :  graph.getGraph().entrySet()) {
            Nodo currNodo = entry.getKey();
            /*if (currNodo.getValue() == source) {
                currNodo.setDistance(0);
                queue.add(currNodo);
            }*/ 
        }

        final Set<Nodo> doneSet = new HashSet<Nodo>();

        while (!queue.isEmpty()) {
            Nodo src = queue.poll();
            if (!doneSet.contains(src)){
                doneSet.add(src);

                for (Arista Arista : graph.getAdj(src)) {
                    Nodo currentNodo = Arista.getAdjacentNode(src);

                    if (!doneSet.contains(currentNodo)) {
                        int newDistance = src.getDistance() + Arista.getDistance();
                        if (newDistance < currentNodo.getDistance()) {
                            currentNodo.setDistance(newDistance);
                            queue.add(currentNodo);
                        } 
                    }
                }
            }
        }

        return doneSet;
    }

    /*public static void main(String[] args) {
    	Graphs G = new Graphs(7);
    	G.addArista(new Nodo(0), new Nodo(1), 4);
    	G.addArista(new Nodo(0), new Nodo(2), 3);
    	G.addArista(new Nodo(0), new Nodo(4), 7);
    	G.addArista(new Nodo(1), new Nodo(3), 5);
    	G.addArista(new Nodo(2), new Nodo(3), 11);
    	G.addArista(new Nodo(4), new Nodo(3), 2);
    	G.addArista(new Nodo(5), new Nodo(3), 2);
    	G.addArista(new Nodo(6), new Nodo(3), 10);
    	G.addArista(new Nodo(4), new Nodo(6), 5);
    	G.addArista(new Nodo(6), new Nodo(5), 3);

    	Dijkstra1 dijkstra = new Dijkstra1(G);
    	Set<Nodo> path = dijkstra.findShortest(0);

    	Iterator<Nodo> it = path.iterator();
    	while (it.hasNext()) {
        	System.out.println(it.next().getDistance());
    	}
	}*/
}