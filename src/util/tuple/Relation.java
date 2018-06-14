package util.tuple;

import java.util.Collection;
import java.util.HashSet;

public class Relation<T extends Tuple> extends HashSet<T> {
	private static final long serialVersionUID = -795496356555507147L;

	
	private Tuple attributes;
	
	
	/****************************************/
	/**********     Constructor    **********/
	/****************************************/
	public Relation(Collection<? extends T> tuples, Tuple attributes) {
		super(tuples);
		setAttributes(attributes);
	}
	public Relation(Tuple attributes) {
		super();
		setAttributes(attributes);
	}

	

	/****************************************/
	/**********    Member Method   **********/
	/****************************************/

	

	/****************************************/
	/**********   Getter, Setter   **********/
	/****************************************/
	public Tuple getAttributes() {
		return attributes;
	}
	public void setAttributes(Tuple attributes) {
		this.attributes = attributes;
	}	

}