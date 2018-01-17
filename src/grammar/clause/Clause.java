package grammar.clause;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

import grammar.word.Adjunct;
import grammar.word.Categorem;
import grammar.word.Phrase;
import grammar.word.Word;

public class Clause extends AbstractClause<Categorem>{
	private static int clausesSum = 0;

	private final int id;
	
	private List<Word> words;			// 構成するWordのListを持つ
	
	private Clause depending;			// 係り先文節.どのClauseに係るか
	private List<Clause> dependeds = new ArrayList<>();		// 係られてる文節の集合.どのClauseから係り受けるか

	
	public Clause() {
		super(new ArrayList<>());
		this.id = clausesSum++;
	}
	/**
	 * @param words				単語のリスト
	 * @param categoremIndex		自立語の位置
	 */
	public Clause(List<Word> words, int categoremIndex) {
		super(words);
		this.id = clausesSum++;

		
		this.words = words;		
		setItself2Words();
		setCategoremHeadWords(0, categoremIndex+1, true);	// CaboChaで自立語と判定された単語だけでなく，0番目からその単語まで全て自立語とする
		uniteCategorems4Words();	// 自立語を全て結合
	}
	/**
	 * 新型
	 * @param categorem
	 * @param adjuncts
	 * @param others
	 */
	public Clause(Categorem categorem, List<Adjunct> adjuncts, List<Word> others) {
		super(categorem, adjuncts, others);
		this.id = clausesSum++;
	}


	public void setWords(List<Word> words) {
		this.words = words;
		setItself2Words();
	}
	public void setDepending(Clause depending) {
		this.depending = depending;
		if (depending != null)
			depending.dependeds.add(this);	// 係り先の'係られてる集合'に自身を追加
	}
	/**
	 * @param fromIndex
	 * @param toIndex
	 * fromIndexからtoIndexの範囲(toIndexは含まない)のwordを主辞であるとする．
	 */
	private void setCategoremHeadWords(int fromIndex, int toIndex, boolean ctgrm_adjnc) {
		for (int i=fromIndex; i<toIndex; i++) {
			Word word = this.words.get(i);
			word.setIsCategorem(ctgrm_adjnc);
		}
	}
	private void uniteCategorems4Words() {
		List<Word> newWords = new ArrayList<>();
		List<Word> categorems = new LinkedList<Word>();	// 主辞
		for(Iterator<Word> itr = words.iterator(); itr.hasNext(); ) {
			Word word = itr.next();
			if (word.isCategorem) {	// 複数の主辞があれば一つにまとめる
				categorems.add(word);
			} else {
				switch(categorems.size()) {			// 主辞の数が
				case 0:				// 0なら
					break;							// スルー
				case 1:				// 1なら
					newWords.addAll(categorems);	// そのまま入れる
					categorems.clear();
					break;
				default:				// 2以上
					Phrase main = new Phrase();	// 主辞合成
					main.setPhrase(categorems, this, false);
					newWords.add(main);	// 生成したPhraseを入れる
					categorems.clear();
				}
				newWords.add(word);
			}
		}
		newWords.addAll(categorems);	// 残り物があれば回収
		this.words = newWords;
	}
	private void setItself2Words() {
		words.stream().forEach(w -> w.setParent(this));
	}

	public int indexOfW(Word word) {
		return words.indexOf(word);
	}

	/**
	 * 主辞だけを返す
	 * 仕様上，もし1つの文節に複数の主辞があると最後の主辞が選ばれる．
	 * 1つの文節に主辞は1つなので一応問題ない．
	 */
	public Word getMainWord() {
		Word mainWord = null;
		for(Word word: words) {
			if(word.isCategorem) mainWord = word;
		}
		return mainWord;
	}
	/**
	 * 機能語だけを返す
	 */
	public List<Word> getFunctionWords() {
		List<Word> functions = new ArrayList<Word>();
		for(Word word: words) {
			String[] tag_sign = {"記号"};
			if(word.hasSomeTags(tag_sign))	continue;	// 記号(「」、。など)はスルー
			if(!word.isCategorem) functions.add(word);
		}
		return functions;
	}

	public List<Clause> getAllDepending() {
		List<Clause> allDepending = new ArrayList<Clause>();
		for(Clause depto = this.depending; depto != null; depto = depto.depending) {
			allDepending.add(depto);
		}
		return allDepending;
	}

	/**
	 * 渡されたClauseをつなげて1つのClause(連文節)を作成する．
	 */
	public void uniteClauses(List<Clause> baseClauses) {
		if(baseClauses.size() < 2) return;
		List<Word> phraseWords = new ArrayList<>();			// 新しいPhraseの元になるWord
		List<Word> conjunctionWords = new ArrayList<>();	// Phrase完成後につなげる接続詞を保持
		Clause depto = null;								// 最後尾のClauseがどのClauseに係るか

		for(Iterator<Clause> itr = baseClauses.iterator(); itr.hasNext(); ) {
			Clause clause = itr.next();
			for(Word word: clause.words) {		// 元ClauseのWordはこの新しいClauseに属するように変える
				word.setParent(this);
			}
			// 全ての元Clauseの係り先を新しいClauseに変える
			if (clause.dependeds != null) {
				for(Clause bedep: clause.dependeds) {
					bedep.setDepending(this);
				}
			}

			if(!itr.hasNext()) {	// 最後尾の場合
				phraseWords.add(clause.getMainWord());
				conjunctionWords.addAll(clause.words);
				conjunctionWords.removeAll(phraseWords);
				depto = clause.depending;
			}else {
				phraseWords.addAll(clause.words);
			}
		}

		// 新しいPhraseを作成
		Phrase nph = new Phrase();
		nph.setPhrase(phraseWords, this, false);
		List<Word> newWords = new ArrayList<>();
		newWords.add(nph);
		newWords.addAll(conjunctionWords);
		setWords(newWords);
		setDepending(depto);
	}

