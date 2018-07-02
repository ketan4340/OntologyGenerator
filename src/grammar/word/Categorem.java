package grammar.word;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import data.RDF.vocabulary.JASS;
import grammar.clause.Clause;
import grammar.concept.Concept;

public class Categorem extends Word{
	public static final Categorem EMPTY_CATEGOREM = new Categorem(Concept.EMPTY_CONCEPT);

	/****************************************/
	/**********     Constructor    **********/
	/****************************************/
	public Categorem(Concept concept, Clause<?> parentClause) {
		super(concept, parentClause);
	}
	public Categorem(Concept concept) {
		super(concept, null);
	}
	
	/****************************************/
	/**********   Member  Method   **********/
	/****************************************/
	/* 全く同じWordを複製する */
	@Override
	public Categorem clone() {
		return new Categorem(this.concept);
	}
	
	
	/****************************************/
	/**********  Interface Method  **********/
	/****************************************/
	@Override
	public Resource toRDF(Model model) {
		return super.toRDF(model)
				.addProperty(RDF.type, JASS.Categorem);
	}

}