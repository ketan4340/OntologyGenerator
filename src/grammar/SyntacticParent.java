package grammar;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class SyntacticParent<C extends SyntacticChild> {
	protected List<C> children;

	/* ================================================== */
	/* =================== Constructor ================== */
	/* ================================================== */
	public SyntacticParent(List<C> children) {
		setChildren(children);
		imprintThisOnChildren();
	}

	protected boolean imprintThisOnChildren() {
		if (Objects.isNull(children))
			return false;
		return true;
	}


	/* ================================================== */
	/* ================== Member Method ================= */
	/* ================================================== */
	public boolean replace(C before, C after) {
		int beforeIndex = children.indexOf(before);
		if (beforeIndex == -1)
			return false;
		return before == children.set(beforeIndex, after);
	}

	/** 
	 * 指定の子要素のリストがこの親要素に順番を維持して含まれているか.
	 * @param subChildren
	 * @return 子要素が順番通りに含まれている場合、{@code true}
	 */
	public boolean containsChildren(List<C> subChildren) {
		int size = children.size();
		int subsize = subChildren.size();
		for (int i = 0; i < size - subsize; i++) {
			int fromIndex = i, toIndex = i + subsize;
			if (subChildren.equals(children.subList(fromIndex, toIndex)))
				return true;
		}
		return false;
	}

	public int indexOfChild(C predicate) {
		return children.indexOf(predicate);
	}

	public List<Integer> indexesOfChildren(List<C> clauseList) {
		return clauseList.stream().map(this::indexOfChild).collect(Collectors.toList());
	}

	public C nextChild(C clause) {
		int nextIndex = indexOfChild(clause) + 1;
		if (nextIndex <= 0 || children.size() <= nextIndex)
			return null;
		return children.get(nextIndex);
	}

	public C previousChild(C clause) {
		int prevIndex = indexOfChild(clause) - 1;
		if (prevIndex < 0)
			return null;
		return children.get(prevIndex);
	}

	public C head() {
		return children.isEmpty() ? null : children.get(0);
	}

	public C tail() {
		return children.isEmpty() ? null : children.get(children.size() - 1);
	}

	
	/* ================================================== */
	/* ================== Getter, Setter ================ */
	/* ================================================== */	
	public List<C> getChildren() {
		return children;
	}
	
	public void setChildren(List<C> children) {
		this.children = children;
	}


	/* ================================================== */
	/* ================== Object Method ================= */
	/* ================================================== */
	@Override
	public String toString() {
		return children.stream()
				.map(C::toString)
				.collect(Collectors.joining());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Objects.hashCode(children);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof SyntacticParent))
			return false;
		SyntacticParent<?> other = (SyntacticParent<?>) obj;
		return Objects.equals(children, other.children);
	}

}