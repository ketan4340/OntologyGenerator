package grammar.word;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import data.RDF.vocabulary.JASS;
import grammar.morpheme.Morpheme;
import language.pos.CabochaTags;

public class Adjunct extends Word {

	/* ================================================== */
	/* ================== Constructor =================== */
	/* ================================================== */
	public Adjunct(List<Morpheme> morphemes) {
		super(morphemes);
	}
	public Adjunct(Morpheme... morphemes) {
		super(morphemes);
	}
	public Adjunct(String name, CabochaTags tags) {
		super(name, tags);
	}
	private Adjunct(Adjunct other) {
		this(new ArrayList<>(other.children));
	}

	/* ================================================== */
	/* ================== Member Method ================= */
	/* ================================================== */

	
	/* ================================================== */
	/* ================ Interface Method ================ */ 
	/* ================================================== */
	@Override
	public Adjunct clone() {
		return new Adjunct(this);
	}

	@Override
	public Resource toJASS(Model model) {
		return super.toJASS(model)
				.addProperty(RDF.type, JASS.Adjunct);
	}
	
}
