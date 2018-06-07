package grammar.structure;

public interface SyntacticChild {

	
	/****************************************/
	/**********   Getter, Setter   **********/
	/****************************************/
	public <P extends SyntacticComponent<?>> P getParent();
	public <P extends SyntacticComponent<?>> void setParent(P parent);
	
}