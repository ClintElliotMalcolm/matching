package numberUtils;

public interface Accumulator<E, N extends Number & Comparable<N>> {
	public N add(N sum, E next);
	public N zero();
}
