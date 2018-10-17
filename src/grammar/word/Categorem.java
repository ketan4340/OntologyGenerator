package grammar.word;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import data.RDF.vocabulary.GOO;
import data.RDF.vocabulary.JASS;
import grammar.morpheme.Morpheme;

public class Categorem extends Word implements Resourcable {
	public static final Categorem EMPTY_CATEGOREM = new Categorem(Collections.emptyList());

	/****************************************/
	/**********     Constructor    **********/
	/****************************************/
	public Categorem(List<Morpheme> morphemes) {
		super(morphemes);
	}
	public Categorem(Morpheme... morphemes) {
		this(Arrays.asList(morphemes));
	}


	/****************************************/
	/**********   Member  Method   **********/
	/****************************************/
	/* 全く同じWordを複製する */
	@Override
	public Categorem clone() {
		return new Categorem(children);
	}
	@Override
	public String toResourceURI() {
		return GOO.uri + infinitive();
	}

	/****************************************/
	/**********  Interface Method  **********/
	/****************************************/
	@Override
	public Resource toRDF(Model model) {
		return super.toRDF(model)
				.addProperty(RDF.type, JASS.Categorem)
				.addProperty(JASS.means, model.createResource(toResourceURI()));
	}

}