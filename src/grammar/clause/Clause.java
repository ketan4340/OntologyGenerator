package grammar.clause;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import data.RDF.vocabulary.JASS;
import grammar.Constituent;
import grammar.GrammarInterface;
import grammar.SyntacticChild;
import grammar.SyntacticParent;
import grammar.morpheme.Morpheme;
import grammar.pattern.ClausePattern;
import grammar.pattern.WordPattern;
import grammar.word.Adjunct;
import grammar.word.Categorem;
import grammar.word.Word;

public abstract class Clause<C extends Categorem> extends SyntacticParent<Word>
implements SyntacticChild, GrammarInterface, Constituent {
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
		return words().stream().anyMatch(w -> w.matches(wp));
	}

	/**
	 * この文節が渡された単語指定配列に適合するかを判定する. 複数の単語が連続しているか調べたければ品詞.
	 * @param cp 文節指定パターン
	 * @param ignoreSign 文節末尾の接辞，記号 (、や。)を無視するか否か
	 * @return 文節の最後の単語が指定の品詞なら真，そうでなければ偽
	 */
	public boolean matchWith(ClausePattern cp, boolean ignoreSign) {
		List<Word> words = ignoreSign? words() : getChildren();
		int startmin = 0, startmax = words.size() - cp.size();
		if (startmax < 0) return false;
		if (cp.getForwardMatch() && cp.getBackwardMatch() && startmin != startmax) 
			return false;
		List<Integer> startIndexes = 
				cp.getForwardMatch()? Arrays.asList(startmin) : 
				cp.getBackwardMatch()? Arrays.asList(startmax) : 
					IntStream.range(startmin, startmax).boxed().collect(Collectors.toList());
		for (int idx : startIndexes) {
			ListIterator<Word> itr_w = words.listIterator(idx);
			ListIterator<WordPattern> itr_wp = cp.listIterator();
			while (itr_w.hasNext() && itr_wp.hasNext()) {
				Word word = itr_w.next();
				WordPattern wp = itr_wp.next();
				if (!word.matches(wp)) return false;
			}	
		}
		return true;
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
				model.createList(adjuncts.stream().map(m -> m.toJASS(model)).iterator())
				.addProperty(RDF.type, JASS.AdjunctList);

		Resource clauseResource = model.createResource(getJassURI())
				.addProperty(RDF.type, JASS.Clause)
				.addProperty(JASS.categorem, categoremResource)
				.addProperty(JASS.adjuncts, adjunctList);
		return clauseResource;
	}

	@Override
	public void onChanged(Change<? extends Word> c) {
		// TODO 自動生成されたメソッド・スタブ	
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
		final byte prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hashCode(categorem);
		result = prime * result + Objects.hashCode(adjuncts);
		result = prime * result + Objects.hashCode(others);
		//result = prime * result + Objects.hashCode(depending);
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof Clause<?>))
			return false; 
		Clause<?> other = (Clause<?>) obj;
		return Objects.equals(this.categorem, other.categorem) && 
				Objects.equals(this.adjuncts, other.adjuncts) &&
				Objects.equals(this.others, other.others);
				//Objects.equals(this.depending, other.depending);
	}
	
	@Override
	public String toString() {
		return getChildren().stream()
				.map(Word::toString)
				.collect(Collectors.joining("."));
	}
}