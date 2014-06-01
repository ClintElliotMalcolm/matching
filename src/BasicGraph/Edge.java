package BasicGraph;

public class Edge<N, E> {
	public N source;
	public N sink;
	public E data;

	public Edge(N source, N sink, E data) {
		this.source = source;
		this.sink = sink;
		this.data = data;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Edge<?, ?>))
			return false;
		Edge<?, ?> other = (Edge<?, ?>) o;
		return other.source.equals(source) && other.sink.equals(sink)
				&& other.data.equals(data);
	}
}
