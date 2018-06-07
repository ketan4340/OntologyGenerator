package grammar.structure;

import java.util.List;

public interface SyntacticParent {

	
	/***********************************/
	/********** Getter/Setter **********/
	/***********************************/
	public <C extends SyntacticChild> List<C> getChildren();
	public <C extends SyntacticChild> void setChildren(List<C> children);
	
}