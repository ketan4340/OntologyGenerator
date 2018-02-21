package grammar;

import java.util.List;
import java.util.stream.Collectors;

import grammar.structure.GrammarInterface;
import grammar.structure.SyntacticComponent;
import grammar.structure.SyntacticParent;

public class Paragraph extends SyntacticComponent<Paragraph, Sentence> 
	implements GrammarInterface {
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
	}
	
	/***********************************/
	/**********  MemberMethod **********/
	/***********************************/
	
	/***********************************/
	/**********   Interface   **********/
	/***********************************/
	@Override
	public String name() {
		return getChildren().stream().map(s -> s.name()).collect(Collectors.joining());
	}
	@Override
	public Paragraph getParent() {return this;}
	@Override
	public <Pr extends SyntacticParent> void setParent(Pr parent) {}

	
	/***********************************/
	/********** 	Getter/Setter **********/
	/***********************************/
	public int getID() {
		return id;
	}
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