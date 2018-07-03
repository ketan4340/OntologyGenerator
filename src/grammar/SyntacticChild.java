package grammar;

public interface SyntacticChild<P extends SyntacticParent<? extends SyntacticChild<P>>> {

	
	/****************************************/
	/**********   Getter, Setter   **********/
	/****************************************/
	P getParent();
	void setParent(P parent);
	
	
	/****************************************/
	/**********   Member  Method   **********/
	/****************************************/
}