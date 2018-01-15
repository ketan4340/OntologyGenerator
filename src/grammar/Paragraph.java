package grammar;

import java.util.ArrayList;
import java.util.List;

public class Paragraph {
	public List<Sentence> sentences;
	
	
	public Paragraph(List<Sentence> sentences) {
		this.sentences = sentences;
	}
	public Paragraph() {
		this(new ArrayList<>());
	}
	
	/* Getter/Setter */
	public List<Sentence> getSentences() {
		return sentences;
	}
	public void setSentences(List<Sentence> sentences) {
		this.sentences = sentences;
	}
}