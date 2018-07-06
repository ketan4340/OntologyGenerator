package util.tuple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class Tuple extends ArrayList<String> {
	private static final long serialVersionUID = -201286752453864379L;

	/* ================================================== */
	/* ==========          Constructor         ========== */
	/* ================================================== */
	public Tuple(int size, String initValue) {
		super(Collections.nCopies(size, initValue));
	}
	public Tuple(List<String> values) {
		super(values);
	}
	public Tuple(String... values) {
		super(Arrays.asList(values));
	}


	/* ================================================== */
	/* ==========        Member  Method        ========== */
	/* ================================================== */
	public String toCSV() {
		return String.join(",", this);
	}


	@Override
	public boolean add(String e) throws UnsupportedOperationException {return false;}
	@Override
	public void add(int index, String element) throws UnsupportedOperationException {}
	@Override
	public boolean addAll(Collection<? extends String> c) throws UnsupportedOperationException {return false;}
	@Override
	public boolean addAll(int index, Collection<? extends String> c) throws UnsupportedOperationException {return false;}
	@Override
	public String remove(int index) throws UnsupportedOperationException {return get(index);}
	@Override
	public boolean remove(Object o) throws UnsupportedOperationException {return false;}
	@Override
	public boolean removeAll(Collection<?> c) throws UnsupportedOperationException {return false;}
	@Override
	public boolean retainAll(Collection<?> c) throws UnsupportedOperationException {return false;}
	@Override
	public boolean removeIf(Predicate<? super String> filter) throws UnsupportedOperationException {return false;}
	@Override
	protected void removeRange(int fromIndex, int toIndex) throws UnsupportedOperationException {}

}