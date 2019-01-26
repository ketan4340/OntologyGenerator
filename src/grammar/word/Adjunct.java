package grammar.word;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import data.RDF.vocabulary.JASS;
import grammar.morpheme.Morpheme;

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
	
	/* ================================================== */
	/* ================== Object Method ================= */
	/* ================================================== */
	@Override
	public int hashCode() {
		return children.hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Categorem))
			return false;
		Adjunct other = Adjunct.class.cast(obj);
		return Objects.equals(this.getChildren(), other.getChildren());
	}
	
}
