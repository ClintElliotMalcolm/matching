package matchingAlgorithms;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import numberUtils.NumberWrapper;
import numberUtils.ToNum;

public class HungarianMatcher<A, B, V extends Number & Comparable<V>>
		implements Matcher<A, B> {
	private final Map<Integer, A> colVal;
	private final Map<Integer, B> rowVal;
	private final NumberWrapper<V>[][] grid;
	private Set<Pair<A, B>> matchings = null;

	@SuppressWarnings("unchecked")
	public HungarianMatcher(Set<A> as, Set<B> bs, ToNum<Pair<A, B>, V> evaluator) {
		int dimension = Math.max(as.size(), bs.size());
		this.grid = (NumberWrapper<V>[][]) new NumberWrapper[dimension][dimension];
		colVal = new HashMap<Integer, A>();

		// Apply to Indexs
		int i = 0;
		for (A a : as) {
			colVal.put(i, a);
			i++;
		}
		i = 0;
		rowVal = new HashMap<Integer, B>();
		for (B b : bs) {
			rowVal.put(i, b);
			i++;
		}

		// Put in valid numbers
		NumberWrapper<V> max = null;
		for (i = 0; i < colVal.size(); i++) {
			for (int j = 0; j < rowVal.size(); j++) {
				NumberWrapper<V> val = evaluator.toNum(new Pair<A, B>(colVal
						.get(i), rowVal.get(j)));
				grid[i][j] = val;
				if (max == null || max.compareTo(val) < 0) {
					max = val;
				}
			}
		}

		if (colVal.size() != rowVal.size()) {
			// If not square, then make square
			int minI = colVal.size() < dimension ? colVal.size() : 0;
			int minJ = rowVal.size() < dimension ? rowVal.size() : 0;
			for (i = minI; i < dimension; i++) {
				for (int j = minJ; j < dimension; j++) {
					grid[i][j] = max;
				}
			}
		}
	}
	
	public HungarianMatcher(Map<Integer, A> aMap, Map<Integer, B> bMap, NumberWrapper<V>[][] values) {
		grid = values;
		colVal = aMap;
		rowVal = bMap;
	}
	
	private int countZeroes() {
		NumberWrapper<V> zero = grid[0][0].zero();
		int count = 0;
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid.length; j++) {
				if (grid[i][j].compareTo(zero) == 0) {
					count++;
				}
			}
		}
		return count;
	}

	public Set<Pair<A, B>> match() {
		if (matchings != null) {
			return matchings;
		}
		printNumberWrapperGrid(grid);
		initializeFirstResult();
		Set<Pair<Integer, Integer>> assignment = new HashSet<Pair<Integer, Integer>>();
		Pair<Set<Integer>, Set<Integer>> covers = getAssignments(assignment);
		int count = 0;
		while (assignment.size() != grid.length) {
			System.out.println(count);
			//printNumberWrapperGrid(grid);
			int prevZeroes = countZeroes();
			augment(covers);
			System.out.println("zeroes: " + (countZeroes() - prevZeroes));
			System.out.println("size: " + assignment.size());
			assignment = new HashSet<Pair<Integer, Integer>>();
			covers = getAssignments(assignment);
			count++;
		}
		matchings = new HashSet<Pair<A, B>>();
		for (Pair<Integer, Integer> assign : assignment) {
			int a = assign.getFirst();
			int b = assign.getSecond();

			// Not junk data
			if (a < colVal.size() && b < rowVal.size()) {
				matchings.add(new Pair<A, B>(colVal.get(a), rowVal.get(b)));
			}
		}
		return matchings;
	}

	public void augment(Pair<Set<Integer>, Set<Integer>> covers) {
		Set<Integer> coveredCols = covers.getFirst();
		Set<Integer> coveredRows = covers.getSecond();
		
		NumberWrapper<V> min = null;
		for (int i = 0; i < grid.length; i++) {
			if (!coveredCols.contains(i)) {
				for (int j = 0; j < grid.length; j++) {
					if (!coveredRows.contains(j) && (min == null || min.compareTo(grid[i][j]) > 0)) {
						min = grid[i][j];
					}
				}
			}
		}
		
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid.length; j++) {
				if (coveredCols.contains(i) && coveredRows.contains(j)) {
					grid[i][j] = grid[i][j].add(min);
				} else if (!(coveredCols.contains(i) || coveredRows.contains(j))) {
					grid[i][j] = grid[i][j].subtract(min);
				}
			}
		}
	}

	public Pair<Set<Integer>, Set<Integer>> getAssignments(
			Set<Pair<Integer, Integer>> assignments) {
		NumberWrapper<V> zero = grid[0][0].zero();
		Map<Integer, Set<Integer>> zeroesInRow = new HashMap<Integer, Set<Integer>>();
		Map<Integer, Set<Integer>> zeroesInCol = new HashMap<Integer, Set<Integer>>();
		Set<Integer> coveredCols = new HashSet<Integer>();
		Set<Integer> coveredRows = new HashSet<Integer>();

		for (int i = 0; i < grid.length; i++) {
			if (!zeroesInCol.containsKey(i)) {
				zeroesInCol.put(i, new HashSet<Integer>());
			}
			for (int j = 0; j < grid.length; j++) {
				if (!zeroesInRow.containsKey(j)) {
					zeroesInRow.put(j, new HashSet<Integer>());
				}
				if (grid[i][j].compareTo(zero) == 0) {
					zeroesInCol.get(i).add(j);
					zeroesInRow.get(j).add(i);
				}
			}
		}
		while (!allEmpty(zeroesInRow) || !allEmpty(zeroesInCol)) {
			boolean gotOneZero = false;
			for (int i = 0; i < grid.length && (!allEmpty(zeroesInRow) || !allEmpty(zeroesInCol)); i++) {
				if (zeroesInRow.get(i).size() == 1) {
					if (!conflicts(assignments, false, i)) {
						gotOneZero = true;
						removeZeroes(zeroesInCol, zeroesInRow, i, true,
								assignments, coveredCols, coveredRows);
					} else {
						firstNonConflicting(zeroesInRow, )
					}
				}
				if (zeroesInCol.get(i).size() == 1 && !conflicts(assignments, true, i)) {
					gotOneZero = true;
					removeZeroes(zeroesInCol, zeroesInRow, i, false,
							assignments, coveredCols, coveredRows);
				}
			}
			if (!gotOneZero && (!allEmpty(zeroesInRow) || !allEmpty(zeroesInCol))) {
				int maxZeroes = -1;
				int index = -1;
				boolean lineThroughCol = false;
				for (int i = 0; i < grid.length; i++) {
					if (zeroesInRow.get(i).size() > maxZeroes) {
						int temp = -1;
						Iterator<Integer> iter = zeroesInRow.get(i).iterator();
						boolean hadNext;
						while ((hadNext = iter.hasNext()) && !conflicts(assignments, true, (temp = iter.next())));
						if (hadNext) {
							index = temp;
							lineThroughCol = false;
							maxZeroes = zeroesInRow.get(i).size();
						}
					}
					if (zeroesInCol.get(i).size() > maxZeroes) {
						int temp = -1;
						Iterator<Integer> iter = zeroesInCol.get(i).iterator();
						boolean hadNext;
						while ((hadNext = iter.hasNext()) && !conflicts(assignments, false, (temp = iter.next())));
						if (hadNext) {
							index = temp;
							lineThroughCol = true;
							maxZeroes = zeroesInCol.get(i).size();
						}
					}
				}
				removeZeroes(zeroesInCol, zeroesInRow, index, lineThroughCol,
						assignments, coveredCols, coveredRows);
			}
		}
		return new Pair<Set<Integer>, Set<Integer>>(coveredCols, coveredRows);

	}
	
	public Pair<Integer,Integer> firstNonConflicting(Map<Integer, Set<Integer>> map, boolean throughCol, int i, Set<Pair<Integer,Integer>> assignments) {
		int index = -1;
		int temp = -1;
		Iterator<Integer> iter = map.get(i).iterator();
		boolean hadNext;
		while ((hadNext = iter.hasNext()) && !conflicts(assignments, throughCol, (temp = iter.next())));
		if (hadNext) {
			index = temp;
		}
		return new Pair<Integer,Integer>(index, map.get(i).size());
	}
	
	public boolean conflicts(Set<Pair<Integer,Integer>> assignments, boolean isRow, int index) {
		for (Pair<Integer,Integer> assign : assignments) {
			if ((isRow ? assign.getSecond() : assign.getFirst()) == index)
				return true;
		}
		return false;
	}

	public void removeZeroes(Map<Integer, Set<Integer>> zeroesInCol,
			Map<Integer, Set<Integer>> zeroesInRow, int index,
			boolean lineThroughCol, Set<Pair<Integer, Integer>> assignments,
			Set<Integer> coveredCols, Set<Integer> coveredRows) {
		Map<Integer, Set<Integer>> other = lineThroughCol ? zeroesInCol
				: zeroesInRow;
		Map<Integer, Set<Integer>> me = lineThroughCol ? zeroesInRow
				: zeroesInCol;
		Set<Integer> coverMe = lineThroughCol ? coveredRows : coveredCols;
		Set<Integer> coverOther = lineThroughCol ? coveredCols : coveredRows;
		int chosen =  me.get(index).iterator().next();
		assignments.add(lineThroughCol ? new Pair<Integer, Integer>(chosen,
				index) : new Pair<Integer, Integer>(index, chosen));
		coverOther.add(chosen);
		for(Integer covered : other.get(chosen)) {
			me.get(covered).remove(chosen);
		}
		other.get(chosen).clear();
		me.get(index).remove(chosen);
	}

	public static boolean allEmpty(Map<Integer, Set<Integer>> map) {
		for (Integer i : map.keySet()) {
			if (map.get(i).size() != 0) {
				return false;
			}
		}
		return true;
	}

	public void initializeFirstResult() {
		for (int i = 0; i < grid.length; i++) {
			NumberWrapper<V> rowMin = getLeastInRow(i);
			for (int j = 0; j < grid.length; j++) {
				grid[j][i] = grid[j][i].subtract(rowMin);
			}
		}
		for (int i = 0; i < grid.length; i++) {
			NumberWrapper<V> colMin = getLeastInCol(i);
			for (int j = 0; j < grid.length; j++) {
				grid[i][j] = grid[i][j].subtract(colMin);
			}
		}
	}

	public NumberWrapper<V> getLeastInRow(int row) {
		NumberWrapper<V> min = grid[0][row];
		for (int i = 1; i < grid.length; i++) {
			NumberWrapper<V> candidate = grid[i][row];
			if (min.compareTo(candidate) > 0) {
				min = candidate;
			}
		}
		return min;
	}

	public NumberWrapper<V> getLeastInCol(int col) {
		NumberWrapper<V> min = grid[col][0];
		for (int i = 1; i < grid.length; i++) {
			NumberWrapper<V> candidate = grid[col][i];
			if (min.compareTo(candidate) > 0) {
				min = candidate;
			}
		}
		return min;
	}

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		/*
		String[] as = { "alice", "bob", "cynthia" };
		String[] bs = { "x", "y", "zoo"};
		ToNum<Pair<String, String>, Integer> toNum = new ToNum<Pair<String, String>, Integer>() {
			public NumberWrapper<Integer> toNum(Pair<String, String> obj) {
				return new IntegerWrapper(obj.getFirst().length()
						* obj.getSecond().length());
			}
		};
		HungarianMatcher<String, String, Integer> matcher = new HungarianMatcher<String, String, Integer>(
				new HashSet<String>(Arrays.asList(as)), new HashSet<String>(
						Arrays.asList(bs)), toNum);
		*/
		Map<Integer,Integer> names = new HashMap<Integer,Integer>();
		int size = 110;
		for (int i = 0; i < size; i++) {
			names.put(i, i);
		}
		NumberWrapper<Integer>[][] values = (NumberWrapper<Integer>[][]) new NumberWrapper[size][size];
		Random r = new Random();
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				values[i][j] = new IntegerWrapper(r.nextInt(Integer.MAX_VALUE));
			}
		}
		HungarianMatcher<Integer, Integer,Integer> matcher = new HungarianMatcher<Integer,Integer,Integer>(names, names, (NumberWrapper<Integer>[][]) values);
		//printNumberWrapperGrid(matcher.grid);
		long start = System.currentTimeMillis();
		System.out.println(matcher.match());
		System.out.println(System.currentTimeMillis() - start);
		//printNumberWrapperGrid(matcher.grid);
	}

	public static <N extends Number & Comparable<N>> void printNumberWrapperGrid(
			NumberWrapper<N>[][] grid) {
		for (int i = 0; i < grid.length; i++) {
			System.out.print("[");
			for (int j = 0; j < grid[i].length; j++) {
				System.out.print(grid[i][j].unwrap() + ", ");
			}
			System.out.println("]");
		}
	}

	private static class IntegerWrapper implements NumberWrapper<Integer> {
		private Integer val;
		private static final Map<Integer, IntegerWrapper> instances = new HashMap<Integer, IntegerWrapper>();

		private IntegerWrapper(int a) {
			val = a;
		}

		public IntegerWrapper getWrapper(int a) {
			if (instances.get(a) == null) {
				instances.put(a, new IntegerWrapper(a));
			}
			return instances.get(a);
		}

		public int compareTo(NumberWrapper<Integer> arg0) {
			return val.compareTo(arg0.unwrap());
		}

		public IntegerWrapper add(NumberWrapper<Integer> b) {
			return getWrapper(b.unwrap() + val);
		}

		public IntegerWrapper subtract(NumberWrapper<Integer> b) {
			return getWrapper(val - b.unwrap());
		}

		public IntegerWrapper zero() {
			return getWrapper(0);
		}

		public Integer unwrap() {
			return val;
		}

		@Override
		public String toString() {
			return "" + val;
		}

	}
}
