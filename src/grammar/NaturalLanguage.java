package grammar;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
	/** List<String> -> List<NaturalLanguage> */
	public static List<NaturalLanguage> toNaturalLanguageList(List<String> stringList) {
		return stringList.stream().map(str -> new NaturalLanguage(str)).collect(Collectors.toList());
	}	
	/** NaturalLanguage[] -> String[] */
	public static String[] toStringArray(NaturalLanguage[] nls) {
		return Stream.of(nls).map(nl -> nl.toString()).toArray(String[]::new);
	}
	/** String[] -> NaturalLanguage[] */
	public static NaturalLanguage[] toNaturalLanguageArray(String[] strings) {
		return Stream.of(strings).map(str -> new NaturalLanguage(str)).toArray(NaturalLanguage[]::new);
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