package grammar;

import data.RDF.rule.JassModelizable;

/**
 * 全ての言葉の単位.
 * @author tanabekentaro
 *
 */
public interface GrammarInterface extends JassModelizable {

	/**
	 * 付加情報なしの単純な文字列を返す.
	 * @return 自然言語のままの表記 (表層形)
	 */
	public String name();
}