package grammar;

import java.util.List;
import java.util.stream.Collectors;

import grammar.structure.GrammarInterface;
import grammar.structure.SyntacticChild;
import grammar.structure.SyntacticComponent;
import grammar.structure.SyntacticParent;

public class Paragraph extends SyntacticComponent<Sentence> 
	implements GrammarInterface, SyntacticChild {
	private static int paragraphSum = 0;
	
	private final int id;
	
	private Writing parent;
	

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
		return (Writing) parent;
	}
	@Override
	public <P extends SyntacticComponent<?>> void setParent(P parent) {
		// TODO 自動生成されたメソッド・スタブ
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