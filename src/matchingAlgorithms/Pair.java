package matchingAlgorithms;

public class Pair<A,B> {
	private final A first;
	private final B second;
	
	public Pair(A first, B second) {
		this.first = first;
		this.second = second;
	}
	
	public A getFirst() {
		return first;
	}
	
	public B getSecond() {
		return second;
	}
	
	@Override
	public int hashCode() {
		return first.hashCode() ^ second.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Pair<?,?>))
			return false;
		Pair<?,?> other = (Pair<?,?>)o;
		return other.first.equals(first) && other.second.equals(second);
	}
	
	@Override
	public String toString() {
		return "(" + first + ", " + second + ")";
	}
}
