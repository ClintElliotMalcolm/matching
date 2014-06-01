package numberUtils;

public interface ToInteger<E> extends ToNum<E, Integer>{
	public int toInt(E val);
}
