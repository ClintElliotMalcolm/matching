package matchingAlgorithms;

import java.util.Set;

public interface Matcher<A,B> {
	public Set<Pair<A,B>> match();
}
