package grammar.clause;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import data.RDF.vocabulary.JASS;
import grammar.Constituent;
import grammar.SyntacticChild;
import grammar.SyntacticParent;
import grammar.morpheme.Morpheme;
import grammar.pattern.WordPattern;
import grammar.word.Adjunct;
import grammar.word.Categorem;
import grammar.word.Word;

public abstract class Clause<C extends Categorem> extends SyntacticParent<Word>
implements SyntacticChild, Constituent {
	private static int SUM = 0;

	private final int id;

	/** 文節の子要素 */
	protected C categorem;				// 自立語
	protected List<Adjunct> adjuncts; 	// 付属語
	protected List<Word> others;		// 。などの記号

	protected Clause<?> depending;		// 係り先文節.どのClauseに係るか


	/* ================================================== */
	/* =================== Constructor ================== */
	/* ================================================== */
	public Clause(C categorem, List<Adjunct> adjuncts, List<Word> others) {
		super(linedupWords(categorem, adjuncts, others));
		this.id = SUM++;
		this.categorem = categorem;
		this.adjuncts = adjuncts;
		this.others = others;
	}
	private static List<Word> linedupWords(
			Word categorem, 
			List<? extends Word> adjuncts, 
			List<? extends Word> others
	) {
		List<Word> words = new ArrayList<>(5);
		words.add(categorem);
		words.addAll(adjuncts);
		words.addAll(others);
		return words;
	}


	/* ================================================== */
	/* ================== Member Method ================= */
	/* ================================================== */
	/**
	 * この文節を構成する単語のリスト.
	 */
	public List<Word> words() {
		List<Word> words = new ArrayList<>(4);
		words.add(categorem);
		words.addAll(adjuncts);
		return words;
	}
	public List<Morpheme> morphemes() {
		return words().stream()
				.map(Word::getChildren)
				.flatMap(List::stream)
				.collect(Collectors.toList());
	}


	@Override
	public abstract Clause<C> clone();

	/** 指定の品詞を"全て"持つWordが含まれているか判定 */
	public boolean containsWordHas(WordPattern wp) {
		return words().stream().anyMatch(wp::matches);
	}
	
	public boolean hasOnlyCategorem() {
		return adjuncts.isEmpty() && others.isEmpty();
	}

	/* ================================================== */
	/* ================ Interface Method ================ */
	/* ================================================== */
	@Override
	public int id() {
		return id;
	}
	@Override
	public String name() {
		return getChildren().stream().map(w -> w.name()).collect(Collectors.joining());
	}
	@Override
	/**
	 * {@code AbstractClause}の子要素，{@code Word}のリストだけは
	 * {@code Parent.children}とは別に管理しているので上書きする.
	 */
	public List<Word> getChildren() {
		return linedupWords(categorem, adjuncts, others);
	}
	@Override
	public Resource toJASS(Model model) {
		Resource categoremResource = categorem.toJASS(model);
		Resource adjunctList = 
				model.createList(adjuncts.stream().map(m -> m.toJASS(model)).iterator());
				//.addProperty(RDF.type, JASS.AdjunctList);
		// others(記号)は出さなくていいかな
		Resource clauseResource = model.createResource(getJassURI())
				.addProperty(RDF.type, JASS.Clause)
				.addProperty(JASS.categorem, categoremResource)
				.addProperty(JASS.adjuncts, adjunctList);
		return clauseResource;
	}
	
	/* ================= Getter, Setter ================= */
	public C getCategorem() {
		return categorem;
	}
	public void setCategorem(C categorem) {
		this.categorem = categorem;
	}
	public List<Adjunct> getAdjuncts() {
		return adjuncts;
	}
	public List<Word> getOthers() {
		return others;
	}
	public Clause<?> getDepending() {
		return depending;
	}
	public void setDepending(Clause<?> depending) {
		this.depending = depending;
	}

	/* ================================================== */
	/* ================== Object Method ================= */
	/* ================================================== */
	@Override
	public int hashCode() {
		return Objects.hash(categorem, adjuncts, others);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Clause))
			return false; 
		Clause<?> other = Clause.class.cast(obj);
		return //Objects.equals(this.id, other.id) &&
				Objects.equals(this.categorem, other.categorem) && 
				Objects.equals(this.adjuncts, other.adjuncts) &&
				Objects.equals(this.others, other.others) && 
				Objects.equals(this.depending, other.depending);
	}
	
	@Override
	public String toString() {
		return getChildren().stream()
				.map(Word::toString)
				.collect(Collectors.joining("."));
	}
}