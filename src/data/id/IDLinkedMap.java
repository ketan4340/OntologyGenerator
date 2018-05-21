package data.id;

import java.util.LinkedHashMap;

public abstract class IDLinkedMap<K> extends LinkedHashMap<K, IDTuple> {
	private static final long serialVersionUID = 4734377852049948002L;

	/***********************************/
	/**********  Constructor  **********/
	/***********************************/
	public IDLinkedMap() {
		super();
	}
	public IDLinkedMap(LinkedHashMap<K, IDTuple> m) {
		super(m);
	}
	
	/***********************************/
	/********** Member Method **********/
	/***********************************/
	public void scoreAllInit() {
		values().stream().forEach(id -> id.setScore(0));
	}

	/***********************************/
	/********** Getter/Setter **********/
	/***********************************/

	
}