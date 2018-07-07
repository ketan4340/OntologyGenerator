package grammar.paragraph;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import data.RDF.RDFizable;
import data.RDF.vocabulary.JASS;
import data.id.Identifiable;
import grammar.GrammarInterface;
import grammar.SyntacticParent;
import grammar.sentence.Sentence;

public class Paragraph extends SyntacticParent<Sentence>
	implements Identifiable, GrammarInterface, RDFizable {
	private static int paragraphSum = 0;

	private final int id;

	/* ================================================== */
	/* ==========          Constructor         ========== */
	/* ================================================== */
	public Paragraph(List<Sentence> sentences) {
		super(sentences);
		this.id = paragraphSum++;
	}

	/* ================================================== */
	/* ==========        Member  Method        ========== */
	/* ================================================== */

	/* ================================================== */
	/* ==========       Interface Method       ========== */
	/* ================================================== */
	@Override
	public int id() {return id;}
	@Override
	public String name() {
		return getChildren().stream().map(s -> s.name()).collect(Collectors.joining());
	}
	@Override
	public String getURI() {
		return JASS.uri+getClass().getSimpleName()+id();
	}
	@Override
	public Resource toRDF(Model model) {
		Resource sentenceNode = model.createList(getChildren().stream().map(m -> m.toRDF(model)).iterator());

		Resource paragraphResource = model.createResource(getURI())
				.addProperty(RDF.type, JASS.Paragraph)
				.addProperty(JASS.consistsOfSentences, sentenceNode);
		return paragraphResource;
	}



	/* ================================================== */
	/* ==========        Object  Method        ========== */
	/* ================================================== */
	@Override
	public String toString() {
		return children.stream().map(s -> s.toString()).collect(Collectors.joining("\n"));
	}

}