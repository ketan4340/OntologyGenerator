package grammar;

import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class Sentence implements GrammarInterface{
	public static int sentSum = 0;

	public int id;
	public List<Integer> clauseIDs; // Clauseのリストで文を構成する

	public Sentence() {
		id = sentSum++;
		clauseIDs = new ArrayList<Integer>();
	}
	public Sentence(List<Integer> clauseList) {
		id = sentSum++;
		clauseIDs = clauseList;
	}
	public void setSentence(List<Integer> clauseList) {
		clauseIDs = clauseList;
	}

	public int indexOfC(int clauseID) {
		return clauseIDs.indexOf(clauseID);
	}
	public List<Integer> indexesOfC(List<Integer> clauseIDList) {
		List<Integer> indexList = new ArrayList<Integer>(clauseIDList.size());
		for(final int clauseID: clauseIDList) {
			indexList.add(indexOfC(clauseID));
		}
		return indexList;
	}
	public int nextC(int clauseID) {
		int prevIndex = indexOfC(clauseID)+1;
		if(prevIndex < 0 || clauseIDs.size() <= prevIndex) return -1;
		int prevClauseID = clauseIDs.get(prevIndex);
		return prevClauseID;
	}
	public int previousC(int clauseID) {
		int prevIndex = indexOfC(clauseID)-1;
		if(prevIndex < 0 || clauseIDs.size() <= prevIndex) return -1;
		int prevClauseID = clauseIDs.get(prevIndex);
		return prevClauseID;
	}

	public Sentence subSentence(int fromIndex, int toIndex) {
		Sentence subSent = new Sentence();
		List<Integer> subIDs = new ArrayList<Integer>(clauseIDs.subList(fromIndex, toIndex));
		subSent.setSentence(subIDs);
		return subSent;
	}

	public List<Integer> collectWords(String name) {
		List<Integer> words = new ArrayList<Integer>();
		for(final int clauseID: clauseIDs) {
			Clause clause = Clause.get(clauseID);
			words.addAll(clause.collectWords(name));	// 各Chunk内を探す
		}
		return words;
	}
	/* 渡された品詞に一致するWordのIDを返す */
	public List<Integer> collectTagWords(String[][] tagNames) {
		List<Integer> taggedWords = new ArrayList<Integer>();
		for(final int clauseID: clauseIDs) {
			Clause clause = Clause.get(clauseID);
			taggedWords.addAll(clause.collectAllTagWords(tagNames));	// 各Chunk内を探す
		}
		return taggedWords;
	}
	/* 渡された品詞に一致するWordを含むChunkのIDを返す */
	public List<Integer> collectTagClauses(String[][] tagNames) {
		List<Integer> taggedClauses = new ArrayList<Integer>();
		for(final int clauseID: clauseIDs) {
			Clause clause = Clause.get(clauseID);
			if(clause.haveSomeTagWord(tagNames))	taggedClauses.add(clauseID);	// 各Chunk内を探す
		}
		return taggedClauses;
	}
	public List<Integer> collectClausesEndWith(String[][][] tagNamess) {
		List<Integer> taggedClauses = new ArrayList<Integer>();
		for(final int clauseID: clauseIDs) {
			Clause clause = Clause.get(clauseID);
			for(final String[][] tagNames: tagNamess) {
				if(clause.endWith(tagNames, true)) {
					taggedClauses.add(clauseID);	// 各Chunk内を探す
					break;
				}
			}
		}
		return taggedClauses;
	}

	public int uniteClauses(List<Integer> connectClauseList, int baseIndex) {
		int baseClauseID = connectClauseList.get(baseIndex);	// tagや係り受けはこのChunkに依存
		Clause baseClause = Clause.get(baseClauseID);			// 全てこのChunkに収める
		List<Integer> newClauseIDs = clauseIDs;		// Chunkを組み替えた新たなchunkIDsをここに

		// 渡されたClauseがSentence上で連続しているか
		Map<Integer, Boolean> continuity = getContinuity(connectClauseList);
		continuity.remove(continuity.size()-1);	// 最後は必ずfalseなので抜いておく
		for(final Map.Entry<Integer, Boolean> entry: continuity.entrySet()) {
			if(entry.getValue() == false) {
				System.out.println("error: Not serial in sentence.");
				return baseClauseID;
			}
		}
		System.out.println("connectChunkList:"+connectClauseList + "\tbase:" + baseClauseID);

		List<Integer> phraseWords = new ArrayList<Integer>();	// 新しいPhraseの元になるWord
		List<Integer> functionWords = new ArrayList<Integer>();	// Phrase完成後につなげる接続詞を保持
		int depto = baseClause.dependUpon;						// 最後尾のChunkがどのChunkに係るか

		for(Iterator<Integer> itr = connectClauseList.iterator(); itr.hasNext(); ) {
			int connectClauseID = itr.next();
			Clause connectClause = Clause.get(connectClauseID);

			if(connectClauseID == baseClauseID) {

			}else {
				// 元ChunkのWordはbaseChunkに属するように変える
				for(final int wdID: connectClause.wordIDs) {
					Word.get(wdID).belongClause = baseClauseID;
				}
				// 全ての元Chunkの係り先をbaseChunkに変える
				for(final int bedep: connectClause.beDepended) {
					Clause.get(bedep).dependUpon = baseClauseID;
				}
			}
			if(!itr.hasNext()) {		// 最後尾の場合
				phraseWords.add(connectClause.getMainWord());			// 被修飾語を入れる
				functionWords.addAll(connectClause.getFunctionWords());	// それに繋がる機能語を入れる
				//depto = connectChunk.dependUpon;
			}else {
				phraseWords.addAll(connectClause.wordIDs);
			}
		}

		// 新しいPhraseを作成
		Phrase nph = new Phrase();
		nph.setPhrase(phraseWords, baseClauseID, false);
		List<Integer> clauseBaseWords = new ArrayList<Integer>();
		clauseBaseWords.add(nph.id);
		clauseBaseWords.addAll(functionWords);
		Clause.get(baseClauseID).setClause(clauseBaseWords, depto);

		// 古いChunkを削除して新しいChunkを挿入
		connectClauseList.remove(baseIndex);
		newClauseIDs.removeAll(connectClauseList);

		updateDependency();
		setSentence(newClauseIDs);
		printDep();
		return baseClauseID;
	}

	/* 渡された品詞の連続にマッチする単語を連結する */
	public void connectPattern(String[][] pattern, int baseIndex) {
		if(baseIndex >= pattern.length) System.out.println("index error");

		Set<List<Integer>> matchingWordsSet = new HashSet<List<Integer>>(5);
		List<Integer> matchingWords = new ArrayList<Integer>(pattern.length);
		int ptnIdx = 0;
		for(ListIterator<Integer> li = wordIDs().listIterator(); li.hasNext(); ) {
			int wordID = li.next();
			Word word = Word.get(wordID);
			if(word.hasAllTags(pattern[ptnIdx])) {	// マッチした場合
				ptnIdx++;		// 次のパターンへ
				matchingWords.add(wordID);
			}else {									// マッチしない場合
				for(int i=0; i<ptnIdx; i++) li.previous();	// カーソルを最初にマッチした単語まで戻す
				ptnIdx = 0;		// パターンも一番目にリセット
				matchingWords.clear();
			}

			// パターンの最後までマッチしたら
			if(ptnIdx >= pattern.length) {
				matchingWordsSet.add(matchingWords);	// 連続したWordをしまう
				matchingWords = new ArrayList<Integer>();	// 新しいインスタンスを用意
				ptnIdx = 0;		// パターンのカーソルをリセット
			}
		}

		//printW();
		//System.out.println(matchingWordsSet);

		// Wordの列からChunkの列に直す
		Set<List<Integer>> connectClausesSet = new HashSet<List<Integer>>(matchingWordsSet.size());
		for(final List<Integer> matchedWords: matchingWordsSet) {
			List<Integer> connectClauses = new ArrayList<Integer>(matchingWords.size());
			for(final int id: matchedWords) {
				Word matchedWord = Word.get(id);
				int belong = matchedWord.belongClause;
				if(!connectClauses.contains(belong))
					connectClauses.add(belong);	// どのChunkに所属するか
			}
		}
		// 複数のChunkを結合して新しいChunkを作成
		for(final List<Integer> connectClauses: connectClausesSet) {
			uniteClauses(connectClauses, baseIndex);
		}
	}

	/* 渡された品詞を繋げる */
	/* Clauseを跨いで連結はしない */
	public void connect(String[][] tagNames) {
		for(final int clauseID: clauseIDs) {
			Clause clause = Clause.get(clauseID);
			clause.connect(tagNames);
		}
	}

	/* 渡された品詞で終わるClauseを次のClauseに繋げる */
	public void connect2Next(String[][] tags, boolean ignoreSign) {
		List<Integer> newIDlist = clauseIDs;
		List<Integer> modifyClauseList = new ArrayList<Integer>();

		for(final int clauseID: clauseIDs) {
			Clause clause = Clause.get(clauseID);
			for(final String[] tag: tags) {
				String[][] tagArray = {tag};
				if(clause.endWith(tagArray, ignoreSign)) {	// 指定の品詞で終わるChunkなら(記号考慮)
					modifyClauseList.add(clauseID);
				}
			}
		}

		List<List<Integer>> phraseBaseClausesList = makeModificationList(modifyClauseList);
		//System.out.println("\tph_clauses" + phraseBaseClausesList);
		// 複数のClauseを結合して新しいClauseを作成
		for(final List<Integer> phraseBaseClauses: phraseBaseClausesList) {
			Clause nch = new Clause();
			nch.uniteClauses(phraseBaseClauses);
			// 古いClauseを削除して新しいClauseを挿入
			newIDlist.add(newIDlist.indexOf(phraseBaseClauses.get(0)), nch.clauseID);
			newIDlist.removeAll(phraseBaseClauses);
			updateDependency();
		}
		setSentence(newIDlist);
	}

	/* 上記connect2Nextの補助 */
	/* 修飾節のリストから修飾節被修飾節のセットを作る */
	private List<List<Integer>> makeModificationList(List<Integer> modifyClauseList) {
		List<List<Integer>> phraseBaseClausesList = new ArrayList<List<Integer>>();
		List<Integer> phraseBaseClauses = new ArrayList<Integer>();
		for(final int modifyClause: modifyClauseList) {
			if(!phraseBaseClauses.contains(modifyClause))
				phraseBaseClauses.add(modifyClause);

			int nextIndex = clauseIDs.indexOf(modifyClause) + 1;// 修飾節の次の文節が被修飾節
			if(nextIndex == clauseIDs.size()) continue;			// 修飾節が文末なら回避

			int nextClause = clauseIDs.get(nextIndex);			// 修飾語の直後に被修飾語があることが前提の設計
			if(modifyClauseList.contains(nextClause)) continue;	// 三文節以上連続の可能性を考慮

			phraseBaseClauses.add(nextClause);
			phraseBaseClausesList.add(phraseBaseClauses);
			phraseBaseClauses = new ArrayList<Integer>();
		}
		if(phraseBaseClauses.size() > 1) phraseBaseClausesList.add(phraseBaseClauses);
		return phraseBaseClausesList;
	}

	/* 渡したClauseが文中で連続しているかを<clauseID, Boolean>のMapで返す */
	/* 例:indexのリストが(2,3,4,6,8,9)なら(T,T,F,F,T,F) */
	public Map<Integer, Boolean> getContinuity(List<Integer> clauseIDList) {
		Map<Integer, Boolean> continuity = new LinkedHashMap<Integer, Boolean>(clauseIDList.size());
		List<Integer> clauseIndexList = indexesOfC(clauseIDList);

		Iterator<Integer> liID = clauseIDList.listIterator();
		Iterator<Integer> liIdx = clauseIndexList.listIterator();
		int currentIdx = liIdx.next();
		while(liIdx.hasNext() && liID.hasNext()) {
			int nextIdx = liIdx.next();
			int clsID = liID.next();
			if(currentIdx+1 == nextIdx) {	// indexが連続しているか
				continuity.put(clsID, true);
			}else {							// 否か
				continuity.put(clsID, false);
			}
			currentIdx = nextIdx;
		}
		continuity.put(liID.next(), false);	// 最後は絶対連続しないからfalse

		return continuity;
	}

	/* 複数の述語を持つ文を述語ごと短文に切り分ける */
	public List<Sentence> divide1() {
		List<Sentence> partSentList = new ArrayList<Sentence>(5);

		/* 主語を全て探し，それらが連続しているか否かを調べる */
		List<Integer> subjectList = getSubjectList(false);	// 主節のリスト
		if(subjectList.isEmpty()) return partSentList;		// 文中に主語がなければ終了

		int lastClauseID = clauseIDs.get(clauseIDs.size()-1);	// 文の最後尾Clause
		Clause lastClause = Clause.get(lastClauseID);
		if(subjectList.contains(lastClauseID)) {	// 文の最後尾が主節の場合
			// おそらく固有名詞を正しく判定できていないせい
			// 最後尾の文節は一つの名詞にする
			lastClause.nounize(0, lastClause.wordIDs.size());
			int idx = subjectList.indexOf(lastClauseID);
			subjectList.remove(idx);
		}


		List<Integer> headSubjectList = new ArrayList<Integer>(subjectList.size());	// 先頭の主語群を集める

		Map<Integer, Boolean> sbjContinuityMap = getContinuity(subjectList);	// 主節の連続性を真偽値で表す
		sbjContinuityMap.put(-1, null);		// 最後の要素を表すサイン

		Iterator<Map.Entry<Integer, Boolean>> mapItr = sbjContinuityMap.entrySet().iterator();
		Map.Entry<Integer, Boolean> currentSbjEntry = mapItr.next();
		while(mapItr.hasNext()) {
			Map.Entry<Integer, Boolean> nextSbjEntry = mapItr.next();
			int sbjID = currentSbjEntry.getKey();				// 主節のID
			Clause directSubject = Clause.get(sbjID);			// 主節のClause
			boolean sbjContinuity = currentSbjEntry.getValue();	// 主節のあとに別の主節が隣接しているか
			Clause copySubject = directSubject.clone();

			if(sbjContinuity) {	// このClauseの次も主節である場合
				String[][] tag_Ha = {{"係助詞", "は"}};
				Word no = new Word();	// 助詞・連体化"の"を新たに用意
				no.setWord("の", Arrays.asList("助詞","連体化","*","*","*","*","の","ノ","ノ"), copySubject.clauseID, false);
				int index_Ha = copySubject.indexOfW(copySubject.collectAllTagWords(tag_Ha).get(0));
				copySubject.wordIDs.set(index_Ha, no.id);	// "は"の代わりに"の"を挿入

				headSubjectList.add(copySubject.clauseID);		// 連続した主節は貯め置きしとく

			}else {				// このClauseの次は主節ではない場合
				// 主部をまとめる
				// 新しい主節のインスタンスを用意
				List<Integer> copiedHeadSubjectList = new ArrayList<Integer>(headSubjectList);	// 使い回すので複製
				copiedHeadSubjectList.add(copySubject.clauseID);
				int headClauseID = copiedHeadSubjectList.get(0);
				for(Iterator<Integer> li = copiedHeadSubjectList.listIterator(1); li.hasNext(); ) {
					int nextChunkID = li.next();
					Clause.get(headClauseID).dependUpon = nextChunkID;	// 複数の主節は隣に係る
					headClauseID = nextChunkID;
				}
				// 連続した主節はこの場で結合する
				Clause newSbjClause = new Clause();
				newSbjClause.uniteClauses(copiedHeadSubjectList);

				// 述部を切り離す
				int fromIndex = indexOfC(directSubject.clauseID)+1;	// 述部切り取りの始点は主節の次
				int toIndex = (nextSbjEntry.getKey() != -1)			// 述部切り取りの終点は
						? indexOfC(nextSbjEntry.getKey())			// 次の主節の位置
						: clauseIDs.size();							// なければ文末
				//System.out.println("\t"+"mainPre(" + fromIndex + "~" + toIndex + ")");
				List<Integer> partPredicates = clauseIDs.subList(fromIndex, toIndex);	// 切り取った述部
				Clause.get(partPredicates.get(partPredicates.size()-1)).dependUpon = -1;	// 最後尾の述語はどこにも係らない

				// 述部の分割
				int nextPredicateID = directSubject.dependUpon;			// 次の述語のID
				//System.out.println("directSbj = " + directSubject.chunkID + ", " + "nextPre = " + nextPredicateID);
				if(nextPredicateID == -1) break;
				int fromPrdIndex = indexOfC(directSubject.clauseID)+1;	// 述部分割の始点
				int toPrdIndex = indexOfC(nextPredicateID)+1;			// 述部分割の終点
				while(nextPredicateID != -1) {
					Clause nextPredicateClause = Clause.get(nextPredicateID);
					//System.out.println("\t\t"+"partPre(" + fromPrdIndex + "~" + toPrdIndex + ")");
					List<Integer> piecePredicates = clauseIDs.subList(fromPrdIndex, toPrdIndex);
					// 主節は新しいインスタンスを用意
					Clause newSubject_c = newSbjClause.clone();
					newSubject_c.dependUpon = nextPredicateID;

					List<Integer> partChunkList = new ArrayList<Integer>();		// 短文を構成するChunkのリスト
					partChunkList.add(newSubject_c.clauseID);	// 結合主部セット
					partChunkList.addAll(piecePredicates);		// 部分述部セット
					// 短文生成
					Sentence partSent = new Sentence();
					partSent.setSentence(partChunkList);
					partSentList.add(partSent);

					// 次の述語を見つけ，fromとtoを更新
					nextPredicateID = nextPredicateClause.dependUpon;
					fromPrdIndex = toPrdIndex;
					toPrdIndex = indexOfC(nextPredicateID)+1;
				}
			}
		currentSbjEntry = nextSbjEntry;
		}

		return partSentList;
	}

	/* メインの主語が係る述語ごとに分割 */
	public List<Sentence> divide2() {
		List<Sentence> shortSentList = new ArrayList<Sentence>(5);
		/* 主語を全て探し，それらが連続しているか否かを調べる */
		List<Integer> subjectList = getSubjectList(false);		// 主語のリスト

		int lastClauseID = clauseIDs.get(clauseIDs.size()-1);	// 文の最後尾Chunk
		Clause lastClause = Clause.get(lastClauseID);
		if(subjectList.contains(lastClauseID)) {	// 文の最後尾が主節の場合
			// おそらく固有名詞を正しく判定できていないせい
			// 最後尾の文節は一つの名詞にする
			lastClause.nounize(0, lastClause.wordIDs.size());
			subjectList.remove(subjectList.indexOf(lastClauseID));
		}
		if(subjectList.isEmpty()) return shortSentList;	// 文中に主語がなければ終了

		// 主節の連続性を表す真偽値のリスト
		Map<Integer, Boolean> subjectsContinuity = getContinuity(subjectList);
		// 文頭に連続で並ぶ主語は文全体に係るとみなし、集めて使い回す
		List<Integer> commonSubjectsOrigin = new ArrayList<Integer>(subjectList.size());
		int mainSubjectID = -1;	Clause mainSubject;
		for(Map.Entry<Integer, Boolean> entry: subjectsContinuity.entrySet()) {
			commonSubjectsOrigin.add(entry.getKey());
			if(!entry.getValue()) {	// 主語の連続が途切れたら
				mainSubjectID = entry.getKey();	// 後続の多くの述語に係る、核たる主語
				break;				// 核主語集め完了
			}
		}
		mainSubject = Clause.get(mainSubjectID);
		List<Integer> predicateIDs = mainSubject.getAllDepending();
		predicateIDs.retainAll(clauseIDs);

		if(predicateIDs.size() < 2) {	// 述語が一つならスルー
			shortSentList.add(this);
			return shortSentList;
		}

		/* 文章分割(dependUpon依存) */
		int fromIndex = 0, toIndex;
		for(final int predicateID: predicateIDs) {
			Clause predicate = Clause.get(predicateID);
			predicate.dependUpon = -1;	// 文末の述語となるので係り先はなし(-1)
			toIndex = indexOfC(predicateID) + 1;		// 述語も含めて切り取るため+1
			Sentence subSent = subSentence(fromIndex, toIndex);
			// 文頭の主語は全ての分割後の文に係る
			List<Integer> commonSubjects = Clause.cloneAll(commonSubjectsOrigin);

			if(fromIndex!=0) {		// 最初の分割文は、新たに主語を挿入する必要ない
				int subSentFirst = subSent.clauseIDs.get(0);	// 分割文の先頭
				if(subjectList.contains(subSentFirst)) {		// それが主語なら
					commonSubjectsOrigin.add(subSentFirst);		// 後続の短文にも係るので保管
				}
				subSent.clauseIDs.addAll(0, commonSubjects);	// 共通の主語を挿入
			}

			for(Iterator<Integer> itr = commonSubjects.iterator(); itr.hasNext(); ) {
				int commonSbjID = itr.next();
				Clause commonSubject = Clause.get(commonSbjID);
				commonSubject.dependUpon = predicateID;	// 係り先を正す
			}
			subSent.gatherDepending(predicateID);
			subSent.updateDependency();
			shortSentList.add(subSent);

			int commonSubjectsSize = commonSubjectsOrigin.size();
			if(commonSubjectsSize > 1) {
				int nextClause = nextC(predicateID);
				if(subjectList.contains(nextClause))	// かつ次が主語である
					commonSubjectsOrigin.remove(commonSubjectsSize-1);
			}
			fromIndex = toIndex;
		}
		return shortSentList;
	}

	/* 述語に係る{動詞,形容詞,名詞,~だ,接続助詞}ごとに分割 */
	public List<Sentence> divide3() {
		List<Sentence> partSentList = new ArrayList<Sentence>(5);
		/* 主語を全て探し，それらが連続しているか否かを調べる */
		List<Integer> subjectList = getSubjectList(false);	// 主語のリスト
		if(subjectList.isEmpty()) return partSentList;	// 文中に主語がなければ終了

		int lastClauseID = clauseIDs.get(clauseIDs.size()-1);
		Clause lastClause = Clause.get(lastClauseID);	// 文の最後尾Chunk
		if(subjectList.contains(lastClauseID)) {		// 文の最後尾が主節の場合
			// おそらく固有名詞を正しく判定できていないせい
			// 最後尾の文節は一つの名詞にする
			lastClause.nounize(0, lastClause.wordIDs.size());
			subjectList.remove(subjectList.indexOf(lastClauseID));
		}
		// 主節の連続性を表す真偽値のリスト
		Map<Integer, Boolean> subjectsContinuity = getContinuity(subjectList);
		// 文頭に連続で並ぶ主語は文全体に係るとみなし、集めて使い回す
		List<Integer> commonSubjectsOrigin = new ArrayList<Integer>(subjectList.size());
		for(Map.Entry<Integer, Boolean> entry: subjectsContinuity.entrySet()) {
			commonSubjectsOrigin.add(entry.getKey());
			if(!entry.getValue()) {	// 主語の連続が途切れたら
				break;				// 核主語集め完了
			}
		}

		/* 文章分割(dependUpon依存) */
		String[][] tagParticle = {{"助詞", "-て"}};	// "て"以外の助詞
		String[][] tagAdverb = {{"副詞"}};
		List<Integer> predicateIDs = new ArrayList<Integer>();
		for(final int toLast: lastClause.beDepended) {
			Clause clause2Last = Clause.get(toLast);
			// 末尾が"て"を除く助詞または副詞でないChunkを追加
			if( !clause2Last.endWith(tagParticle, true) && !clause2Last.endWith(tagAdverb, true) )
				predicateIDs.add(toLast);
		}
		predicateIDs.add(lastClauseID);
		predicateIDs.retainAll(clauseIDs);

		//List<Integer> commonObjects = new ArrayList<Integer>();	// 複数の述語にかかる目的語を保管

		if(predicateIDs.size() < 2) { // 述語が一つならスルー
			partSentList.add(this);
			return partSentList;
		}

		int fromIndex = 0, toIndex;
		for(Iterator<Integer> itr = predicateIDs.iterator(); itr.hasNext(); ) {
			int predicateID = itr.next();
			Clause predicate = Clause.get(predicateID);
			predicate.dependUpon = -1;	// 分割後、当該述語は文末にくるので係り先はなし(-1)
			toIndex = indexOfC(predicateID) + 1;	// 述語も含めて切り取るため+1
			Sentence subSent = subSentence(fromIndex, toIndex);
			// 文頭の主語は全ての分割後の文に係る
			List<Integer> commonSubjects = Clause.cloneAll(commonSubjectsOrigin);

			if(fromIndex!=0) {		// 最初の分割文は、新たに主語を挿入する必要ない
				int subSentFirst = subSent.clauseIDs.get(0);	// 分割文の先頭
				if(subjectList.contains(subSentFirst)) {		// それが主語なら
					commonSubjectsOrigin.add(subSentFirst);		// 後続の短文にも係るので保管
				}
				subSent.clauseIDs.addAll(0, commonSubjects);	// 共通の主語を挿入
			}
			// 主語の係り先を正す
			for(final int sbj: subSent.getSubjectList(false)) Clause.get(sbj).dependUpon = predicateID;
			// 係り元の更新
			subSent.gatherDepending(predicateID);
			subSent.updateDependency();
			partSentList.add(subSent);

			// 述語のあとに主語があれば共通主語の最後尾を切り捨てる
			int commonSubjectsSize = commonSubjectsOrigin.size();
			if(commonSubjectsSize > 1) {
				int nextClause = nextC(predicateID);
				if(subjectList.contains(nextClause))	// 次が主語
					commonSubjectsOrigin.remove(commonSubjectsSize-1);
			}

			fromIndex = toIndex;
		}
		return partSentList;
	}

	/* 渡されたClauseIDにdependを向ける */
	private void gatherDepending(int toID) {
		for(Iterator<Integer> itr = clauseIDs.iterator(); itr.hasNext(); ) {
			int clauseID = itr.next();
			if(!itr.hasNext()) break;	// 最後の述語だけは係り先が-1なのでスルー
			Clause clause = Clause.get(clauseID);
			int depto = clause.dependUpon;
			if(!clauseIDs.contains(depto)) clause.dependUpon = toID;
		}
	}

	/* 主語のリストを得る */
	private List<Integer> getSubjectList(boolean ga) {
		List<Integer> subjectList;
		if(ga) {	// "が"は最初の一つのみ!!
			String[][][] tags_Ha_Ga = {{{"係助詞", "は"}}, {{"格助詞", "が"}}};	// "は"
			String[][][] tags_Ga = {{{"格助詞", "が"}}};	//"が"
			String[][] tag_De = {{"格助詞", "で"}};	// "で"
			List<Integer> clause_Ha_Ga_List = new ArrayList<Integer>(collectClausesEndWith(tags_Ha_Ga));	// 係助詞"は"を含むClause
			List<Integer> clause_Ga_List = new ArrayList<Integer>(collectClausesEndWith(tags_Ga));	// 係助詞"は"を含むClause
			List<Integer> clause_De_List = new ArrayList<Integer>(collectTagClauses(tag_De));	// 格助詞"で"を含むClause
			if(!clause_Ga_List.isEmpty())	clause_Ga_List.remove(0);
			clause_Ha_Ga_List.removeAll(clause_Ga_List);
			clause_Ha_Ga_List.removeAll(clause_De_List);	// "は"と"が"を含むClauseのうち、"で"を含まないものが主語
			subjectList = clause_Ha_Ga_List;
		}else {
			String[][][] tags_Ha = {{{"係助詞", "は"}}};	// "は"
			String[][] tag_De = {{"格助詞", "で"}};	// "で"
			List<Integer> clause_Ha_List = new ArrayList<Integer>(collectClausesEndWith(tags_Ha));	// 係助詞"は"を含むClause
			List<Integer> clause_De_List = new ArrayList<Integer>(collectTagClauses(tag_De));	// 格助詞"で"を含むClause
			clause_Ha_List.removeAll(clause_De_List);		// "は"を含むClauseのうち、"で"を含まないものが主語
			subjectList = clause_Ha_List;		// 主語のリスト
		}
		return subjectList;
	}

	public void uniteSubject() {
		List<Integer> subjectList = getSubjectList(false);
		if(subjectList.isEmpty()) return;

		// 主節の連続性を表す真偽値のリスト
		Map<Integer, Boolean> subjectsContinuity = getContinuity(subjectList);
		//System.out.println("subjContinuity: " + subjectsContinuity);
		String[][] tag_Ha = {{"係助詞", "は"}};
		//String[][] tag_Ga = {{"格助詞", "が"}};
		// 文頭に連続で並ぶ主語は文全体に係るとみなし、集めて使い回す
		for(Map.Entry<Integer, Boolean> entry: subjectsContinuity.entrySet()) {
			int sbjID = entry.getKey();		boolean sbjCnt = entry.getValue();
			if(!sbjCnt) break;	// 連続した主語の最後尾には必要ない

			Clause subject = Clause.get(sbjID);

			//if(subject.endWith(tag_Ga, true)) {
				/*
				Word ha = new Word();	// 係り助詞"が"を新たに用意
				ha.setWord("は", Arrays.asList("助詞","係助詞","*","*","*","*","は","ハ","ワ"), subject.clauseID, false);
				int index_Ga = subject.indexOfW(subject.collectAllTagWords(tag_Ga).get(0));
				subject.wordIDs.set(index_Ga, ha.wordID);	// "は"の代わりに"が"を挿入
				*/
			//}else {
				Word no = new Word();	// 助詞・連体化"の"を新たに用意
				no.setWord("の", Arrays.asList("助詞","連体化","*","*","*","*","の","ノ","ノ"), subject.clauseID, false);
				int index_Ha = subject.indexOfW(subject.collectAllTagWords(tag_Ha).get(0));
				subject.wordIDs.set(index_Ha, no.id);	// "は"の代わりに"の"を挿入
			//}
		}
		String[][] tags_NP = {{"助詞", "連体化"}};
		connect2Next(tags_NP, true);
	}

	/* 文章から関係を見つけtripleにする */
	public List<String[]> extractRelation() {
		List<String[]> relations = new ArrayList<String[]>();

		List<Integer> subjectList = getSubjectList(true);	// 主語を整えたところで再定義
		if(subjectList.isEmpty()) return relations;

		// 主節
		Clause subjectClause = Clause.get(subjectList.get(0));
		int sbjMainID = subjectClause.getMainWord();	if(sbjMainID == -1) return relations;
		Word subjectWord = Word.get(sbjMainID);							// 主語
		String subject = subjectWord.name;
		// 述節
		Clause predicateClause = Clause.get(subjectClause.dependUpon);	if(predicateClause == null) return relations;
		int prdMainID = predicateClause.getMainWord();	if(prdMainID == -1) return relations;
		Word predicateWord = Word.get(prdMainID);						// 述語
		// 述部(主節に続く全ての節)
		String predicatePart = subSentence(clauseIDs.indexOf(subjectClause.clauseID)+1, clauseIDs.size()).toString();
		//List<Clause> complementClauses;								// 補部
		//Word complementWord;											// 補語

		String[][] tag_Not = {{"助動詞", "ない"}, {"助動詞", "不変化型", "ん"},  {"助動詞", "不変化型", "ぬ"}};
		boolean not = (predicateClause.haveSomeTagWord(tag_Not))? true: false;	// 述語が否定かどうか

		//System.out.println(subjectClause.toString() + "->" + predicateClause.toString());

		/* 述語が[<名詞>である。]なのか[<動詞>する。]なのか[<形容詞>。]なのか */
		String[][] tagVerb = {{"動詞"}, {"サ変接続"}};
		String[][] tagAdjective = {{"形容詞"}};

		/* リテラル情報かどうか */
		/* 長さ */
		String regexLength = "(.*?)[長径]?.*?(\\d+(\\.\\d+)?)([ア-ンa-zA-Zー－]+).*?";	// 「~(数字)(単位)~」を探す
		Pattern ptrnLength = Pattern.compile(regexLength);
		Matcher mtchLength = ptrnLength.matcher(predicatePart);
		boolean boolLength = mtchLength.matches();
		/* 重さ */
		String regexWeight = "(.*?)重.*?(\\d+(\\.\\d+)?)([ア-ンa-zA-Zー－]+).*?";	// 「~(数字)(単位)~」を探す
		Pattern ptrnWeight = Pattern.compile(regexWeight);
		Matcher mtchWeight = ptrnWeight.matcher(predicatePart);
		boolean boolWeight = mtchWeight.matches();

		if(boolLength || boolWeight) {
			if(boolLength) {
				String blank = "_:"+subjectWord.name+"-length";
				relations.add( new String[]{subjectWord.name, "hasLength", blank} );		// 空白ノード
				relations.add( new String[]{blank, "rdf:value", mtchLength.group(2)} );			// リテラル
				relations.add( new String[]{blank, "exterms:units", mtchLength.group(4)} );	// 単位
			}
			if(boolWeight) {
				String blank = "_:"+subjectWord.name+"-weight";
				relations.add( new String[]{subjectWord.name, "hasWeight", blank} );		// 空白ノード
				relations.add( new String[]{blank, "rdf:value", mtchWeight.group(2)} );		// リテラル
				relations.add( new String[]{blank, "exterms:units", mtchWeight.group(4)} );	// 単位
			}
		/* 述語が動詞 */
		}else if( predicateClause.haveSomeTagWord(tagVerb) ) {
			/* "がある"かどうか */
			String[][] tag_Have = {{"動詞", "ある"}, {"動詞", "もつ"}, {"動詞", "持つ"}};		// 動詞の"ある"(助動詞ではない)
			boolean boolHave = predicateClause.haveSomeTagWord(tag_Have);
			/* "~の総称" */
			String regexGnrnm = "(.*?)(の総称)";				// 「〜の総称」を探す
			Pattern ptrnGnrnm = Pattern.compile(regexGnrnm);
			Matcher mtchGnrnm = ptrnGnrnm.matcher(predicateClause.toString());
			boolean boolGnrnm = mtchGnrnm.matches();

			if(boolHave) {			// "~がある","~をもつ"
				Clause previousClause = Clause.get(previousC(predicateClause.clauseID));	// 動詞の一つ前の文節
				if(previousClause == null) return relations;
				String part = Word.get(previousClause.getMainWord()).name;	// その主辞の文字列
				String[][] tag_Ga_Wo = {{"格助詞", "が"}, {"格助詞", "を"}};
				if(previousClause.haveSomeTagWord(tag_Ga_Wo)) {
					relations.add( new String[]{part, "dcterms:isPartOf", subject} );
					relations.add( new String[]{subject, "dcterms:hasPart", part} );
				}

			}else if(boolGnrnm) {	// "~の総称"
				relations.add( new String[]{subjectWord.name, "owl:equivalentClass", mtchGnrnm.group(1)} );

			}else {					// その他の動詞

				String verb = predicateWord.tags.get(6);	// 原形を取り出すためのget(6)
				String object = null;

				// 格助詞"に","を","へ"などを元に目的語を探す
				String[][] tag_Ni_Wo = {{"格助詞", "が"}, {"格助詞", "に"}, {"格助詞", "を"}};	// 目的語oと述語pを結ぶ助詞
				List<Integer> clauses_Ni_Wo = collectTagClauses(tag_Ni_Wo);
				if(!clauses_Ni_Wo.isEmpty()) {	// 目的語あり
					Clause chunk_Ni_Wo = Clause.get(clauses_Ni_Wo.get(0));
					Word word_Ni_Wo = Word.get(chunk_Ni_Wo.getMainWord());	// "に"または"を"の主辞
					object = word_Ni_Wo.name;
				}else {							// 目的語なし
					object = null;
				}
				String[] spo = {subject, verb, object};
				relations.addAll(makeObjectiveProperty(spo, not));
			}


		/* 述語が形容詞 */
		}else if(predicateClause.haveSomeTagWord(tagAdjective)) {
			String adjective = predicateWord.tags.get(6);
			Clause previousClause = Clause.get(previousC(predicateClause.clauseID));	// 形容詞の一つ前の文節
			if(previousClause == null) return relations;
			String[][] tag_Ga = {{"格助詞", "が"}};
			if(previousClause.haveSomeTagWord(tag_Ga)) {
				String part = Word.get(previousClause.getMainWord()).name;	// その主辞の文字列
				subject += "の"+part;
			}
			relations.add( new String[]{adjective, "attributeOf", subject} );

		/* 述語が名詞または助動詞 */
		}else {
			/* 別名・同義語かどうか */
			String regexSynonym = "(.*?)((に同じ)|(の別名)|(の略)|(のこと)|(の異称))";	// 「〜の別名」「〜に同じ」を探す
			Pattern ptrnSynonym = Pattern.compile(regexSynonym);
			Matcher mtchSynonym = ptrnSynonym.matcher(predicatePart);
			boolean boolSynonym = mtchSynonym.matches();
			/* 一種・一品種かどうか */
			String regexKind = "(.*?)((の一種)|(の一品種))";					// 「〜の一種」「〜の一品種」を探す
			Pattern ptrnKind = Pattern.compile(regexKind);
			Matcher mtchKind = ptrnKind.matcher(predicatePart);
			boolean boolKind = mtchKind.matches();
			/* 形容動詞語幹を含むか */
			String[][] tag_Adjective = {{"形容動詞語幹"}};
			boolean boolAdjective = predicateClause.haveSomeTagWord(tag_Adjective);

			if(boolSynonym) {
				relations.add( new String[]{subjectWord.name, "owl:sameClassAs", mtchSynonym.group(1)} );
			}else if(boolKind) {
				relations.add( new String[]{subjectWord.name, "rdf:type", mtchKind.group(1)} );
			}else if(boolAdjective) {
				String adjective = predicateWord.tags.get(6);
				relations.add( new String[]{adjective, "attributeOf", subject} );
			}else {
				relations.add( new String[]{subjectWord.name, "rdfs:subClassOf", predicateWord.name} );
			}
		}

		return relations;
	}

	/* (s,p,o)の三つ組を受け取り、否定のオントロジーにして返す */
	private List<String[]> makeObjectiveProperty(String[] spo, boolean not) {
		String s = spo[0], p = spo[1], o = spo[2];
		List<String[]> spoList = new LinkedList<String[]>();

		spoList.add( new String[]{p, "rdf:type", "rdfs:ObjectProprety"} );
		spoList.add( new String[]{p, "rdfs:domain", s} );
		if(o==null) {	// 目的語なし
			o = "_:" + p+id + "-object";
		}else {			// 目的語あり
			spoList.add( new String[]{p, "rdfs:range", o} );
		}
		if(!not) {	// 原形
			spoList.add( new String[]{s,p,o} );
		}else {		// 否定形
			spoList.addAll(makeNegation(spo));
		}
		return spoList;
	}
	/* (s,p,o)の否定のオントロジーを返す */
	private List<String[]> makeNegation(String[] spo) {
		String s = spo[0], p = spo[1], o = spo[2];
		List<String[]> spoList = new LinkedList<String[]>();
		String blank = "_:" + p+id + "-not";	// 空白ノードの名前を決める
		spoList.add( new String[]{blank, "rdf:type", "owl:NegativePropertyAssertion"} );
		spoList.add( new String[]{blank, "owl:sourceIndividual", s} );
		spoList.add( new String[]{blank, "owl:assertionProperty", p} );
		if(o!=null)	// 目的語が存在する場合のみ
			spoList.add( new String[]{blank, "owl:targetIndividual", o} );

		return spoList;
	}

	/* ClauseのIDのリストからWordのIDのリストにする */
	public List<Integer> wordIDs() {
		List<Integer> wordIDlist = new ArrayList<Integer>();
		for(int clauseID: clauseIDs) {
			wordIDlist.addAll(Clause.get(clauseID).wordIDs);
		}
		return wordIDlist;
	}

	/* Clauseの係り受け関係を更新 */
	/* 全てのChunkインスタンスのdependUponが正しいことが前提の設計 */
	public void updateDependency() {
		for(final int clauseID: clauseIDs) {
			Clause clause = Clause.get(clauseID);
			clause.beDepended.clear();	// 一度全ての被係り受けをまっさらにする
		}
		for(final int clauseID: clauseIDs) {
			Clause clause = Clause.get(clauseID);
			int depto = clause.dependUpon;
			if(depto != -1) Clause.get(depto).beDepended.add(clause.clauseID);
		}
	}

	@Override
	public String toString() {
		String str = new String();
		for(int clsID: clauseIDs) {
			str +=Clause.get(clsID).toString();
		}
		return str;
	}
	@Override
	public void printDetail() {
		System.out.println(toString());
	}

	public void printW() {
		for(int wdID: wordIDs()) {
			System.out.print("("+wdID+")" + Word.get(wdID).name);
		}
		System.out.println();
	}
	public void printSF() {
		for(int wdID: wordIDs()) {
			boolean sf = Word.get(wdID).isSubject;
			String t_f = sf? "T": "F";
			System.out.print("("+t_f+")" + Word.get(wdID).name);
		}
		System.out.println();
	}
	public void printC() {
		for(int clsID: clauseIDs) {
			System.out.print("("+clsID+")" + Clause.get(clsID).toString());
		}
		System.out.println();
	}
	public void printDep() {
		for(int clsID: clauseIDs) {
			Clause ch = Clause.get(clsID);
			System.out.print("(" + ch.clauseID + ">" + ch.dependUpon + ")" + ch.toString());
		}
		System.out.println();
	}
	/* 文を区切りを挿入して出力する */
	public void printS() {
		for(int wdID: wordIDs()) { // Word単位で区切る
			System.out.print(Word.get(wdID).name + "|");
		}
		System.out.println();
		for(int clsID: clauseIDs) { // Chunk単位で区切る
			System.out.print(Clause.get(clsID).toString() + "|");
		}
		System.out.println();
	}
}
