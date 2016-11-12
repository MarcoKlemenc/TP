import java.util.Comparator;
import java.util.HashSet;
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

	public class NodeCompator implements Comparator<Nodo> {
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
		final Queue<Nodo> queue = new PriorityQueue<Nodo>(10, new NodeCompator());

		for (Entry<Nodo, ArrayList<Arista>> entry : graph.getGraph().entrySet()) {
			Nodo currNode = entry.getKey();
			if (currNode.getValue() == source) {
				currNode.setDistance(0);
				queue.add(currNode);
			}
		}

		final Set<Nodo> doneSet = new HashSet<Nodo>();

		while (!queue.isEmpty()) {
			Nodo src = queue.poll();
			if (!doneSet.contains(src)) {
				doneSet.add(src);

				for (Arista edge : graph.getAdj(src)) {
					Nodo currentNode = edge.getAdjacentNode(src);

					if (!doneSet.contains(currentNode)) {
						int newDistance = src.getDistance() + edge.getDistance();
						if (newDistance < currentNode.getDistance()) {
							currentNode.setDistance(newDistance);
							queue.add(currentNode);
						}
					}
				}
			}
		}

		return doneSet;
	}

//	public static void main(String[] args) {
//		Graphs G = new Graphs(7);
//		G.addEdge(new Node(0), new Node(1), 4);
//		G.addEdge(new Node(0), new Node(2), 3);
//		G.addEdge(new Node(0), new Node(4), 7);
//		G.addEdge(new Node(1), new Node(3), 5);
//		G.addEdge(new Node(2), new Node(3), 11);
//		G.addEdge(new Node(4), new Node(3), 2);
//		G.addEdge(new Node(5), new Node(3), 2);
//		G.addEdge(new Node(6), new Node(3), 10);
//		G.addEdge(new Node(4), new Node(6), 5);
//		G.addEdge(new Node(6), new Node(5), 3);

//		Dijkstra1 dijkstra = new Dijkstra1(G);
//		Set<Node> path = dijkstra.findShortest(0);

//		Iterator<Node> it = path.iterator();
//		while (it.hasNext()) {
//			System.out.println(it.next().getDistance());
//		}
//	}

}
