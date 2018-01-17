package grammar;

import java.util.List;
import java.util.stream.Collectors;

public class Paragraph {
	private List<Sentence> sentences;
	
	
	public Paragraph(List<Sentence> sentences) {
		this.sentences = sentences;
	}
	
	/***********************************/
	/********** 	Getter/Setter **********/
	/***********************************/
	public List<Sentence> getSentences() {
		return sentences;
	}
	
	
	/**********************************/
	/********** Objectメソッド **********/
	/**********************************/
	@Override
	public String toString() {
		return sentences.stream().map(s -> s.toString()).collect(Collectors.joining("\n"));
	}
}