package grammar.structure;

import java.util.List;

public interface SyntacticParent {

	
	/***********************************/
	/********** Getter/Setter **********/
	/***********************************/
	public <Ch extends SyntacticChild> List<Ch> getChildren();
	public <Ch extends SyntacticChild> void setChildren(List<Ch> constituents);
	
}