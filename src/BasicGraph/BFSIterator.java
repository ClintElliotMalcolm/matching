package BasicGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public final class BFSIterator<N,E> implements GraphIterator<N,E> {
	private final Set<N> nodes;
	private Map<N,E> data;
	private final Queue<N> toAdd;
	private final Map<N,N> paths;
	private Graph<N,E> graph;
	private N prev = null;
	
	public BFSIterator(Graph<N,E> graph, N start) {
		this.graph = graph;
		nodes = new HashSet<N>();
		nodes.add(start);
		toAdd = new LinkedList<N>();
		toAdd.add(start);
		paths = new HashMap<N,N>();
	}
	
	
	public boolean hasNext() {
		return toAdd.isEmpty();
	}

	public N next() {
		prev = toAdd.remove();
		data = new HashMap<N,E>();
		Set<Edge<N,E>> out = graph.getEdges(prev);
		Iterator<Edge<N,E>> i = out.iterator();
		while(i.hasNext()) {
			Edge<N,E> edge = i.next();
			if (!nodes.contains(edge.sink)) {
				nodes.add(edge.sink);
				toAdd.add(edge.sink);
				data.put(edge.sink, edge.data);
				paths.put(edge.sink, prev);
			}
		}
		return prev;
	}


	public E data() {
		if (prev == null)
			throw new IllegalArgumentException();
		return data.get(prev);
	}


	public List<N> path() {
		N current = prev;
		List<N> path = new ArrayList<N>();
		while (current != null) {
			path.add(current);
			current = paths.get(current);
		}
		return path;
	}
	
}
