package grammar.structure;

public interface SyntacticChild {

	
	/***********************************/
	/********** Getter/Setter **********/
	/***********************************/
	public <Pr extends SyntacticParent> Pr getParent();
	public <Pr extends SyntacticParent> void setParent(Pr parent);
	
}