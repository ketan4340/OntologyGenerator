package grammar.word;

import grammar.Concept;
import grammar.clause.AbstractClause;

public class Categorem extends Word{

	public Categorem(Concept concept, AbstractClause<?> parentClause) {
		super(concept, parentClause);
	}
	public Categorem(Concept concept) {
		super(concept, null);
	}
}