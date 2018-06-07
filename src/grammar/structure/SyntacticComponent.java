package grammar.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class SyntacticComponent<C extends SyntacticChild> {
	protected List<C> children;

	/****************************************/
	/**********     Constructor    **********/
	/****************************************/
	public SyntacticComponent(List<C> constituents) {
		this.children = constituents;
		imprintThisOnChildren();
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
	
	public boolean imprintThisOnChildren() {
		if (Objects.isNull(children))
			return false;
		children.forEach(c -> c.setParent(this));
		return true;
	}
	
	public int indexOfChild(C predicate) {
		return children.indexOf(predicate);
	}
	public List<Integer> indexesOfChildren(List<C> clauseList) {
		List<Integer> indexList = new ArrayList<Integer>(clauseList.size());
		for (final C clause: clauseList) {
			indexList.add(indexOfChild(clause));
		}
		return indexList;
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
	
	public boolean replace(C before, C after) {
		if (!children.contains(before)) return false;
		after.setParent(this);
		int beforeIndex = children.indexOf(before);
		return before == children.set(beforeIndex, after);
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
				.map(cons -> cons.toString())
				.collect(Collectors.joining());
	}
}