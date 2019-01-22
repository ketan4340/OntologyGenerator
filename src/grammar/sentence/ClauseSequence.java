package grammar.sentence;

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

import grammar.clause.Clause;

public class ClauseSequence implements List<Clause<?>>, Cloneable {

	private final List<Clause<?>> clauses;
	//private final DependencyMap dm;

	public ClauseSequence() {
		this.clauses = new ArrayList<>();
	}
	public ClauseSequence(List<Clause<?>> clauses) {
		this.clauses = clauses;
	}
	public ClauseSequence(Clause<?>... clauses) {
		this.clauses = Arrays.asList(clauses);
	}

	public ClauseSequence cloneAll() {
		List<Clause<?>> cloneClauses = stream().map(Clause::clone).collect(Collectors.toList());
		ListIterator<Clause<?>> itr_origin = listIterator();
		ListIterator<Clause<?>> itr_clone = cloneClauses.listIterator();
		// 係り先があれば整え、なければnull
		while (itr_origin.hasNext() && itr_clone.hasNext()) {
			Clause<?> origin = itr_origin.next(), clone = itr_clone.next();
			int index2Dep = indexOf(origin.getDepending());
			clone.setDepending(index2Dep>=0? cloneClauses.get(index2Dep) : null);
		}
		return new ClauseSequence(cloneClauses);
	}
	
	public Sentence toSentence() {
		return new Sentence(clauses);
	}
	
	public List<Clause<?>> getClauses() {return clauses;}
	
	@Override
	public void forEach(Consumer<? super Clause<?>> action) {clauses.forEach(action);}
	@Override
	public int size() {return clauses.size();}
	@Override
	public boolean isEmpty() {return clauses.isEmpty();}
	@Override
	public boolean contains(Object o) {return clauses.contains(o);}
	@Override
	public Iterator<Clause<?>> iterator() {return clauses.iterator();}
	@Override
	public Object[] toArray() {return clauses.toArray();}
	@Override
	public <T> T[] toArray(T[] a) {return clauses.toArray(a);}
	@Override
	public boolean add(Clause<?> e) {return clauses.add(e);}
	@Override
	public boolean remove(Object o) {return clauses.remove(o);}
	@Override
	public boolean containsAll(Collection<?> c) {return clauses.containsAll(c);}
	@Override
	public boolean addAll(Collection<? extends Clause<?>> c) {return clauses.addAll(c);}
	@Override
	public boolean addAll(int index, Collection<? extends Clause<?>> c) {return clauses.addAll(index, c);}
	@Override
	public boolean removeAll(Collection<?> c) {return clauses.removeAll(c);}
	@Override
	public boolean retainAll(Collection<?> c) {return clauses.retainAll(c);}
	@Override
	public void replaceAll(UnaryOperator<Clause<?>> operator) {clauses.replaceAll(operator);}
	@Override
	public boolean removeIf(Predicate<? super Clause<?>> filter) {return clauses.removeIf(filter);}
	@Override
	public void sort(Comparator<? super Clause<?>> c) {clauses.sort(c);}
	@Override
	public void clear() {clauses.clear();}
	@Override
	public boolean equals(Object o) {return clauses.equals(o);}
	@Override
	public int hashCode() {return clauses.hashCode();}
	@Override
	public Clause<?> get(int index) {return clauses.get(index);}
	@Override
	public Clause<?> set(int index, Clause<?> element) {return clauses.set(index, element);}
	@Override
	public void add(int index, Clause<?> element) {clauses.add(index, element);}
	@Override
	public Stream<Clause<?>> stream() {return clauses.stream();}
	@Override
	public Clause<?> remove(int index) {return clauses.remove(index);}
	@Override
	public Stream<Clause<?>> parallelStream() {return clauses.parallelStream();}
	@Override
	public int indexOf(Object o) {return clauses.indexOf(o);}
	@Override
	public int lastIndexOf(Object o) {return clauses.lastIndexOf(o);}
	@Override
	public ListIterator<Clause<?>> listIterator() {return clauses.listIterator();}
	@Override
	public ListIterator<Clause<?>> listIterator(int index) {return clauses.listIterator(index);}
	@Override
	public List<Clause<?>> subList(int fromIndex, int toIndex) {return clauses.subList(fromIndex, toIndex);}
	@Override
	public Spliterator<Clause<?>> spliterator() {return clauses.spliterator();}
	
}
