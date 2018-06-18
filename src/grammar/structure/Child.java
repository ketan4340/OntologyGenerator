package grammar.structure;

public interface Child<P extends Parent<? extends Child<P>>> {

	
	/****************************************/
	/**********   Getter, Setter   **********/
	/****************************************/
	P getParent();
	void setParent(P parent);
	
	
	/****************************************/
	/**********   Member  Method   **********/
	/****************************************/
}