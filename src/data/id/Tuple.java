package data.id;

import java.util.ArrayList;
import java.util.List;

public class Tuple {

	protected List<String> values;
	

	/****************************************/
	/**********     Constructor    **********/
	/****************************************/
	public Tuple(int size) {
		setValues(new ArrayList<>());
	}
	public Tuple(List<String> values) {
		setValues(values);
	}
	
	/****************************************/
	/**********   Member  Method   **********/
	/****************************************/
	public String toCSV() {
		return String.join(",", values);
	}
	
	/****************************************/
	/**********   Getter, Setter   **********/
	/****************************************/
	public List<String> getValues() {
		return values;
	}
	public void setValues(List<String> values) {
		this.values = values;
	}
}