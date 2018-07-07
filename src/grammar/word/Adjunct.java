package grammar.word;

import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import data.RDF.vocabulary.JASS;
import grammar.morpheme.Morpheme;

public class Adjunct extends Word {

	/****************************************/
	/**********     Constructor    **********/
	/****************************************/
	public Adjunct(List<Morpheme> morphemes) {
		super(morphemes);
	}
	public Adjunct(Morpheme... morphemes) {
		super(morphemes);
	}

	/****************************************/
	/**********   Member  Method   **********/
	/****************************************/
	/* 全く同じWordを複製する */
	@Override
	public Adjunct clone() {
		return new Adjunct(children);
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