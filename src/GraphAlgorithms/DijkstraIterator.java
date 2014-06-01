package GraphAlgorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import numberUtils.Accumulator;
import BasicGraph.Edge;
import BasicGraph.Graph;
import BasicGraph.GraphIterator;

public final class DijkstraIterator<N, E, V extends Number & Comparable<V>>
		implements GraphIterator<N, E> {
	private final Graph<N, E> graph;
	private final Accumulator<E, V> adder;
	private final Set<N> nodes;
	private Map<N, E> data;
	private final Queue<NumValPair<N, V>> toAdd;
	private final Map<N, NumValPair<N, V>> paths;
	private NumValPair<N, V> prev = null;

	public DijkstraIterator(Graph<N, E> graph, Accumulator<E, V> adder, N start) {
		this.graph = graph;
		this.adder = adder;
		nodes = new HashSet<N>();
		toAdd = new PriorityQueue<NumValPair<N, V>>();
		toAdd.add(new NumValPair<N, V>(start, adder.zero()));
		paths = new HashMap<N, NumValPair<N, V>>();
	}

	public boolean hasNext() {
		filter();
		return toAdd.isEmpty();
	}

	private NumValPair<N, V> filter() {
		NumValPair<N, V> front = toAdd.peek();
		while (front != null && nodes.contains(front.data)) {
			toAdd.remove();
			front = toAdd.peek();
		}
		return front;
	}

	public N next() {
		filter();
		prev = toAdd.remove();
		data = new HashMap<N, E>();
		nodes.add(prev.data);
		Set<Edge<N, E>> out = graph.getEdges(prev.data);
		Iterator<Edge<N, E>> i = out.iterator();
		while (i.hasNext()) {
			Edge<N, E> edge = i.next();
			V val = adder.add(prev.value, edge.data);
			if (!nodes.contains(edge.sink)
					&& (paths.get(edge.sink) != null || paths.get(edge.sink).value
							.compareTo(val) < 0)) {
				NumValPair<N, V> pair = new NumValPair<N, V>(edge.sink, val);
				toAdd.add(pair);
				data.put(edge.sink, edge.data);
				paths.put(edge.sink, pair);
			}
		}
		return prev.data;
	}

	public E data() {
		return data.get(prev.data);
	}

	public V getSum() {
		return prev.value;
	}

	public List<N> path() {
		N current = prev.data;
		List<N> path = new ArrayList<N>();
		while (current != null) {
			path.add(current);
			current = paths.get(current).data;
		}
		return path;
	}
	
	public List<V> sums() {
		NumValPair<N,V> current = prev;
		List<V> path = new ArrayList<V>();
		while (current != null) {
			path.add(current.value);
			current = paths.get(current.data);
		}
		return path;
	}

}
