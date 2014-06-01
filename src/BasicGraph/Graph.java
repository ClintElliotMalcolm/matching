package BasicGraph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Graph<N, E> {
	private Map<N, EdgeCollection<N, E>> adjancency;

	public Graph() {
		adjancency = new HashMap<N, EdgeCollection<N, E>>();
	}

	public void addNode(N node) {
		adjancency.put(node, new EdgeCollection<N, E>());
	}
	
	public Set<N> nodes() {
		return new HashSet<N>(adjancency.keySet());
	}

	public void addEdge(N from, N to, E data) {
		addEdge(new Edge<N, E>(from, to, data));
	}
	
	public void addEdge(Edge<N,E> e) {
		if (adjancency.get(e.source) == null || adjancency.get(e.sink) == null)
			throw new IllegalArgumentException();
		adjancency.get(e.source).addEdge(e);
	}
	
	public E setEdge(N from, N to, E data) {
		return setEdge(new Edge<N, E>(from, to, data));
	}
	
	public E setEdge(Edge<N,E> e) {
		if (adjancency.get(e.source) == null || adjancency.get(e.sink) == null)
			throw new IllegalArgumentException();
		return adjancency.get(e.source).setEdge(e);
	}

	public E removeEdge(N from, N to) {
		if (adjancency.get(from) == null || adjancency.get(to) == null)
			throw new IllegalArgumentException();
		return adjancency.get(from).removeEdge(to);
	}
	
	public Set<Edge<N,E>> getEdges(N from) {
		if (adjancency.get(from) == null)
			throw new IllegalArgumentException();
		return new HashSet<Edge<N,E>>(adjancency.get(from).getEdges());
	}
	
	public Edge<N,E> getEdgesBetween(N from, N to) {
		if (adjancency.get(from) == null && adjancency.get(to) == null)
			throw new IllegalArgumentException();
		return adjancency.get(from).getEdgeBySink(to);
	}
	
	public Set<Edge<N,E>> getEdgesWithData(N from, E data) {
		if (adjancency.get(from) == null)
			throw new IllegalArgumentException();
		return new HashSet<Edge<N,E>>(adjancency.get(from).getEdgeByData(data));
	}

	private static class EdgeCollection<N, E> {
		private Map<N, Edge<N, E>> byNode;
		private Map<E, Set<Edge<N, E>>> byEdge;
		private Set<Edge<N,E>> allEdges;

		public EdgeCollection() {
			byNode = new HashMap<N, Edge<N, E>>();
			byEdge = new HashMap<E, Set<Edge<N, E>>>();
			allEdges = new HashSet<Edge<N,E>>();
		}

		public void addEdge(Edge<N, E> e) {
			byNode.put(e.sink, e);
			ensuredAddToSet(byEdge, e.data, e);
			allEdges.add(e);
		}
		
		public Set<Edge<N,E>> getEdges() {
			return allEdges;
		}

		public Edge<N, E> getEdgeBySink(N node) {
			return byNode.get(node);
		}

		public Set<Edge<N, E>> getEdgeByData(E data) {
			return byEdge.get(data);
		}

		public E removeEdge(N to) {
			Edge<N,E> e = byNode.get(to);
			byNode.remove(to);
			ensuredRemoveFromSet(byEdge, e.data, e);
			allEdges.remove(e);
			return e.data;
		}
		
		public E setEdge(Edge<N, E> e) {
			Edge<N,E> old = byNode.get(e.sink);
			byNode.put(e.sink, e);
			ensuredReplaceFromSet(byEdge, e.data, old, e);
			allEdges.remove(e);
			return old.data;
		}

		private static <K, V> void ensuredAddToSet(Map<K, Set<V>> map, K key,
				V val) {
			if (map.get(key) == null) {
				map.put(key, new HashSet<V>());
			}
			map.get(key).add(val);
		}
		
		private static <K, V> void ensuredReplaceFromSet(Map<K, Set<V>> map, K key, V old,
				V newVal) {
			if (map.get(key) != null) {
				Set<V> set = map.get(key);
				set.remove(old);
				set.add(newVal);
			}
		}

		private static <K, V> void ensuredRemoveFromSet(Map<K, Set<V>> map,
				K key, V val) {
			if (map.get(key) == null) {
				map.put(key, new HashSet<V>());
			}
			map.get(key).remove(val);
		}
	}
}