	/* 全く同じClauseを複製する */
	public Clause clone() {
		Clause replicaClause = new Clause();
		List<Word> subWords = new ArrayList<>(words.size());
		for(Word word: words) {
			Word subWord = word.copy();
			subWord.setParent(replicaClause);
			subWords.add(subWord);
		}
		replicaClause.setWords(subWords);
		replicaClause.setDepending(depending);
		return replicaClause;
	}
	/**
	 * 複数のClauseを係り受け関係を維持しつつ複製する
	 */
	public static List<Clause> cloneAll(List<Clause> clauseList) {
		// まずは複製
		List<Clause> replicaList = clauseList.stream().map(origin -> origin.clone()).collect(Collectors.toList());
		// 係り先があれば整え、なければnull
		for (int i=0; i<clauseList.size(); i++) {
			Clause origin = clauseList.get(i);
			Clause replica = replicaList.get(i);
			int index2Dep = clauseList.indexOf(origin.depending);
			replica.setDepending( (index2Dep != -1)
					? replicaList.get(index2Dep)
					: null);
		}
		return replicaList;
	}

	/* 指定の品詞を持つWordが並んでいたら繋げる */
	public void connect(String[][] tagNames) {
		if(words.size() < 2) return;	// ClauseのWordが一つなら意味がない
		List<Word> newWords = new ArrayList<>();
		List<Word> serialNouns = new ArrayList<>();

		while( !words.isEmpty() ) {
			Word word = words.remove(0);

			boolean hasSomeTag = false;
			for(String[] tagName: tagNames) {
				if(word.hasAllTags(tagName)) {
					hasSomeTag = true;
					break;
				}
			}
			if(hasSomeTag) {	// 指定品詞に該当
				serialNouns.add(word);
			}else {				// 該当せず
				if(!serialNouns.isEmpty()) {
					Phrase nph = new Phrase();
					nph.setPhrase(serialNouns, this, false);
					newWords.add(nph);
					serialNouns.clear();
				}
				newWords.add(word);
			}
		}

		if(!serialNouns.isEmpty()) {	// Clauseの末尾が該当した場合ここで処理
			Phrase nph = new Phrase();
			nph.setPhrase(serialNouns, this, false);	// 末尾のWordに依存=false
			newWords.add(nph);
		}
		words = newWords;
	}

	/** 指定の品詞を持つWordを返す */
	public List<Word> collectAllTagWords(String[][] tagNames) {
		List<String[]> tagNameList = Arrays.asList(tagNames);
		List<Word> taggedWords = new ArrayList<>();
		for(final Word word: words) {
			for (final String[] tagsArray: tagNameList){
				if(word.hasAllTags(tagsArray))	taggedWords.add(word);
			}
		}
		return taggedWords;
	}
	/** 指定の品詞を"全て"持つWordが含まれているか判定 */
	public boolean haveSomeTagWord(String[][] tagNames) {
		for(final Word word: words) {
			for (final String[] tagsArray: tagNames){
				if(word.hasAllTags(tagsArray))	return true;
			}
		}
		return false;
	}

	/* このClauseの最後尾が渡された品詞のWordかどうか */
	/* 最後尾が読点"、"の場合は無視 */
	public boolean endWith(String[][] tagNames, boolean ignoreSign) {
		boolean endWith = true;
		int tagIndex = tagNames.length-1;
		String[] tagSign = {"記号"};

		for(ListIterator<Word> li = words.listIterator(words.size()); li.hasPrevious(); ) {
			Word word = li.previous();				// wordも
			String[] tagName = tagNames[tagIndex];	// tagも後ろから遡る
			if(ignoreSign && word.hasAllTags(tagSign))
				continue;	// 記号の場合はスルー

			if(word.hasAllTags(tagName)) {
				tagIndex--;
			}else {
				endWith = false;
				break;
			}
			if(tagIndex < 0) break;
		}
		return endWith;
	}

	/* このClauseのうち、指定された範囲のWordを繋げて一つの品詞にする */
	public void nounize(int fromIndex, int toIndex) {
		List<Word> mainIDs = words.subList(fromIndex, toIndex);

		Phrase properNoun = new Phrase();	// 固有名詞として扱う
		properNoun.setPhrase(mainIDs, this, true);
		mainIDs.clear();
		this.words.add(fromIndex, properNoun);
	}

	
	
	
	public int getID() {
		return id;
	}
	public List<Word> getWords() {
		return words;
	}
	public Clause getDepending() {
		return depending;
	}
	public List<Clause> getDependeds() {
		return dependeds;
	}
	
	/**********************************/
	/********** Objectメソッド **********/
	/**********************************/

}