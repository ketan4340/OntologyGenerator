package grammar;

import java.util.List;
import java.util.stream.Collectors;

import grammar.structure.Child;
import grammar.structure.GrammarInterface;
import grammar.structure.Parent;

public class Paragraph extends Parent<Sentence> 
	implements GrammarInterface, Child<Writing> {
	private static int paragraphSum = 0;
	
	private final int id;
	
	/** 段落の親要素，文章. */
	private Writing parentWriting;
	
	/****************************************/
	/**********     Constructor    **********/
	/****************************************/
	public Paragraph(List<Sentence> sentences) {
		super(sentences);
		this.id = paragraphSum++;
	}
	
	/****************************************/
	/**********   Member  Method   **********/
	/****************************************/
	
	/****************************************/
	/**********  Interface Method  **********/
	/****************************************/
	@Override
	public String name() {
		return getChildren().stream().map(s -> s.name()).collect(Collectors.joining());
	}
	@Override
	public Writing getParent() {
		return parentWriting;
	}
	@Override
	public void setParent(Writing parent) {
		this.parentWriting = parent;
	}
	@Override
	public void setThisAsParent(Sentence child) {
		child.setParent(this);
	}
	
	/****************************************/
	/**********   Getter, Setter   **********/
	/****************************************/
	public int getID() {
		return id;
	}
	
	
	/****************************************/
	/**********   Object  Method   **********/
	/****************************************/
	@Override
	public String toString() {
		return children.stream().map(s -> s.toString()).collect(Collectors.joining("\n"));
	}

}