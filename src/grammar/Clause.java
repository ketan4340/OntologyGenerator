package grammar;

import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.ListIterator;

public class Clause implements GrammarInterface{
	public static int clauseSum = 0;
	public static List<Clause> allClausesList = new ArrayList<Clause>();

	public int clauseID;
	public List<Integer> wordIDs;		// 構成するWordのidを持つ
	
	public int dependUpon;				// どのClauseに係るか
	public List<Integer> beDepended;	// どのClauseから係り受けるか
	public int originID;				// このClauseが別Clauseのコピーである場合，そのIDを示す
	public List<Integer> cloneIDs;		// このClauseのクローン達のID

	public Clause() {
		clauseID = clauseSum++;
		allClausesList.add(this);
		wordIDs = new ArrayList<Integer>();
		dependUpon = -1;
		beDepended = new ArrayList<Integer>();
		originID = -1;
		cloneIDs = new ArrayList<Integer>();
	}
	public void setClause(List<Integer> wdl, int depto) {
		List<Integer> mains = new LinkedList<Integer>();	// 主辞
		for(Iterator<Integer> itr = wdl.iterator(); itr.hasNext(); ) {
			int wordID = itr.next();
			Word word = Word.get(wordID);
			if(word.isSubject) {	// 複数の主辞があれば一つにまとめる
				mains.add(wordID);
			}else {
				int mainsSize = mains.size();
				switch(mainsSize) {	// mainsの要素数が
				case 0:				// 0なら
					break;			// スルー
				case 1:				// 1なら
					wordIDs.addAll(mains);	// そのまま入れる
					mains.clear();
					break;
				default:			// 2以上
					Phrase main = new Phrase();	// 主辞合成
					main.setPhrase(mains, this.clauseID, false);
					wordIDs.add(main.id);	// 生成したPhraseを入れる
					mains.clear();
				}
				wordIDs.add(wordID);
			}
		}
		wordIDs.addAll(mains);	// 残り物があれば回収

		dependUpon = depto;
	}
	public int indexOfW(int wordID) {
		return wordIDs.indexOf(wordID);
	}

	public static Clause get(int id) {
		if(id < 0) return null;
		return allClausesList.get(id);
	}

	/* 主辞だけを返す */
	public int getMainWord() {
		int mainID = -1;
		for(int wordID: wordIDs) {
			Word word = Word.get(wordID);
			if(word.isSubject) mainID = wordID;
		}
		return mainID;
	}
	/* 機能語だけを返す */
	public List<Integer> getFunctionWords() {
		List<Integer> functionIDs = new ArrayList<Integer>();
		for(int wordID: wordIDs) {
			Word word = Word.get(wordID);
			String[] tag_sign = {"記号"};
			if(word.hasSomeTags(tag_sign))	continue;	// 記号(「」、。など)はスルー
			if(!word.isSubject) functionIDs.add(wordID);
		}
		return functionIDs;
	}

	public List<Integer> getAllDepending() {
		List<Integer> allDepending = new ArrayList<Integer>();
		for(int depto = this.dependUpon; depto != -1; depto = Clause.get(depto).dependUpon) {
			allDepending.add(depto);
		}
		return allDepending;
	}

	public void uniteClauses(List<Integer> baseClauses) {
		if(baseClauses.size() < 2) return;
		List<Integer> phraseWords = new ArrayList<Integer>();		// 新しいPhraseの元になるWord
		List<Integer> conjunctionWords = new ArrayList<Integer>();	// Phrase完成後につなげる接続詞を保持
		int depto = -1;												// 最後尾のClauseがどのClauseに係るか

		for(Iterator<Integer> itr = baseClauses.iterator(); itr.hasNext(); ) {
			int clauseID = itr.next();
			Clause clause = Clause.get(clauseID);
			for(int wdID: clause.wordIDs) {		// 元ClauseのWordはこの新しいChunkに属するように変える
				Word.get(wdID).belongClause = this.clauseID;
			}
			// 全ての元Clauseの係り先を新しいChunkに変える
			for(int bedep: clause.beDepended) {
				Clause.get(bedep).dependUpon = this.clauseID;
			}

			if(!itr.hasNext()) {	// 最後尾の場合
				phraseWords.add(clause.getMainWord());
				conjunctionWords.addAll(clause.wordIDs);
				conjunctionWords.removeAll(phraseWords);
				depto = clause.dependUpon;
			}else {
				phraseWords.addAll(clause.wordIDs);
			}
		}

		// 新しいPhraseを作成
		Phrase nph = new Phrase();
		nph.setPhrase(phraseWords, clauseID, false);
		List<Integer> newWordIDs = new ArrayList<Integer>();
		newWordIDs.add(nph.id);
		newWordIDs.addAll(conjunctionWords);
		setClause(newWordIDs, depto);
	}

