package grammar.word;

import grammar.Concept;
import grammar.clause.AbstractClause;

public class Categorem extends Word{
	public static final Categorem ZEROCATEGOREM = new Categorem(Concept.ZEROCONCEPT);

	/***********************************/
	/**********  Constructor  **********/
	/***********************************/
	public Categorem(Concept concept, AbstractClause<?> parentClause) {
		super(concept, parentClause);
	}
	public Categorem(Concept concept) {
		super(concept, null);
	}
	
	/***********************************/
	/**********  MemberMethod **********/
	/***********************************/
	/* 全く同じWordを複製する */
	@Override
	public Categorem clone() {
		return new Categorem(this.concept);
	}
	
}