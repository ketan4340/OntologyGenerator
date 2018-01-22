package grammar.structure;

import java.util.List;

public interface SyntacticParent {

	
	/***********************************/
	/********** Getter/Setter **********/
	/***********************************/
	public <Ch extends SyntacticChild> List<Ch> getConstituents();
	public <Ch extends SyntacticChild> void setConstituents(List<Ch> constituents);
	
}