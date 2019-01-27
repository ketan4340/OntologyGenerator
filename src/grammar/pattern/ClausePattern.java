package grammar.pattern;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import grammar.clause.Clause;
import grammar.word.Word;

public class ClausePattern implements List<WordPattern> {
	private final List<WordPattern> wps;
	private final PatternOptions options = new PatternOptions();
	
	private ClausePattern() {
		this.wps = new ArrayList<>();
	}
	
	/**
	 * この文節が渡された単語指定配列に適合するかを判定する. 複数の単語が連続しているか調べたければ品詞.
	 * @param c 文節
	 * @return 文節の最後の単語が指定の品詞なら真，そうでなければ偽
	 */
	public boolean matches(Clause<?> c) {
		List<Word> words = getCheckSign()? c.getChildren() : c.words();
		int startmin = 0, startmax = words.size() - size();
		if (startmax < 0) return false;
		if (getForwardMatch() && getBackwardMatch() && startmin != startmax) 
			return false;
		List<Integer> startIndexes = 
				getForwardMatch()? Collections.singletonList(startmin) : 
				getBackwardMatch()? Collections.singletonList(startmax) : 
					IntStream.rangeClosed(startmin, startmax).boxed().collect(Collectors.toList());

		for (int idx : startIndexes) {
			ListIterator<Word> itr_w = words.listIterator(idx);
			ListIterator<WordPattern> itr_wp = listIterator();
			while (itr_w.hasNext() && itr_wp.hasNext()) {
				Word word = itr_w.next();
				WordPattern wp = itr_wp.next();
				if (!wp.matches(word)) return false;
			}	
		}
		return true;
	}
	
	public boolean getForwardMatch() {
		return options.getForwardMatch();
	}
	public boolean getBackwardMatch() {
		return options.getBackwardMatch();
	}
	public boolean getCheckSign() {
		return options.getCheckSign();
	}
	public void setForwardMatch() {
		options.setForwardMatch();
	}
	public void setBackwardMatch() {
		options.setBackwardMatch();
	}
	public void setCheckSign() {
		options.setCheckSign();
	}
	
	private static final String OPTION_KEY = "%o";
	private static final String FORWARD_MATCH_KEY = "^";
	private static final String BACKWARD_MATCH_KEY = "$";
	private static final String CHEK_SIGN_KEY = ",.";

	public static ClausePattern compile(String[][] strss) {
		ClausePattern cp = new ClausePattern();
		for (String[] strs : strss) {
			WordPattern wp = new WordPattern(strs);
			if (wp.contains(OPTION_KEY)) {
				if (wp.contains(FORWARD_MATCH_KEY))
					cp.setForwardMatch();
				if (wp.contains(BACKWARD_MATCH_KEY))
					cp.setBackwardMatch();
				if (wp.contains(CHEK_SIGN_KEY))
					cp.setCheckSign();
			} else
				cp.add(wp);
		}
		return cp;
	}
	
	
	@Override
	public void forEach(Consumer<? super WordPattern> action) {
		wps.forEach(action);
	}
	@Override
	public int size() {
		return wps.size();
	}
	@Override
	public boolean isEmpty() {
		return wps.isEmpty();
	}
	@Override
	public boolean contains(Object o) {
		return wps.contains(o);
	}
	@Override
	public Iterator<WordPattern> iterator() {
		return wps.iterator();
	}
	@Override
	public Object[] toArray() {
		return wps.toArray();
	}
	@Override
	public <T> T[] toArray(T[] a) {
		return wps.toArray(a);
	}
	@Override
	public boolean add(WordPattern e) {
		return wps.add(e);
	}
	@Override
	public boolean remove(Object o) {
		return wps.remove(o);
	}
	@Override
	public boolean containsAll(Collection<?> c) {
		return wps.containsAll(c);
	}
	@Override
	public boolean addAll(Collection<? extends WordPattern> c) {
		return wps.addAll(c);
	}
	@Override
	public boolean addAll(int index, Collection<? extends WordPattern> c) {
		return wps.addAll(index, c);
	}
	@Override
	public boolean removeAll(Collection<?> c) {
		return wps.removeAll(c);
	}
	@Override
	public boolean retainAll(Collection<?> c) {
		return wps.retainAll(c);
	}
	@Override
	public void replaceAll(UnaryOperator<WordPattern> operator) {
		wps.replaceAll(operator);
	}
	@Override
	public boolean removeIf(Predicate<? super WordPattern> filter) {
		return wps.removeIf(filter);
	}
	@Override
	public void sort(Comparator<? super WordPattern> c) {
		wps.sort(c);
	}
	@Override
	public void clear() {
		wps.clear();
	}
	@Override
	public boolean equals(Object o) {
		return wps.equals(o);
	}
	@Override
	public int hashCode() {
		return wps.hashCode();
	}
	@Override
	public WordPattern get(int index) {
		return wps.get(index);
	}
	@Override
	public WordPattern set(int index, WordPattern element) {
		return wps.set(index, element);
	}
	@Override
	public void add(int index, WordPattern element) {
		wps.add(index, element);
	}
	@Override
	public Stream<WordPattern> stream() {
		return wps.stream();
	}
	@Override
	public WordPattern remove(int index) {
		return wps.remove(index);
	}
	@Override
	public Stream<WordPattern> parallelStream() {
		return wps.parallelStream();
	}
	@Override
	public int indexOf(Object o) {
		return wps.indexOf(o);
	}
	@Override
	public int lastIndexOf(Object o) {
		return wps.lastIndexOf(o);
	}
	@Override
	public ListIterator<WordPattern> listIterator() {
		return wps.listIterator();
	}
	@Override
	public ListIterator<WordPattern> listIterator(int index) {
		return wps.listIterator(index);
	}
	@Override
	public List<WordPattern> subList(int fromIndex, int toIndex) {
		return wps.subList(fromIndex, toIndex);
	}
	@Override
	public Spliterator<WordPattern> spliterator() {
		return wps.spliterator();
	}
	
}
