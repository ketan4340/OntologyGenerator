package grammar.clause;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import data.RDF.rule.RDFizable;
import data.RDF.vocabulary.JASS;
import grammar.GrammarInterface;
import grammar.SyntacticParent;
import grammar.morpheme.Morpheme;
import grammar.word.Adjunct;
import grammar.word.Word;

public abstract class Clause<W extends Word> extends SyntacticParent<Word>
implements GrammarInterface, RDFizable {
	private static int clausesSum = 0;

	private final int id;

	/** 文節の子要素 */
	protected W categorem;				// 自立語
	protected List<Adjunct> adjuncts; 	// 付属語
	protected List<Word> others;		// 。などの記号

	protected Clause<?> depending;		// 係り先文節.どのClauseに係るか


	/* ================================================== */
	/* ==========          Constructor         ========== */
	/* ================================================== */
	public Clause(W categorem, List<Adjunct> adjuncts, List<Word> others) {
		super(linedupWords(categorem, adjuncts, others));
		this.id = clausesSum++;
		this.categorem = categorem;
		this.adjuncts = adjuncts;
		this.others = others;
	}
	private static List<Word> linedupWords(Word categorem, List<? extends Word> adjuncts, List<? extends Word> others) {
		List<Word> words = new ArrayList<>(5);
		words.add(categorem);
		words.addAll(adjuncts);
		words.addAll(others);
		return words;
	}

	/* ================================================== */
	/* ==========        Member  Method        ========== */
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

	/**
	 * この文節が係る文節，その文節が係る文節，と辿っていき，経由した全ての文節をリストにして返す.
	 * @return この文節から係る全ての文節
	 */
	public List<Clause<?>> allDependings() {
		List<Clause<?>> allDepending = new ArrayList<>();
		Clause<?> depto = depending;
		while (depto != null) {
			allDepending.add(depto);
			depto = depto.getDepending();
		}
		return allDepending;
	}

	/** 全く同じClauseを複製する */
	@Override
	public abstract Clause<?> clone();

	/**
	 * 複数のClauseを係り受け関係を維持しつつ複製する
	 */
	public static List<Clause<?>> cloneAll(List<Clause<?>> originClauses) {
		// まずは複製
		List<Clause<?>> cloneClauses = originClauses.stream().map(Clause::clone).collect(Collectors.toList());
		ListIterator<Clause<?>> itr_origin = originClauses.listIterator();
		ListIterator<Clause<?>> itr_clone = cloneClauses.listIterator();

		// 係り先があれば整え、なければnull
		while (itr_origin.hasNext() && itr_clone.hasNext()) {
			Clause<?> origin = itr_origin.next(), clone = itr_clone.next();
			int index2Dep = originClauses.indexOf(origin.getDepending());
			clone.setDepending(index2Dep != -1? cloneClauses.get(index2Dep): null);
		}
		return cloneClauses;
	}

	/**
	 * 自立語と先頭の付属語がそれぞれ指定の品詞を持つ場合，結合する.
	 * 名詞とサ変動詞 (使用+する) のような組み合わせに適用する.
	 * @param categoremTag
	 * @param adjunctTag
	 * @return 結合に成功すれば真．そうでなければ偽．
	 */
	public boolean uniteAdjunct2Categorem(String[] categoremTag, String[] adjunctTag) {
		if (adjuncts.isEmpty())
			return false;	// 付属語がないなら意味がない
		if (!categorem.hasAllTag(categoremTag))
			return false;
		if (!adjuncts.get(0).hasAllTag(adjunctTag))
			return false;

		// 付属語から先頭の単語を取り出す
		Adjunct headAdjunct = adjuncts.remove(0);

		List<Morpheme> morphemes = Stream
				.concat(categorem.getChildren().stream(), headAdjunct.getChildren().stream())
				.collect(Collectors.toList());

		// この文節の自立語が参照する概念を更新
		categorem.setChildren(morphemes);
		return true;
	}


	/** 指定の品詞を"全て"持つWordが含まれているか判定 */
	public boolean containsWordHas(String[] tag) {
		for (final Word word: words())
			if (word.hasAllTag(tag))
				return true;
		return false;
	}

	/** 指定の品詞配列の"ある"品詞を，"全て"持つWordが含まれているか判定 */
	public boolean containsAnyWordsHave(String[][] tags) {
		for (String[] tag : tags)
			if (containsWordHas(tag))
				return true;
		return false;
	}

	/**
	 * このClauseの最後尾が渡された品詞のWordなら真. 複数の単語が連続しているか調べたければ品詞配列を複数指定可能.
	 * @param tags 品詞の配列
	 * @param ignoreSign 文節末尾の接辞，記号 (、や。)を無視するか否か
	 * @return 文節の最後の単語が指定の品詞なら真，そうでなければ偽
	 */
	public boolean endWith(String[][] tags, boolean ignoreSign) {
		int tagIndex = tags.length-1;
		List<Word> words = ignoreSign? words() : getChildren();
		for (ListIterator<Word> li = words.listIterator(words.size());
				li.hasPrevious() && tagIndex>=0; tagIndex--) {
			Word word = li.previous();		// wordも
			String[] tag = tags[tagIndex];	// tagも後ろから遡る
			if (!word.hasAllTag(tag))
				return false;
		}
		return true;
	}

	/* ================================================== */
	/* ==========       Interface Method       ========== */
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
	public Resource toRDF(Model model) {
		Resource categoremResource = categorem.toRDF(model);
		Resource adjunctNode = model.createList(
				adjuncts.stream().map(m -> m.toRDF(model)).iterator());

		Resource clauseResource = model.createResource(getURI())
				.addProperty(RDF.type, JASS.Clause)
				.addProperty(JASS.consistsOfCategorem, categoremResource)
				.addProperty(JASS.consistsOfAdjuncts, adjunctNode);
		return clauseResource;
	}

	/* ================================================== */
	/* ==========        Getter, Setter        ========== */
	/* ================================================== */
	public W getCategorem() {
		return categorem;
	}
	public void setCategorem(W categorem) {
		this.categorem = categorem;
	}
	public List<Adjunct> getAdjuncts() {
		return adjuncts;
	}
	public void setAdjuncts(List<Adjunct> adjuncts) {
		this.adjuncts = adjuncts;
	}
	public List<Word> getOthers() {
		return others;
	}
	public void setOthers(List<Word> others) {
		this.others = others;
	}
	public Clause<?> getDepending() {
		return depending;
	}
	public void setDepending(Clause<?> depending) {
		this.depending = depending;
	}

	/* ================================================== */
	/* ==========        Object  Method        ========== */
	/* ================================================== */
	@Override
	public String toString() {
		return getChildren().stream()
				.map(Word::toString)
				.collect(Collectors.joining("."));
	}
}