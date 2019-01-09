package grammar.clause;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import data.RDF.vocabulary.JASS;
import grammar.word.Adjunct;
import grammar.word.Categorem;
import grammar.word.Dependent;
import grammar.word.Phrase;
import grammar.word.Word;

public class SerialClause extends Clause<Phrase> {
	
	/* ================================================== */
	/* ================== Constructor =================== */
	/* ================================================== */
	private SerialClause(Phrase categorem, List<Adjunct> adjuncts, List<Word> others) {
		super(categorem, adjuncts, others);
	}
	private SerialClause(SerialClause other) {
		this(other.categorem.clone(), 
				new ArrayList<>(other.adjuncts), 
				new ArrayList<>(other.others));
	}
	
	/* ================================================== */
	/* ================== Static Method ================= */
	/* ================================================== */
	public static SerialClause join(Clause<?>... clauses) {
		int tailIndex = clauses.length-1;
		Dependent dependent = new Dependent(Arrays.asList(Arrays.copyOfRange(clauses, 0, tailIndex)));
		Categorem head = clauses[tailIndex].categorem;
		Phrase categorem = new Phrase(dependent, head); 
		List<Adjunct> adjuncts = clauses[tailIndex].adjuncts;
		List<Word> others = clauses[tailIndex].others;
		SerialClause sc = new SerialClause(categorem, adjuncts, others);
		sc.setDepending(clauses[tailIndex].depending);
		return sc;
	}
	public static SerialClause join(List<Clause<?>> clauses) {
		return join(clauses.toArray(new Clause[clauses.size()]));
	}
	
	/* ================================================== */
	/* ================= Abstract Method ================ */
	/* ================================================== */
	@Override
	public SerialClause clone() {
		SerialClause clone = new SerialClause(this);
		clone.setDepending(getDepending());
		return clone;
	}

	/* ================================================== */
	/* ================= Interface Method =============== */
	/* ================================================== */
	@Override
	public Resource toJASS(Model model) {
		return super.toJASS(model).addProperty(RDF.type, JASS.SerialClause);
	}
	
	/* ================================================== */
	/* ================== Object Method ================= */
	/* ================================================== */
	@Override
	public String toString() {
		return getChildren().stream()
				.map(w -> Objects.toString(w, "Word"))
				.collect(Collectors.joining("."));
	}

}
