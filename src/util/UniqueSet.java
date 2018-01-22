package util;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;

public class UniqueSet<E extends Uniqueness<? super E>> extends AbstractSet<E> implements Set<E>, Cloneable, java.io.Serializable {
	private static final long serialVersionUID = 768173710L;

	private transient HashMap<E, E> map;

	/**
	 * Constructs a new, empty set; the backing <tt>HashMap</tt> instance has
	 * default initial capacity (16) and load factor (0.75).
	 */
	public UniqueSet() {
		map = new HashMap<>();
	}

	/**
     * Constructs a new set containing the elements in the specified
     * collection.  The <tt>HashMap</tt> is created with default load factor
     * (0.75) and an initial capacity sufficient to contain the elements in
     * the specified collection.
     *
     * @param c the collection whose elements are to be placed into this set
     * @throws NullPointerException if the specified collection is null
     */
	public UniqueSet(Collection<? extends E> c) {
		map = new HashMap<>(Math.max((int) (c.size()/.75f) + 1, 16));
		addAll(c);
	}

	/**
	 * Constructs a new, empty set; the backing <tt>HashMap</tt> instance has
     * the specified initial capacity and the specified load factor.
     *
     * @param      initialCapacity   the initial capacity of the hash map
     * @param      loadFactor        the load factor of the hash map
     * @throws     IllegalArgumentException if the initial capacity is less
     *             than zero, or if the load factor is nonpositive
     */
    public UniqueSet(int initialCapacity, float loadFactor) {
        map = new HashMap<>(initialCapacity, loadFactor);
    }

    /**
     * Constructs a new, empty set; the backing <tt>HashMap</tt> instance has
     * the specified initial capacity and default load factor (0.75).
     *
     * @param      initialCapacity   the initial capacity of the hash table
     * @throws     IllegalArgumentException if the initial capacity is less
     *             than zero
     */
    public UniqueSet(int initialCapacity) {
    		map = new HashMap<>(initialCapacity);
    }

	/**
	     * Constructs a new, empty linked hash set.  (This package private
	     * constructor is only used by LinkedHashSet.) The backing
	     * HashMap instance is a LinkedHashMap with the specified initial
	     * capacity and the specified load factor.
	     *
	     * @param      initialCapacity   the initial capacity of the hash map
	     * @param      loadFactor        the load factor of the hash map
	     * @param      dummy             ignored (distinguishes this
	     *             constructor from other int, float constructor.)
	     * @throws     IllegalArgumentException if the initial capacity is less
	     *             than zero, or if the load factor is nonpositive
	     */
	    UniqueSet(int initialCapacity, float loadFactor, boolean dummy) {
	        map = new LinkedHashMap<>(initialCapacity, loadFactor);
	    }

	/**
	 * Returns an iterator over the elements in this set. The elements are returned
	 * in no particular order.
	 *
	 * @return an Iterator over the elements in this set
	 * @see ConcurrentModificationException
	 */
	public Iterator<E> iterator() {
		return map.keySet().iterator();
	}

	/**
	 * Returns the number of elements in this set (its cardinality).
	 *
	 * @return the number of elements in this set (its cardinality)
	 */
	public int size() {
		return map.size();
	}

	/**
	 * Returns <tt>true</tt> if this set contains no elements.
	 *
	 * @return <tt>true</tt> if this set contains no elements
	 */
	public boolean isEmpty() {
		return map.isEmpty();
	}

	/**
	 * Returns <tt>true</tt> if this set contains the specified element. More
	 * formally, returns <tt>true</tt> if and only if this set contains an element
	 * <tt>e</tt> such that
	 * <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>.
	 *
	 * @param o
	 *            element whose presence in this set is to be tested
	 * @return <tt>true</tt> if this set contains the specified element
	 */
	public boolean contains(Object o) {
		return map.containsKey(o);
	}

	/**
	 * Adds the specified element to this set if it is not already present. More
	 * formally, adds the specified element <tt>e</tt> to this set if this set
	 * contains no element <tt>e2</tt> such that
	 * <tt>(e==null&nbsp;?&nbsp;e2==null&nbsp;:&nbsp;e.equals(e2))</tt>. If this set
	 * already contains the element, the call leaves the set unchanged and returns
	 * <tt>false</tt>.
	 *
	 * @param e
	 *            element to be added to this set
	 * @return <tt>true</tt> if this set did not already contain the specified
	 *         element
	 */
	public boolean add(E e) {
		return map.put(e, e) == null;
	}

	/**
	 * Removes the specified element from this set if it is present. More formally,
	 * removes an element <tt>e</tt> such that
	 * <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>, if this set
	 * contains such an element. Returns <tt>true</tt> if this set contained the
	 * element (or equivalently, if this set changed as a result of the call). (This
	 * set will not contain the element once the call returns.)
	 *
	 * @param o
	 *            object to be removed from this set, if present
	 * @return <tt>true</tt> if the set contained the specified element
	 */
	public boolean remove(Object o) {
		return map.remove(o) == o;
	}

	/**
	 * Removes all of the elements from this set. The set will be empty after this
	 * call returns.
	 */
	public void clear() {
		map.clear();
	}

	/**
	 * Returns a shallow copy of this <tt>UniqueSet</tt> instance: the elements
	 * themselves are not cloned.
	 *
	 * @return a shallow copy of this set
	 */
	@SuppressWarnings("unchecked")
	public Object clone() {
		try {
			UniqueSet<E> newSet = (UniqueSet<E>) super.clone();
			newSet.map = (HashMap<E, E>) map.clone();
			return newSet;
		} catch (CloneNotSupportedException e) {
			throw new InternalError(e);
		}
	}

	/**
	 * Creates a <em><a href="Spliterator.html#binding">late-binding</a></em> and
	 * <em>fail-fast</em> {@link Spliterator} over the elements in this set.
	 *
	 * <p>
	 * The {@code Spliterator} reports {@link Spliterator#SIZED} and
	 * {@link Spliterator#DISTINCT}. Overriding implementations should document the
	 * reporting of additional characteristic values.
	 *
	 * @return a {@code Spliterator} over the elements in this set
	 * @since 1.8
	 */
	public Spliterator<E> spliterator() {
		return Spliterators.spliterator(iterator(), size(), Spliterator.DISTINCT);
		// 別案
		// return Spliterators.spliterator(this, Spliterator.DISTINCT);
		// HashSetはこれだった
		// return new HashMap.KeySpliterator<E, E>(map, 0, -1, 0, 0);
	}

	
	
	/**********************************/
	/**********  独自メソッド  **********/
	/**********************************/
	/**
	 * 指定のインスタンスeとe.equals(t)==trueになるような要素tがあればtを返す．なければeを返す．
	 * @param e setに存在するか確認したい要素．
	 * @return 引数と同値の要素．なければ引数の要素．
	 */
	public E getExistingOrIntact(E e) {
		return map.getOrDefault(e, e);
	}
	
}