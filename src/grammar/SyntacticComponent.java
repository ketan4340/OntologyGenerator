package grammar;

import java.util.List;
import java.util.stream.Collectors;

public abstract class SyntacticComponent<P, C> {
	private P parent;
	private List<C> constituents;

	public SyntacticComponent(List<C> constituents) {
		this.constituents = constituents;
	}

	
	
	public List<C> subConstituents(int fromIndex, int toIndex) {
		return constituents.subList(fromIndex, toIndex);
	}
	
	public boolean containsSubConstituents(List<C> subConstituents) {
		int size = constituents.size();
		int subsize = subConstituents.size();
		for (int i = 0; i < size-subsize; i++) {
			int fromIndex = i, toIndex = i + subsize;
			if (subConstituents.equals(subConstituents(fromIndex, toIndex)))
				return true;
		}
		return false;
	}

	
	/***********************************/
	/********** Getter/Setter **********/
	/***********************************/
	public List<C> getConstituents() {
		return constituents;
	}
	public P getParent() {
		return parent;
	}
	public void setParent(P parent) {
		this.parent = parent;
	}
	public void setConstituents(List<C> constituents) {
		this.constituents = constituents;
	}




	/**********************************/
	/********** Objectメソッド **********/
	/**********************************/
	@Override
	public String toString() {
		return constituents.stream()
				.map(cons -> cons.toString())
				.collect(Collectors.joining());
	}
}