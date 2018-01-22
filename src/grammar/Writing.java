package grammar;

import java.util.List;

import grammar.structure.SyntacticChild;

/**
 * 文構造に関わる全てのクラスを包括する最上位クラス. とりあえずは{@code Paragraph}の直属の上位クラス.
 * @author tanabekentaro
 *
 */
public class Writing {

	
	
	/***********************************/
	/**********   Interface   **********/
	/***********************************/
	public <C extends SyntacticChild> List<C> getConstituents() {return null;}
	public <C extends SyntacticChild> void setConstituents(List<C> constituents) {}
}