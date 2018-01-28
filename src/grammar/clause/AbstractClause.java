package grammar.clause;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import grammar.Concept;
import grammar.GrammarInterface;
import grammar.Sentence;
import grammar.morpheme.Morpheme;
import grammar.structure.SyntacticComponent;
import grammar.word.Adjunct;
import grammar.word.Word;

public abstract class AbstractClause<W extends Word> extends SyntacticComponent<Sentence, Word> 
	implements GrammarInterface {
	private static int clausesSum = 0;
	
	private final int id;
	
	protected W categorem;				// 自立語
	protected List<Adjunct> adjuncts; 	// 付属語
	protected List<Word> others;			// 。などの記号
	
	protected AbstractClause<?> depending;		// 係り先文節.どのClauseに係るか


	/***********************************/
	/**********  Constructor  **********/
	/***********************************/
	public AbstractClause(W categorem, List<Adjunct> adjuncts, List<Word> others) {
		super(linedupWords(categorem, adjuncts, others));
		this.id = clausesSum++;
		this.categorem = categorem;
		this.adjuncts = adjuncts;
		this.others = others;
	}
	public AbstractClause(List<Word> constituents) {
		super(constituents);
		this.id = clausesSum++;
	}
	
	
	/***********************************/
	/**********  MemberMethod **********/
	/***********************************/
	public List<Word> words() {
		List<Word> words = new ArrayList<>(5);
		words.add(categorem);
		words.addAll(adjuncts);
		words.addAll(others);
		return words;
	}
	
	private static List<Word> linedupWords(Word categorem, List<? extends Word> adjuncts, List<? extends Word> others) {
		List<Word> words = new ArrayList<>(5);
		words.add(categorem);
		words.addAll(adjuncts);
		words.addAll(others);
		return words;
	}
	

	/***********************************/
	/************* 旧型 ***********/
	/***********************************/
	
	/**
	 * この文節が係る文節，その文節が係る文節，と辿っていき，経由した全ての文節をリストにして返す.
	 * @return この文節から係る全ての文節
	 */
	public List<AbstractClause<?>> allDependings() {
		List<AbstractClause<?>> allDepending = new ArrayList<>();
		AbstractClause<?> depto = depending;
		while (depto != null) {
			allDepending.add(depto);
			depto = depto.getDepending();
		}
		return allDepending;
	}


	/* 全く同じClauseを複製する */
	public abstract AbstractClause<?> clone();
	
	/**
	 * 複数のClauseを係り受け関係を維持しつつ複製する
	 */
	public static List<AbstractClause<?>> cloneAll(List<AbstractClause<?>> clauseList) {
		// まずは複製
		List<AbstractClause<?>> replicaList = clauseList.stream().map(origin -> origin.clone()).collect(Collectors.toList());
		// 係り先があれば整え、なければnull
		for (int i=0; i<clauseList.size(); i++) {
			AbstractClause<?> origin = clauseList.get(i);
			AbstractClause<?> replica = replicaList.get(i);
			int index2Dep = clauseList.indexOf(origin.getDepending());
			replica.setDepending( (index2Dep != -1)
					? replicaList.get(index2Dep)
					: null);
		}
		return replicaList;
	}

	
	public boolean uniteAdjunct2Categorem(String[] tag4Categorem, String[] tag4Adjunct) {
		if (adjuncts.isEmpty())
			return false;	// 付属語がないなら意味がない
		if (!categorem.hasAllTags(tag4Categorem))
			return false;
		if (!adjuncts.get(0).hasAllTags(tag4Adjunct))
			return false;
		
		// 付属語から先頭の単語を取り出す
		Adjunct headAdjunct = adjuncts.remove(0);
		
		List<Morpheme> morphemes = Stream
				.concat(categorem.getMorphemes().stream(), headAdjunct.getMorphemes().stream())
				.collect(Collectors.toList());
		
		// 統合した新しい概念を用意
		Concept unitedConcept = Concept.getOrNewInstance(morphemes);
		// この文節の自立語が参照する概念を更新
		categorem.setConcept(unitedConcept);
		return true;
	}
	
	
	/** 指定の品詞を持つWordを返す */
	public List<Word> collectWordsHaveAll(String[][] tags) {
		List<String[]> tagNameList = Arrays.asList(tags);
		List<Word> taggedWords = new ArrayList<>();
		for (final Word word: words()) {
			for (final String[] tagsArray: tagNameList) {
				if (word.hasAllTags(tagsArray))	taggedWords.add(word);
			}
		}
		return taggedWords;
	}
	/** 指定の品詞を"全て"持つWordが含まれているか判定 */
	public boolean containsWordHasAll(String[][] tags) {
		for (final Word word: words())
			for (final String[] tagsArray: tags)
				if (word.hasAllTags(tagsArray))
					return true;
		return false;
	}

	/**
	 * このClauseの最後尾が渡された品詞のWordなら真. 複数の単語が連続しているか調べたければ品詞配列を複数指定可能.
	 * 最後尾が読点"、"の場合は無視
	 * @param tagNames 品詞
	 * @param ignoreSign 最後尾が記号の場合無視するか
	 * @return 文節の最後の単語が指定の品詞なら真，そうでなければ偽
	 */
	public boolean endWith(String[][] tagNames, boolean ignoreSign) {
		boolean endWith = true;
		int tagIndex = tagNames.length-1;
		String[] tagSign = {"記号"};

		for (ListIterator<Word> li = words().listIterator(words().size()); li.hasPrevious(); ) {
			Word word = li.previous();				// wordも
			String[] tagName = tagNames[tagIndex];	// tagも後ろから遡る
			if(ignoreSign && word.hasAllTags(tagSign))
				continue;	// 記号の場合はスルー

			if (word.hasAllTags(tagName)) {
				tagIndex--;
			} else {
				endWith = false;
				break;
			}
			if(tagIndex < 0) break;
		}
		return endWith;
	}


	public Set<AbstractClause<?>> clausesDependThis() {
		return parent.getChildren().stream()
				.filter(c -> c.depending == this)
				.collect(Collectors.toSet());
	}
	

	/***********************************/
	/**********   Interface   **********/
	/***********************************/
	@Override
	public String name() {
		return getChildren().stream().map(w -> w.name()).collect(Collectors.joining());
	}
	/*
	public Sentence getParent() {
		return parent;
	}
	public List<Word> getConstituents() {
		return constituents;
	}
	public <P extends SyntacticParent> void setParent(P parent) {
		this.parent = (Sentence) parent;
	}
	public <C extends SyntacticChild> void setConstituents(List<C> constituents) {
		this.constituents = (List<Word>) constituents;
	}
	*/
	
	/***********************************/
	/********** Getter/Setter **********/
	/***********************************/
	public int getID() {
		return id;
	}
	public W getCategorem() {
		return categorem;
	}
	public List<Adjunct> getAdjuncts() {
		return adjuncts;
	}
	public List<Word> getOthers() {
		return others;
	}
	public AbstractClause<?> getDepending() {
		return depending;
	}
	public void setCategorem(W categorem) {
		this.categorem = categorem;
	}
	public void setAdjuncts(List<Adjunct> adjuncts) {
		this.adjuncts = adjuncts;
	}
	public void setOthers(List<Word> others) {
		this.others = others;
	}
	public void setDepending(AbstractClause<?> depending) {
		this.depending = depending;
	}
	@Override
	public List<Word> getChildren() {
		List<Word> words = new ArrayList<>(5);
		words.add(categorem);
		words.addAll(adjuncts);
		words.addAll(others);
		return words;
	}

	
	/**********************************/
	/********** Objectメソッド **********/
	/**********************************/
	@Override
	public String toString() {
		return words().stream().map(w -> Objects.toString(w, "nullWord")).collect(Collectors.joining());
	}
}