package grammar.word;

import grammar.Concept;
import grammar.clause.Clause;

public class Adjunct extends Word {

	/***********************************/
	/**********  Constructor  **********/
	/***********************************/
	public Adjunct(Concept concept, Clause<?> parentClause) {
		super(concept, parentClause);
	}
	public Adjunct(Concept concept) {
		super(concept, null);
	}
	
	/***********************************/
	/**********  MemberMethod **********/
	/***********************************/
	/* 全く同じWordを複製する */
	@Override
	public Adjunct clone() {
		return new Adjunct(this.concept);
	}
}