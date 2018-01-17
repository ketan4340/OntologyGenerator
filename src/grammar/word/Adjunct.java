package grammar.word;

import grammar.Concept;
import grammar.clause.AbstractClause;

public class Adjunct extends Word {

	public Adjunct(Concept concept, AbstractClause<?> parentClause) {
		super(concept, parentClause);
	}
	public Adjunct(Concept concept) {
		super(concept, null);
	}
}