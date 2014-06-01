package numberUtils;

public interface ToNum<E, N extends Number & Comparable<N>> {
	public NumberWrapper<N> toNum(E obj);
}
