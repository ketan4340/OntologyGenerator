package util.uniqueSet;

public interface Uniqueness<T> extends Comparable<T>{
	@Override
	boolean equals(Object obj);
	@Override
	int hashCode();
}