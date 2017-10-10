package grammar;

import java.util.List;
import java.util.stream.Collectors;

public class NaturalLanguage {
	private String nlText;

	public NaturalLanguage() {
		this("");
	}
	public NaturalLanguage(String text) {
		nlText = text;
	}

	public void setText(String nlText) {
		this.nlText = nlText;
	}
	@Override
	public String toString() {
		return nlText;
	}
	public boolean equals(String s) {
		return nlText.equals(s);
	}

	/** List<NaturalLanguage> -> List<String> **/
	public static List<String> toStringList(List<NaturalLanguage> nlList) {
		return nlList.stream().map(nl -> nl.toString()).collect(Collectors.toList());
	}
}
