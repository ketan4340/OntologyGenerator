package util.uniqueSet;

public interface Uniqueness<T> extends Comparable<T>{
	public boolean equals(Object obj);
	public int hashCode();
}