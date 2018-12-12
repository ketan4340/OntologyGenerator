package grammar.paragraph;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import data.RDF.vocabulary.JASS;
import grammar.GrammarInterface;
import grammar.SyntacticChild;
import grammar.SyntacticParent;
import grammar.sentence.Sentence;

public class Paragraph extends SyntacticParent<Sentence>
	implements SyntacticChild, GrammarInterface {
	private static int SUM = 0;

	private final int id;

	/* ================================================== */
	/* ==========          Constructor         ========== */
	/* ================================================== */
	public Paragraph(List<Sentence> sentences) {
		super(sentences);
		this.id = SUM++;
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
	public String getJassURI() {
		return JASS.uri+getClass().getSimpleName()+id();
	}
	@Override
	public Resource toJASS(Model model) {
		Resource sentenceNode = model.createList(getChildren().stream().map(m -> m.toJASS(model)).iterator());

		Resource paragraphResource = model.createResource(getJassURI())
				.addProperty(RDF.type, JASS.Paragraph)
				.addProperty(JASS.sentences, sentenceNode);
		return paragraphResource;
	}
	@Override
	public void onChanged(Change<? extends Sentence> c) {
		// TODO 自動生成されたメソッド・スタブ	
	}

	/* ================================================== */
	/* ==========        Object  Method        ========== */
	/* ================================================== */
	@Override
	public String toString() {
		return children.stream().map(s -> s.toString()).collect(Collectors.joining("\n"));
	}

}