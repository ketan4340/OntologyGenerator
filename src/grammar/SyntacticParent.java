package grammar;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class SyntacticParent<C> {

	protected List<C> children;


	/****************************************/
	/**********     Constructor    **********/
	/****************************************/
	public SyntacticParent(List<C> children) {
		setChildren(children);
		imprintThisOnChildren();
	}

	protected boolean imprintThisOnChildren() {
		if (Objects.isNull(children))
			return false;
		return true;
	}
	public boolean replace(C before, C after) {
		if (!getChildren().contains(before)) return false;
		return before == children.set(children.indexOf(before), after);
	}

	/****************************************/
	/**********   Member  Method   **********/
	/****************************************/
	public boolean containsSubConstituents(List<C> subConstituents) {
		int size = children.size();
		int subsize = subConstituents.size();
		for (int i = 0; i < size-subsize; i++) {
			int fromIndex = i, toIndex = i + subsize;
			if (subConstituents.equals(children.subList(fromIndex, toIndex)))
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
		int nextIndex = indexOfChild(clause)+1;
		if (nextIndex <= 0 || children.size() <= nextIndex)
			return null;
		return children.get(nextIndex);
	}
	public C previousChild(C clause) {
		int prevIndex = indexOfChild(clause)-1;
		if (prevIndex < 0)
			return null;
		return children.get(prevIndex);
	}
	public C head() {
		return children.get(0);
	}
	public C tail() {
		return children.get(children.size()-1);
	}


	/****************************************/
	/**********   Getter, Setter   **********/
	/****************************************/
	public List<C> getChildren() {
		return children;
	}
	public void setChildren(List<C> children) {
		this.children = children;
	}


	/****************************************/
	/**********   Object  Method   **********/
	/****************************************/
	@Override
	public String toString() {
		return children.stream()
				.map(C::toString)
				.collect(Collectors.joining());
	}
}