	/* 全く同じClauseを複製する */
	public Clause clone() {
		Clause replica = new Clause();
		List<Integer> subWordIDs = new ArrayList<Integer>(wordIDs.size());
		for(int id: wordIDs) {
			Word subWord = Word.get(id).copy();
			subWord.belongClause = replica.clauseID;
			subWordIDs.add(subWord.id);
		}
		replica.setClause(subWordIDs, dependUpon);
		replica.originID = this.clauseID;
		cloneIDs.add(replica.clauseID);
		return replica;
	}
	/* 複数のClauseを係り受け関係を維持しつつ複製する */
	public static List<Integer> cloneAll(List<Integer> clauseIDList) {
		List<Integer> replicaList = new ArrayList<Integer>();
		// まず複製
		for(final int id: clauseIDList) {
			Clause origin = Clause.get(id);
			Clause replica = origin.clone();
			replicaList.add(replica.clauseID);
		}
		// 係り先があれば整え、なければ-1
		for(final int id: replicaList) {
			Clause replica = Clause.get(id);
			Clause origin = Clause.get(replica.originID);
			int index4Dep = clauseIDList.indexOf(origin.dependUpon);
			replica.dependUpon = (index4Dep != -1)? replicaList.get(index4Dep): -1;
		}
		return replicaList;
	}

	/* 指定の品詞を持つWordが並んでいたら繋げる */
	public void connect(String[][] tagNames) {
		if(wordIDs.size() < 2) return;	// ClauseのWordが一つなら意味がない
		List<Integer> newWordIDs = new ArrayList<Integer>();
		List<Integer> serialNouns = new ArrayList<Integer>();

		while( !wordIDs.isEmpty() ) {
			int wordID = wordIDs.remove(0);
			Word word = Word.get(wordID);

			boolean hasSomeTag = false;
			for(String[] tagName: tagNames) {
				if(word.hasAllTags(tagName)) {
					hasSomeTag = true;
					break;
				}
			}
			if(hasSomeTag) {	// 指定品詞に該当
				serialNouns.add(word.id);
			}else {				// 該当せず
				if(!serialNouns.isEmpty()) {
					Phrase nph = new Phrase();
					nph.setPhrase(serialNouns, clauseID, false);
					newWordIDs.add(nph.id);
					serialNouns.clear();
				}
				newWordIDs.add(wordID);
			}
		}

		if(!serialNouns.isEmpty()) {	// Clauseの末尾が該当した場合ここで処理
			Phrase nph = new Phrase();
			nph.setPhrase(serialNouns, clauseID, false);	// 末尾のWordに依存=false
			newWordIDs.add(nph.id);
		}
		wordIDs = newWordIDs;
	}

	/* 指定の文字列に一致するWordのIDを返す */
	public List<Integer> collectWords(String name) {
		List<Integer> ids = new ArrayList<Integer>();
		for(final int id: wordIDs) {
			Word wd = Word.get(id);
			if(wd.name.equals(name))	ids.add(id);
		}
		return ids;
	}
	/* 指定の品詞を持つWordのIDを返す */
	public List<Integer> collectAllTagWords(String[][] tagNames) {
		List<String[]> tagNameList = Arrays.asList(tagNames);
		List<Integer> taggedIDs = new ArrayList<Integer>();
		for(final int wordID: wordIDs) {
			Word word = Word.get(wordID);
			for (final String[] tagsArray: tagNameList){
				if(word.hasAllTags(tagsArray))	taggedIDs.add(wordID);
			}
		}
		return taggedIDs;
	}
	/* 指定の品詞を"全て"持つWordが含まれているか判定 */
	public boolean haveSomeTagWord(String[][] tagNames) {
		for(final int wordID: wordIDs) {
			Word word = Word.get(wordID);
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

		for(ListIterator<Integer> li = wordIDs.listIterator(wordIDs.size()); li.hasPrevious(); ) {
			int wordID = li.previous();				// wordも
			Word word = Word.get(wordID);
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
		List<Integer> mainIDs = wordIDs.subList(fromIndex, toIndex);

		Phrase properNoun = new Phrase();	// 固有名詞として扱う
		properNoun.setPhrase(mainIDs, clauseID, true);
		mainIDs.clear();
		this.wordIDs.add(fromIndex, properNoun.id);
	}

	/* Clauseを文字列で返す */
	@Override
	public String toString() {
		String clauseName = new String();
		for(int orgid: wordIDs) {
			clauseName += Word.get(orgid).toString();
		}
		return clauseName;
	}
	@Override
	public void printDetail() {
		System.out.println(toString());
	}


	/* Clauseの係り受け関係を更新 */
	/* 全てのChunkインスタンスのdependUponが正しいことが前提の設計 */
	public static void updateAllDependency() {
		for(final Clause cls: Clause.allClausesList) cls.beDepended.clear();	// 一度全ての被係り受けをまっさらにする
		for(final Clause cls: Clause.allClausesList) {
			int depto = cls.dependUpon;
			if(depto != -1) Clause.get(depto).beDepended.add(cls.clauseID);
		}
	}

}
