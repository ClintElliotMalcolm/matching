package BasicGraph;

import java.util.Iterator;
import java.util.List;

public interface GraphIterator<N, E> extends Iterator<N> {
	public E data();
	public List<N> path();
}
