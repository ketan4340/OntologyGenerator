package grammar.writing;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import data.RDF.vocabulary.JASS;
import grammar.SyntacticParent;
import grammar.paragraph.Paragraph;

/**
 * 文構造に関わる全てのクラスを包括する最上位クラス. とりあえずは{@code Paragraph}の上位クラス.
 * @author tanabekentaro
 *
 */
public class Writing extends SyntacticParent<Paragraph> {
	private static int WRITING_SUM = 0;
	
	private final int id; 
	
	/* ================================================== */
	/* ==========          Constructor         ========== */
	/* ================================================== */
	public Writing(List<Paragraph> paragraphs) {
		super(paragraphs);
		this.id = WRITING_SUM++;
	}

	/* ================================================== */
	/* ================ Interface Method ================ */
	/* ================================================== */
	@Override
	public int id() {return id;}
	@Override
	public String name() {
		return getChildren().stream().map(s -> s.name()).collect(Collectors.joining());
	}
	@Override
	public Resource toJASS(Model model) {
		Resource paragraphList = 
				model.createList(getChildren().stream().map(m -> m.toJASS(model)).iterator())
				.addProperty(RDF.type, JASS.ParagraphList);

		Resource writingResource = model.createResource(getJassURI())
				.addProperty(RDF.type, JASS.Writing)
				.addProperty(JASS.paragraphs, paragraphList);
		return writingResource;
	}

}
