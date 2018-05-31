package util;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Tuple {

	protected List<String> values;
	

	/****************************************/
	/**********     Constructor    **********/
	/****************************************/
	public Tuple(int size) {
		setValues(Stream.generate(() -> "-1").limit(size).collect(Collectors.toList()));
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

	/****************************************/
	/**********   Object  Method   **********/
	/****************************************/
	@Override
	public String toString() {
		return values.toString(); 
	}
}