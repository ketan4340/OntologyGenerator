package grammar.word;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import data.RDF.vocabulary.JASS;
import grammar.clause.Clause;
import grammar.concept.Concept;

public class Adjunct extends Word {

	/****************************************/
	/**********     Constructor    **********/
	/****************************************/
	public Adjunct(Concept concept, Clause<?> parentClause) {
		super(concept, parentClause);
	}
	public Adjunct(Concept concept) {
		super(concept, null);
	}
	
	/****************************************/
	/**********   Member  Method   **********/
	/****************************************/
	/* 全く同じWordを複製する */
	@Override
	public Adjunct clone() {
		return new Adjunct(this.concept);
	}
	/****************************************/
	/**********  Interface Method  **********/
	/****************************************/
	@Override
	public Resource toRDF(Model model) {
		return super.toRDF(model)
				.addProperty(RDF.type, JASS.Adjunct);
	}
	
}