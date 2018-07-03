package grammar.writing;

import java.util.List;

import grammar.SyntacticParent;
import grammar.paragraph.Paragraph;

/**
 * 文構造に関わる全てのクラスを包括する最上位クラス. とりあえずは{@code Paragraph}の上位クラス.
 * @author tanabekentaro
 *
 */
public class Writing extends SyntacticParent<Paragraph> {

	/****************************************/
	/**********     Constructor    **********/
	/****************************************/
	public Writing(List<Paragraph> constituents) {
		super(constituents);
	}
	
	/****************************************/
	/**********  Interface Method  **********/
	/****************************************/
	@Override
	public void setThisAsParent(Paragraph child) {
		child.setParent(this);
	}
}