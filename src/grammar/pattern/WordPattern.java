package grammar.pattern;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class WordPattern implements Set<String> {
	private final Set<String> spcfs;
	
	public WordPattern(String... spcfs) {
		this.spcfs = new HashSet<>(Arrays.asList(spcfs));
	}
	public WordPattern(Collection<String> spcfs) {
		this.spcfs = new HashSet<>(spcfs);
	}
	
	@Override
	public void forEach(Consumer<? super String> action) {
		spcfs.forEach(action);
	}
	@Override
	public int size() {
		return spcfs.size();
	}
	@Override
	public boolean isEmpty() {
		return spcfs.isEmpty();
	}
	@Override
	public boolean contains(Object o) {
		return spcfs.contains(o);
	}
	@Override
	public Iterator<String> iterator() {
		return spcfs.iterator();
	}
	@Override
	public Object[] toArray() {
		return spcfs.toArray();
	}
	@Override
	public <T> T[] toArray(T[] a) {
		return spcfs.toArray(a);
	}
	@Override
	public boolean add(String e) {
		return spcfs.add(e);
	}
	@Override
	public boolean remove(Object o) {
		return spcfs.remove(o);
	}
	@Override
	public boolean containsAll(Collection<?> c) {
		return spcfs.containsAll(c);
	}
	@Override
	public boolean addAll(Collection<? extends String> c) {
		return spcfs.addAll(c);
	}
	@Override
	public boolean retainAll(Collection<?> c) {
		return spcfs.retainAll(c);
	}
	@Override
	public boolean removeAll(Collection<?> c) {
		return spcfs.removeAll(c);
	}
	@Override
	public void clear() {
		spcfs.clear();
	}
	@Override
	public boolean equals(Object o) {
		return spcfs.equals(o);
	}
	@Override
	public int hashCode() {
		return spcfs.hashCode();
	}
	@Override
	public Spliterator<String> spliterator() {
		return spcfs.spliterator();
	}
	@Override
	public boolean removeIf(Predicate<? super String> filter) {
		return spcfs.removeIf(filter);
	}
	@Override
	public Stream<String> stream() {
		return spcfs.stream();
	}
	@Override
	public Stream<String> parallelStream() {
		return spcfs.parallelStream();
	}
	
}
