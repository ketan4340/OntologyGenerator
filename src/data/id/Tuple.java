package data.id;

public class Tuple {

	protected String[] values;
	
	/***********************************/
	/**********  Constructor  **********/
	/***********************************/
	public Tuple(int size) {
		values = new String[size];
	}
	

	/***********************************/
	/**********  MemberMethod **********/
	/***********************************/
	public String toCSV() {
		return String.join(",", values);
	}

}