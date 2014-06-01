package numberUtils;

public interface NumberWrapper<V extends Number & Comparable<V>> extends Comparable<NumberWrapper<V>> {
	public NumberWrapper<V> add(NumberWrapper<V> b);
	public NumberWrapper<V> subtract(NumberWrapper<V> b);
	public NumberWrapper<V> zero();
	public V unwrap();
}
