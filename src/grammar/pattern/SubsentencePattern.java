package grammar.pattern;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import grammar.sentence.Sentence;
import modules.textRevision.SubsentenceMatcher;
public class SubsentencePattern implements List<ClausePattern> {
	private final List<ClausePattern> cls;

	
	private SubsentencePattern() {
		this.cls = new ArrayList<>();
	}
	
	
	public static SubsentencePattern compile(String[][][] strsss) {
		return Arrays.stream(strsss).map(ClausePattern::compile)
				.collect(Collectors.toCollection(SubsentencePattern::new));
	}
	
	public SubsentenceMatcher matcher(Sentence s) {
		return new SubsentenceMatcher(s);
	}
	
	@Override
	public  void forEach(Consumer<? super ClausePattern> action) {cls.forEach(action);}
	@Override
	public int size() {return cls.size();}
	@Override
	public boolean isEmpty() {return cls.isEmpty();}
	@Override
	public boolean contains(Object o) {return cls.contains(o);}
	@Override
	public Iterator<ClausePattern> iterator() {return cls.iterator();}
	@Override
	public Object[] toArray() {return cls.toArray();}
	@Override
	public <T> T[] toArray(T[] a) {return cls.toArray(a);}
	@Override
	public boolean add(ClausePattern e) {return cls.add(e);}
	@Override
	public boolean remove(Object o) {return cls.remove(o);}
	@Override
	public boolean containsAll(Collection<?> c) {return cls.containsAll(c);}
	@Override
	public boolean addAll(Collection<? extends ClausePattern> c) {return cls.addAll(c);}
	@Override
	public boolean addAll(int index, Collection<? extends ClausePattern> c) {return cls.addAll(index, c);}
	@Override
	public boolean removeAll(Collection<?> c) {return cls.removeAll(c);}
	@Override
	public boolean retainAll(Collection<?> c) {return cls.retainAll(c);}
	@Override
	public  void replaceAll(UnaryOperator<ClausePattern> operator) {cls.replaceAll(operator);}
	@Override
	public  boolean removeIf(Predicate<? super ClausePattern> filter) {return cls.removeIf(filter);}
	@Override
	public  void sort(Comparator<? super ClausePattern> c) {cls.sort(c);}
	@Override
	public void clear() {cls.clear();}
	@Override
	public boolean equals(Object o) {return cls.equals(o);}
	@Override
	public int hashCode() {return cls.hashCode();}
	@Override
	public ClausePattern get(int index) {return cls.get(index);}
	@Override
	public ClausePattern set(int index, ClausePattern element) {return cls.set(index, element);}
	@Override
	public void add(int index, ClausePattern element) {cls.add(index, element);}
	@Override
	public  Stream<ClausePattern> stream() {return cls.stream();}
	@Override
	public ClausePattern remove(int index) {return cls.remove(index);}
	@Override
	public  Stream<ClausePattern> parallelStream() {return cls.parallelStream();}
	@Override
	public int indexOf(Object o) {return cls.indexOf(o);}
	@Override
	public int lastIndexOf(Object o) {return cls.lastIndexOf(o);}
	@Override
	public ListIterator<ClausePattern> listIterator() {return cls.listIterator();}
	@Override
	public ListIterator<ClausePattern> listIterator(int index) {return cls.listIterator(index);}
	@Override
	public List<ClausePattern> subList(int fromIndex, int toIndex) {return cls.subList(fromIndex, toIndex);}
	@Override
	public  Spliterator<ClausePattern> spliterator() {return cls.spliterator();}
	
}
