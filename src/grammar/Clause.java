package grammar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

public class Clause implements GrammarInterface{
	public static int clauseSum = 0;
	public static List<Clause> allClausesList = new ArrayList<Clause>();

	public int id;
	public List<Word> words;			// 構成するWordのidを持つ

	public Clause depending;			// どのClauseに係るか
	public Set<Integer> dependeds;		// どのClauseから係り受けるか
	public int originID;				// このClauseが別Clauseのコピーである場合，そのIDを示す
	public List<Integer> cloneIDs;		// このClauseのクローン達のID

	public Clause() {
		id = clauseSum++;
		allClausesList.add(this);
		words = new ArrayList<Word>();
		depending = null;
		dependeds = new HashSet<Integer>();
		originID = -1;
		cloneIDs = new ArrayList<Integer>();
	}
	public Clause(List<Word> words) {

	}
	public void setClause(List<Word> wdl) {
		List<Word> mains = new LinkedList<Word>();	// 主辞
		for(Iterator<Word> itr = wdl.iterator(); itr.hasNext(); ) {
			Word word = itr.next();
			if(word.isSubject) {	// 複数の主辞があれば一つにまとめる
				mains.add(word);
			}else {
				int mainsSize = mains.size();
				switch(mainsSize) {	// mainsの要素数が
				case 0:				// 0なら
					break;						// スルー
				case 1:				// 1なら
					words.addAll(mains);		// そのまま入れる
					mains.clear();
					break;
				default:			// 2以上
					Phrase main = new Phrase();	// 主辞合成
					main.setPhrase(mains, this.id, false);
					words.add(main);	// 生成したPhraseを入れる
					mains.clear();
				}
				words.add(word);
			}
		}
		words.addAll(mains);	// 残り物があれば回収
	}
	public void setDepending(Clause depend2Clause) {
		depending = depend2Clause;
	}
	public int indexOfW(Word word) {
		return words.indexOf(word);
	}

	public static Clause get(int id) {
		if(id < 0) return null;
		return allClausesList.get(id);
	}

	/**
	 * 主辞だけを返す
	 * 仕様上，もし1つの文節に複数の主辞があると最後の主辞が選ばれる．
	 * 1つの文節に主辞は1つなので一応問題ない．
	 */
	public Word getMainWord() {
		Word mainWord = null;
		for(Word word: words) {
			if(word.isSubject) mainWord = word;
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
			if(!word.isSubject) functions.add(word);
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
		List<Word> phraseWords = new ArrayList<>();		// 新しいPhraseの元になるWord
		List<Word> conjunctionWords = new ArrayList<>();	// Phrase完成後につなげる接続詞を保持
		Clause depto = null;										// 最後尾のClauseがどのClauseに係るか

		for(Iterator<Clause> itr = baseClauses.iterator(); itr.hasNext(); ) {
			Clause clause = itr.next();
			for(Word word: clause.words) {		// 元ClauseのWordはこの新しいClauseに属するように変える
				word.belongClause = this.id;
			}
			// 全ての元Clauseの係り先を新しいChunkに変える
			for(int bedep: clause.dependeds) {
				Clause.get(bedep).depending = this;
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
		nph.setPhrase(phraseWords, id, false);
		List<Word> newWords = new ArrayList<>();
		newWords.add(nph);
		newWords.addAll(conjunctionWords);
		setClause(newWords);
		setDepending(depto);
	}

	/* 全く同じClauseを複製する */
	public Clause clone() {
		Clause replicaClause = new Clause();
		List<Word> subWords = new ArrayList<>(words.size());
		for(Word word: words) {
			Word subWord = word.copy();
			subWord.belongClause = replicaClause.id;
			subWords.add(subWord);
		}
		replicaClause.setClause(subWords);
		replicaClause.setDepending(depending);
		replicaClause.originID = this.id;
		cloneIDs.add(replicaClause.id);
		return replicaClause;
	}
	/* 複数のClauseを係り受け関係を維持しつつ複製する */
	public static List<Clause> cloneAll(List<Clause> clauseList) {
		List<Clause> replicaList = new ArrayList<>();
		// まず複製
		for(final Clause origin : clauseList) {
			Clause replica = origin.clone();
			replicaList.add(replica);
		}
		// 係り先があれば整え、なければ-1
		for(final Clause replica : replicaList) {
			Clause origin = Clause.get(replica.originID);
			int index4Dep = clauseList.indexOf(origin.depending);
			replica.depending = (index4Dep != -1)
					? replicaList.get(index4Dep)
					: null;
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
					nph.setPhrase(serialNouns, id, false);
					newWords.add(nph);
					serialNouns.clear();
				}
				newWords.add(word);
			}
		}

		if(!serialNouns.isEmpty()) {	// Clauseの末尾が該当した場合ここで処理
			Phrase nph = new Phrase();
			nph.setPhrase(serialNouns, id, false);	// 末尾のWordに依存=false
			newWords.add(nph);
		}
		words = newWords;
	}

	/* 指定の文字列に一致するWordのIDを返す */
	public List<Integer> collectWords(String name) {
		List<Integer> ids = new ArrayList<Integer>();
		for(final Word word: words) {
			if(word.name.equals(name))	ids.add(id);
		}
		return ids;
	}
	/* 指定の品詞を持つWordを返す */
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
	/* 指定の品詞を"全て"持つWordが含まれているか判定 */
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
		properNoun.setPhrase(mainIDs, id, true);
		mainIDs.clear();
		this.words.add(fromIndex, properNoun);
	}

	/* Clauseを文字列で返す */
	@Override
	public String toString() {
		String clauseName = new String();
		for(final Word word: words) {
			clauseName += word.toString();
		}
		return clauseName;
	}
	@Override
	public void printDetail() {
		System.out.println(toString());
	}


	/* Clauseの係り受け関係を更新 */
	/* 全てのClauseインスタンスのdependingが正しいことが前提の設計 */
	public static void updateAllDependency() {
		for(final Clause cls: Clause.allClausesList) cls.dependeds.clear();	// 一度全ての被係り受けをまっさらにする
		for(final Clause cls: Clause.allClausesList) {
			Clause depto = cls.depending;
			if(depto != null) depto.dependeds.add(cls.id);
		}
	}

}
