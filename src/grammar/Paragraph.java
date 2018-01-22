package grammar;

import java.util.List;
import java.util.stream.Collectors;

import grammar.structure.SyntacticComponent;
import grammar.structure.SyntacticParent;

public class Paragraph extends SyntacticComponent<Paragraph, Sentence>
 implements Identifiable {
	private static int paragraphSum = 0;
	
	private final int id;
	private List<Sentence> sentences;
	
	

	/***********************************/
	/**********  Constructor  **********/
	/***********************************/
	public Paragraph(List<Sentence> sentences) {
		super(sentences);
		this.id = paragraphSum++;
		this.sentences = sentences;
		//imprintThisOnChildren();
	}
	
	/***********************************/
	/**********   Interface   **********/
	/***********************************/
	public int getID() {
		return id;
	}

	@Override
	public Paragraph getParent() {return this;}
	@Override
	public <Pr extends SyntacticParent> void setParent(Pr parent) {}

	
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