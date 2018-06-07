package grammar;

import java.util.List;

import grammar.structure.SyntacticChild;
import grammar.structure.SyntacticComponent;

/**
 * 文構造に関わる全てのクラスを包括する最上位クラス. とりあえずは{@code Paragraph}の上位クラス.
 * @author tanabekentaro
 *
 */
public class Writing extends SyntacticComponent<Paragraph>{

	
	/****************************************/
	/**********     Constructor    **********/
	/****************************************/
	public Writing(List<Paragraph> constituents) {
		super(constituents);
	}
	
	/****************************************/
	/**********  Interface Method  **********/
	/****************************************/
	public <C extends SyntacticChild> List<C> getConstituents() {return null;}
	public <C extends SyntacticChild> void setConstituents(List<C> constituents) {}
}