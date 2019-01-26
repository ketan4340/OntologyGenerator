package grammar.word;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import data.RDF.vocabulary.JASS;
import grammar.Constituent;
import grammar.SyntacticChild;
import grammar.SyntacticParent;
import grammar.clause.Clause;

public class Dependent extends SyntacticParent<Clause<?>>
		implements SyntacticChild, Constituent {
	private static int SUM = 0;

	private final int id;
		
	
	/* ================================================== */
	/* =================== Constructor ================== */
	/* ================================================== */
	public Dependent(List<Clause<?>> clauses) {
		super(clauses);
		this.id = SUM++;
	}
	private Dependent(Dependent other) {
		this(new ArrayList<>(other.children));
	}

	
	/* ================================================== */
	/* ================ Interface Method ================ */
	/* ================================================== */
	@Override
	public int id() { return id; }
	@Override
	public String name() {
		return getChildren().stream().map(w -> w.name()).collect(Collectors.joining());
	}
	@Override
	public Dependent clone() {
		return new Dependent(this);
	}
	
	@Override
	public Resource toJASS(Model model) {
		return model.createList(children.stream().map(m -> m.toJASS(model)).iterator())
		.addProperty(RDF.type, JASS.ClauseList);
	}

}
