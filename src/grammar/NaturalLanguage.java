package grammar;

import java.util.List;
import java.util.stream.Collectors;

public class NaturalLanguage {
	private static int sum;
	
	private final int id;
	private String text;

	

	/***********************************/
	/**********  Constructor  **********/
	/***********************************/
	public NaturalLanguage(String text) {
		this.id = sum++;
		this.text = text;
	}
	public NaturalLanguage() {
		this("");
	}

	
	/***********************************/
	/********** Static Method **********/
	/***********************************/
	/** List<NaturalLanguage> -> List<String> */
	public static List<String> toStringList(List<NaturalLanguage> nlList) {
		return nlList.stream().map(nl -> nl.toString()).collect(Collectors.toList());
	}
	/** List<NaturalLanguage> -> List<String> */
	public static List<NaturalLanguage> toNaturalLanguageList(List<String> stringList) {
		return stringList.stream().map(str -> new NaturalLanguage(str)).collect(Collectors.toList());
	}	
	
	
	/***********************************/
	/********** Getter/Setter **********/
	/***********************************/
	public int getId() {
		return id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	

	
	/***********************************/
	/********** Object Method **********/
	/***********************************/
	@Override
	public String toString() {
		return text;
	}

}