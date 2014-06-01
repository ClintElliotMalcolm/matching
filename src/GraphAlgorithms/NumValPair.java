package GraphAlgorithms;

public class NumValPair<N, V extends Number & Comparable<V>> implements
		Comparable<NumValPair<N, V>> {
	N data;
	V value;

	public NumValPair(N data, V value) {
		this.data = data;
		this.value = value;
	}

	public int compareTo(NumValPair<N, V> other) {
		return value.compareTo(other.value);
	}
}
