package GraphAlgorithms;

import java.util.ArrayList;
import java.util.List;

import numberUtils.ToInteger;
import BasicGraph.Edge;
import BasicGraph.Graph;

public final class MaxFlow {
	public static <N,E> Graph<N,E> FordFulkerson(Graph<N,E> graph, ToInteger<E> toInt) {
		Graph<N,Integer> residual = new Graph<N,Integer>();
		for(N node : graph.nodes()) {
			residual.addNode(node);
		}
		for(N node : graph.nodes()) {
			for (Edge<N, E> edge : graph.getEdges(node)) {
				residual.addEdge(node, edge.sink, toInt.toInt(edge.data));
			}
		}
		
		List<N> path = new ArrayList<N>();
		path = findPath(residual, path);
		while (path != null) {
			
		}
		
	}
	
	private static <N> List<N> findPath(Graph<N, Integer> residual, List<N> path) {
		return null;
	}
}
