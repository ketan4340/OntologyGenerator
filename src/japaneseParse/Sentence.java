package japaneseParse;

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

public class Sentence {
	public static int sentSum = 0;
	public int sentID;
	public List<Integer> chunkIDs; // Chunkのリストで文を構成する
	/*
	private List<Integer> commonSubjects;
	public Chunk haChunk;
	public Chunk gaChunk;
	public Chunk niChunk;
	public Chunk woChunk;
	public Chunk deChunk;
	 */	
	public Sentence() {
		sentID = sentSum++;
		chunkIDs = new ArrayList<Integer>();
	}
	public void setSentence(List<Integer> chunkList) {
		chunkIDs = chunkList;
	}
	
	public void setChunksEachParticle() {
		/*
		commonSubjects = new ArrayList<Integer>();
		String[][] tag_Ha = {{"係助詞", "は"}};
		String[][] tag_Ga = {{"格助詞", "が"}};
		String[][] tag_Ni = {{"格助詞", "に"}};
		String[][] tag_Wo = {{"格助詞", "を"}};
		String[][] tag_De = {{"格助詞", "で"}};
		haChunk = Chunk.get(collectTagChunks(tag_Ha).get(0));
		gaChunk = Chunk.get(collectTagChunks(tag_Ga).get(0));
		niChunk = Chunk.get(collectTagChunks(tag_Ni).get(0));
		woChunk = Chunk.get(collectTagChunks(tag_Wo).get(0));
		deChunk = Chunk.get(collectTagChunks(tag_De).get(0));
		 */
	}
	
	public int indexOfC(int chunkID) {
		return chunkIDs.indexOf(chunkID);
	}
	public List<Integer> indexesOfC(List<Integer> chunkIDList) {
		List<Integer> indexList = new ArrayList<Integer>(chunkIDList.size());
		for(final int chunkID: chunkIDList) {
			indexList.add(indexOfC(chunkID));
		}
		return indexList;
	}
	public int indexOfW(int wordID) {
		int indexW = 0;
		for(final int chunkID: chunkIDs) {
			Chunk chunk = Chunk.get(chunkID);
			int order = chunk.wordIDs.indexOf(wordID);
			if(order == -1) {
				indexW += chunk.wordIDs.size();
			}else {
				indexW += order;
				break;
			}
		}
		return indexW;
	}
	public List<Integer> indexesOfW(List<Integer> wordIDList) {
		List<Integer> indexList = new ArrayList<Integer>(wordIDList.size());
		for(int wordID: wordIDList) {
			indexList.add(indexOfW(wordID));
		}
		return indexList;
	}
	
	public Sentence subSentence(int fromIndex, int toIndex) {
		Sentence subSent = new Sentence();
		List<Integer> subIDs = new ArrayList<Integer>(chunkIDs.subList(fromIndex, toIndex));
		subSent.setSentence(subIDs);
		return subSent;
	}

	public List<Integer> collectWords(String name) {
		List<Integer> words = new ArrayList<Integer>();
		for(final int chk: chunkIDs) {
			Chunk ch = Chunk.get(chk);
			words.addAll(ch.collectWords(name));	// 各Chunk内を探す
		}
		return words;	
	}
	/* 渡された品詞に一致するWordのIDを返す */
	public List<Integer> collectTagWords(String[][] tagNames) {
		List<Integer> taggedWords = new ArrayList<Integer>();
		for(final int chk: chunkIDs) {
			Chunk ch = Chunk.get(chk);
			taggedWords.addAll(ch.collectAllTagWords(tagNames));	// 各Chunk内を探す
		}
		return taggedWords;
	}
	/* 渡された品詞に一致するWordを含むChunkのIDを返す */
	public List<Integer> collectTagChunks(String[][] tagNames) {
		List<Integer> taggedChunks = new ArrayList<Integer>();
		for(final int chunkID: chunkIDs) {
			Chunk chunk = Chunk.get(chunkID);
			if(chunk.haveAllTagWord(tagNames))	taggedChunks.add(chunkID);	// 各Chunk内を探す
		}
		return taggedChunks;
	}
	
	public int uniteChunks(List<Integer> connectChunkList, int baseIndex) {
		int baseChunkID = connectChunkList.get(baseIndex);	// tagや係り受けはこのChunkに依存
		Chunk baseChunk = Chunk.get(baseChunkID);			// 全てこのChunkに収める
		List<Integer> newChunkIDs = chunkIDs;		// Chunkを組み替えた新たなchunkIDsをここに
		printDep();
		
		// 渡されたChunkがSentence上で連続しているか
		Map<Integer, Boolean> continuity = getContinuity(connectChunkList);
		continuity.remove(continuity.size()-1);	// 最後は必ずfalseなので抜いておく
		for(final Map.Entry<Integer, Boolean> entry: continuity.entrySet()) {
			if(entry.getValue() == false) {
				System.out.println("error: Not serial in sentence.");
				return baseChunkID;
			}
		}
		System.out.println("connectChunkList:"+connectChunkList + "\tbase:" + baseChunkID);
			
		List<Integer> phraseWords = new ArrayList<Integer>();	// 新しいPhraseの元になるWord
		List<Integer> functionWords = new ArrayList<Integer>();	// Phrase完成後につなげる接続詞を保持
		int depto = baseChunk.dependUpon;						// 最後尾のChunkがどのChunkに係るか
		
		for(Iterator<Integer> itr = connectChunkList.iterator(); itr.hasNext(); ) {
			int connectChunkID = itr.next();
			Chunk connectChunk = Chunk.get(connectChunkID);
			
			if(connectChunkID == baseChunkID) {
				
			}else {
				// 元ChunkのWordはbaseChunkに属するように変える
				for(final int wdID: connectChunk.wordIDs) {
					Word.get(wdID).belongChunk = baseChunkID;
				}
				// 全ての元Chunkの係り先をbaseChunkに変える
				for(final int bedep: connectChunk.beDepended) {
					Chunk.get(bedep).dependUpon = baseChunkID;
				}	
			}
			if(!itr.hasNext()) {		// 最後尾の場合
				phraseWords.add(connectChunk.getMainWord());			// 被修飾語を入れる
				functionWords.addAll(connectChunk.getFunctionWords());	// それに繋がる機能語を入れる
				//depto = connectChunk.dependUpon;
			}else {
				phraseWords.addAll(connectChunk.wordIDs);
			}
		}

		// 新しいPhraseを作成
		Phrase nph = new Phrase();
		nph.setPhrase(phraseWords, baseChunkID, false);
		List<Integer> chunkSource_wd = new ArrayList<Integer>();
		chunkSource_wd.add(nph.wordID);
		chunkSource_wd.addAll(functionWords);
		Chunk.get(baseChunkID).setChunk(chunkSource_wd, depto);
		
		// 古いChunkを削除して新しいChunkを挿入
		connectChunkList.remove(baseIndex);
		newChunkIDs.removeAll(connectChunkList);
		
		updateDependency();
		setSentence(newChunkIDs);
		printDep();
		return baseChunkID;
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
		Set<List<Integer>> connectChunksSet = new HashSet<List<Integer>>(matchingWordsSet.size());
		for(final List<Integer> matchedWords: matchingWordsSet) {
			List<Integer> connectChunks = new ArrayList<Integer>(matchingWords.size());
			for(final int id: matchedWords) {
				Word matchedWord = Word.get(id);
				int belong = matchedWord.belongChunk;
				if(!connectChunks.contains(belong))
					connectChunks.add(belong);	// どのChunkに所属するか
			}
		}
		// 複数のChunkを結合して新しいChunkを作成
		for(final List<Integer> connectChunks: connectChunksSet) {
			uniteChunks(connectChunks, baseIndex);
		}
	}
	
	/* 渡された品詞を繋げる */
	/* Chunkを跨いで連結はしない */
	public void connect(String[][] tagNames) {
		for(final int chID: chunkIDs) {
			Chunk ch = Chunk.get(chID);
			ch.connect(tagNames);
		}
	}
	
	/* 渡された品詞で終わるChunkを次のChunkに繋げる */
	public void connect2Next(String[][] tags) {
		List<Integer> newIDlist = chunkIDs;
		List<Integer> modifyChunkList = new ArrayList<Integer>();
		
		for(final int chID: chunkIDs) {
			Chunk chunk = Chunk.get(chID);
			for(final String[] tag: tags) {
				String[][] tagArray = {tag};
				if(chunk.endWith(tagArray, false)) {	// 指定の品詞で終わるChunkなら(記号考慮)
					modifyChunkList.add(chID);
				}
			}
		}
		//System.out.println("modChunks:"+modifyChunkList);
		List<List<Integer>> phChunksList = makeModificationList(modifyChunkList);
		//System.out.println("phrChunks:"+phChunksList);
		// 複数のChunkを結合して新しいChunkを作成
		for(final List<Integer> phChunks: phChunksList) {
			Chunk nch = new Chunk();
			nch.uniteChunks(phChunks);
			// 古いChunkを削除して新しいChunkを挿入
			newIDlist.add(newIDlist.indexOf(phChunks.get(0)), nch.chunkID);
			newIDlist.removeAll(phChunks);
			updateDependency();
		}
		setSentence(newIDlist);
	}
	
	/* 上記connect2Nextの補助 */
	/* 修飾節のリストから修飾節被修飾節のセットを作る */
	private List<List<Integer>> makeModificationList(List<Integer> modifyChunkList) {
		List<List<Integer>> phChunksList = new ArrayList<List<Integer>>();
		List<Integer> phChunks = new ArrayList<Integer>();
		for(final int modifych: modifyChunkList) {
			if(!phChunks.contains(modifych))
				phChunks.add(modifych);

			int nextIndex = chunkIDs.indexOf(modifych) + 1;	// 修飾節の次の文節が被修飾節
			if(nextIndex == chunkIDs.size()) continue;		// 修飾節が文末なら回避

			int nextChunkID = chunkIDs.get(nextIndex);			// 修飾語の直後に被修飾語があることが前提の設計
			if(modifyChunkList.contains(nextChunkID)) continue;	// 三文節以上連続の可能性を考慮
			
			phChunks.add(nextChunkID);
			phChunksList.add(phChunks);
			phChunks = new ArrayList<Integer>();	
		}
		if(phChunks.size() > 1) phChunksList.add(phChunks);
		return phChunksList;
	}
	
	/* 渡したChunkが文中で連続しているかを<chunkID, Boolean>のMapで返す */
	/* 例:indexのリストが(2,3,4,6,8,9)なら(T,T,F,F,T,F) */
	public Map<Integer, Boolean> getContinuity(List<Integer> chunkIDList) {
		Map<Integer, Boolean> continuity = new LinkedHashMap<Integer, Boolean>(chunkIDList.size());
		List<Integer> chunkIndexList = indexesOfC(chunkIDList);
		
		Iterator<Integer> liID = chunkIDList.listIterator();
		Iterator<Integer> liIdx = chunkIndexList.listIterator();
		int currentIdx = liIdx.next();
		while(liIdx.hasNext() && liID.hasNext()) {
			int nextIdx = liIdx.next();
			int chID = liID.next();
			if(currentIdx+1 == nextIdx) {	// indexが連続しているか
				continuity.put(chID, true);
			}else {							// 否か
				continuity.put(chID, false);
			}
			currentIdx = nextIdx;
		}
		continuity.put(liID.next(), false);	// 最後は絶対連続しないからfalse
		
		return continuity;
	}
	
	/* 複数の述語を持つ文を述語ごと短文に切り分ける */
	public List<Sentence> separate1() {
		List<Sentence> partSentList = new ArrayList<Sentence>(5);
		
		/* 主語を全て探し，それらが連続しているか否かを調べる */
		List<Integer> subjectList;			// 主節のリスト
		Map<Integer, Boolean> sbjContinuityMap;	// 主節の連続性を表す真偽値のマップ
		String[][] tag_Ha = {{"係助詞", "は"}};	// "は"
		String[][] tag_De = {{"格助詞", "で"}};	// "で"
		List<Integer> chunk_Ha_List = new ArrayList<Integer>(collectTagChunks(tag_Ha));	// 係助詞"は"を含むChunk
		List<Integer> chunk_De_List = new ArrayList<Integer>(collectTagChunks(tag_De));	// 格助詞"で"を含むChunk
		chunk_Ha_List.removeAll(chunk_De_List);			// "は"を含むChunkのうち、"で"を含まないものが主語
		subjectList = chunk_Ha_List;
		if(subjectList.isEmpty()) return partSentList;		// 文中に主語がなければ終了
		
		int lastChunkID = chunkIDs.get(chunkIDs.size()-1);	// 文の最後尾Chunk
		Chunk lastChunk = Chunk.get(lastChunkID);
		if(subjectList.contains(lastChunkID)) {	// 文の最後尾が主節の場合
			// おそらく固有名詞を正しく判定できていないせい
			// 最後尾の文節は一つの名詞にする
			lastChunk.nounize(0, lastChunk.wordIDs.size());
			int idx = subjectList.indexOf(lastChunkID);
			subjectList.remove(idx);
		}
		
				
		List<Integer> headSubjectList = new ArrayList<Integer>(subjectList.size());	// 先頭の主語群を集める
		
		sbjContinuityMap = getContinuity(subjectList);	// 主節の連続性を真偽値で表す
		sbjContinuityMap.put(-1, null);		// 最後の要素を表すサイン
				
		Iterator<Map.Entry<Integer, Boolean>> mapItr = sbjContinuityMap.entrySet().iterator();
		Map.Entry<Integer, Boolean> currentSbjEntry = mapItr.next();
		while(mapItr.hasNext()) {
			Map.Entry<Integer, Boolean> nextSbjEntry = mapItr.next();
			int sbjID = currentSbjEntry.getKey();				// 主節のID
			Chunk directSubject = Chunk.get(sbjID);				// 主節のChunk
			boolean sbjContinuity = currentSbjEntry.getValue();	// 主節のあとに別の主節が隣接しているか
			Chunk copySubject = directSubject.copy();
			
			if(sbjContinuity) {	// このChunkの次も主節である場合
				Word no = new Word();	// 助詞・連体化"の"を新たに用意
				no.setWord("の", Arrays.asList("助詞","連体化","*","*","*","*","の","ノ","ノ"), copySubject.chunkID, false);
				int index_Ha = copySubject.indexOfW(copySubject.collectAllTagWords(tag_Ha).get(0));
				copySubject.wordIDs.set(index_Ha, no.wordID);	// "は"の代わりに"の"を挿入
				
				headSubjectList.add(copySubject.chunkID);		// 連続した主節は貯め置きしとく
				
			}else {				// このChunkの次は主節ではない場合
				// 主部をまとめる
				// 新しい主節のインスタンスを用意
				List<Integer> copiedHeadSubjectList = new ArrayList<Integer>(headSubjectList);	// 使い回すので複製
				copiedHeadSubjectList.add(copySubject.chunkID);
				int headChunkID = copiedHeadSubjectList.get(0);
				for(Iterator<Integer> li = copiedHeadSubjectList.listIterator(1); li.hasNext(); ) {
					int nextChunkID = li.next();
					Chunk.get(headChunkID).dependUpon = nextChunkID;	// 複数の主節は隣に係る
					headChunkID = nextChunkID;
				}
				// 連続した主節はこの場で結合する
				Chunk newSbjChunk = new Chunk();
				newSbjChunk.uniteChunks(copiedHeadSubjectList);
			
				// 述部を切り離す
				int fromIndex = indexOfC(directSubject.chunkID)+1;	// 述部切り取りの始点は主節の次
				int toIndex = (nextSbjEntry.getKey() != -1)			// 述部切り取りの終点は
						? indexOfC(nextSbjEntry.getKey())			// 次の主節の位置
						: chunkIDs.size();							// なければ文末
				//System.out.println("\t"+"mainPre(" + fromIndex + "~" + toIndex + ")");
				List<Integer> partPredicates = chunkIDs.subList(fromIndex, toIndex);	// 切り取った述部
				Chunk.get(partPredicates.get(partPredicates.size()-1)).dependUpon = -1;	// 最後尾の述語はどこにも係らない
				
				// 述部の分割
				int nextPredicateID = directSubject.dependUpon;			// 次の述語のID
				//System.out.println("directSbj = " + directSubject.chunkID + ", " + "nextPre = " + nextPredicateID);
				if(nextPredicateID == -1) break;
				int fromPrdIndex = indexOfC(directSubject.chunkID)+1;	// 述部分割の始点
				int toPrdIndex = indexOfC(nextPredicateID)+1;			// 述部分割の終点
				while(nextPredicateID != -1) {
					Chunk nextPredicateChunk = Chunk.get(nextPredicateID);
					//System.out.println("\t\t"+"partPre(" + fromPrdIndex + "~" + toPrdIndex + ")");
					List<Integer> piecePredicates = chunkIDs.subList(fromPrdIndex, toPrdIndex);
					// 主節は新しいインスタンスを用意
					Chunk newSubject_c = newSbjChunk.copy();
					newSubject_c.dependUpon = nextPredicateID;
					
					List<Integer> partChunkList = new ArrayList<Integer>();		// 短文を構成するChunkのリスト
					partChunkList.add(newSubject_c.chunkID);	// 結合主部セット
					partChunkList.addAll(piecePredicates);		// 部分述部セット
					// 短文生成
					Sentence partSent = new Sentence();
					partSent.setSentence(partChunkList);
					partSentList.add(partSent);
					
					// 次の述語を見つけ，fromとtoを更新
					nextPredicateID = nextPredicateChunk.dependUpon;
					fromPrdIndex = toPrdIndex;
					toPrdIndex = indexOfC(nextPredicateID)+1;
				}
			}
		currentSbjEntry = nextSbjEntry;
		}
		
		return partSentList;
	}
	
	/* メインの主語が係る述語ごとに分割 */
	public List<Sentence> separate2() {
		List<Sentence> shortSentList = new ArrayList<Sentence>(5);
		/* 主語を全て探し，それらが連続しているか否かを調べる */
		String[][] tag_Ha = {{"係助詞", "は"}};	// "は"
		String[][] tag_De = {{"格助詞", "で"}};	// "で"
		List<Integer> chunk_Ha_List = new ArrayList<Integer>(collectTagChunks(tag_Ha));	// 係助詞"は"を含むChunk
		List<Integer> chunk_De_List = new ArrayList<Integer>(collectTagChunks(tag_De));	// 格助詞"で"を含むChunk
		chunk_Ha_List.removeAll(chunk_De_List);			// "は"を含むChunkのうち、"で"を含まないものが主語
		List<Integer> subjectList = chunk_Ha_List;		// 主語のリスト
				
		int lastChunkID = chunkIDs.get(chunkIDs.size()-1);	// 文の最後尾Chunk
		Chunk lastChunk = Chunk.get(lastChunkID);
		if(subjectList.contains(lastChunkID)) {	// 文の最後尾が主節の場合
			// おそらく固有名詞を正しく判定できていないせい
			// 最後尾の文節は一つの名詞にする
			lastChunk.nounize(0, lastChunk.wordIDs.size());
			subjectList.remove(subjectList.indexOf(lastChunkID));
		}
		if(subjectList.isEmpty()) return shortSentList;	// 文中に主語がなければ終了

		// 主節の連続性を表す真偽値のリスト
		Map<Integer, Boolean> subjectsContinuity = getContinuity(subjectList);
		// 文頭に連続で並ぶ主語は文全体に係るとみなし、集めて使い回す
		List<Integer> commonSubjectsOrigin = new ArrayList<Integer>(subjectList.size());
		int mainSubjectID = -1;	Chunk mainSubject;
		for(Map.Entry<Integer, Boolean> entry: subjectsContinuity.entrySet()) {
			commonSubjectsOrigin.add(entry.getKey());
			if(!entry.getValue()) {	// 主語の連続が途切れたら
				mainSubjectID = entry.getKey();	// 後続の多くの述語に係る、核たる主語
				break;				// 核主語集め完了
			}
		}
		mainSubject = Chunk.get(mainSubjectID);
		List<Integer> predicateIDs = mainSubject.getAllDepending();
		predicateIDs.retainAll(chunkIDs);
		
		/* 文章分割(dependUpon依存) */
		if(predicateIDs.size() > 1) {	// 述語が一つならスルー
			int fromIndex = 0, toIndex;
			for(final int predicateID: predicateIDs) {
				Chunk.get(predicateID).dependUpon = -1;	// 文末の述語となるので係り先はなし(-1)
				toIndex = indexOfC(predicateID) + 1;	// 述語も含めて切り取るため+1
				Sentence subSent = subSentence(fromIndex, toIndex);
				// 文頭の主語は全ての分割後の文に係る
				List<Integer> commonSubjects = (fromIndex!=0)
						? Chunk.copy(commonSubjectsOrigin)	// 後続の分割文なら複製した主語群
						: commonSubjectsOrigin;				// 最初の分割文なら元の主語群
				if(fromIndex!=0)	// 最初の分割文は、新たに主語を挿入する必要ない
					subSent.chunkIDs.addAll(0, commonSubjects);	// 共通の主語を挿入
	
				for(Iterator<Integer> itr = commonSubjects.iterator(); itr.hasNext(); ) {
					int commonSbjID = itr.next();
					Chunk commonSubject = Chunk.get(commonSbjID);
					commonSubject.dependUpon = predicateID;	// 係り先を正す
				}
				subSent.updateDependency();
				shortSentList.add(subSent);
				fromIndex = toIndex;
			}
		}else {
			shortSentList.add(this);
		}
		return shortSentList;
	}
	
	/* 述語に係る{動詞,形容詞,名詞,~だ,接続助詞}ごとに分割 */
	public List<Sentence> separate3() {
		List<Sentence> partSentList = new ArrayList<Sentence>(5);
		/* 主語を全て探し，それらが連続しているか否かを調べる */
		String[][] tag_Ha = {{"係助詞", "は"}};	// "は"
		String[][] tag_De = {{"格助詞", "で"}};	// "で"
		List<Integer> chunk_Ha_List = new ArrayList<Integer>(collectTagChunks(tag_Ha));	// 係助詞"は"を含むChunk
		List<Integer> chunk_De_List = new ArrayList<Integer>(collectTagChunks(tag_De));	// 格助詞"で"を含むChunk
		chunk_Ha_List.removeAll(chunk_De_List);			// "は"を含むChunkのうち、"で"を含まないものが主語
		List<Integer> subjectList = chunk_Ha_List;		// 主語のリスト
		if(subjectList.isEmpty()) return partSentList;	// 文中に主語がなければ終了
		
		int lastChunkID = chunkIDs.get(chunkIDs.size()-1);	// 文の最後尾Chunk
		Chunk lastChunk = Chunk.get(lastChunkID);
		if(subjectList.contains(lastChunkID)) {	// 文の最後尾が主節の場合
			// おそらく固有名詞を正しく判定できていないせい
			// 最後尾の文節は一つの名詞にする
			lastChunk.nounize(0, lastChunk.wordIDs.size());
			subjectList.remove(subjectList.indexOf(lastChunkID));
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
		String[][] tagParticle = {{"助詞", "-て", "-で"}};	// "て"及び"で"以外の助詞
		String[][] tagAdverb = {{"副詞"}};
		List<Integer> predicateIDs = new ArrayList<Integer>();
		for(final int toLast: lastChunk.beDepended) {
			Chunk chunk2Last = Chunk.get(toLast);
			if( !chunk2Last.endWith(tagParticle, true) && !chunk2Last.endWith(tagAdverb, true) )	// 助詞"て","で"またはそれ以外の品詞が
				predicateIDs.add(toLast);				// 末尾に来るChunkを追加
		}
		predicateIDs.add(lastChunkID);
		predicateIDs.retainAll(chunkIDs);

		String[][] tagConj = {{"接続助詞", "て"}};
		List<Integer> commonObjects = new ArrayList<Integer>();	// 複数の述語にかかる目的語を保管
		Map<Integer, Boolean> prdContinuity = getContinuity(predicateIDs);
		//printDep();
		//System.out.println("predicates:" + predicateIDs);
		if(predicateIDs.size() > 1) {	// 述語が一つならスルー
			int fromIndex = 0, toIndex;
			for(final Map.Entry<Integer, Boolean> entry: prdContinuity.entrySet()) {
				int predicateID = entry.getKey();
				boolean adjoin = entry.getValue();
				Chunk predicate = Chunk.get(predicateID);
				predicate.dependUpon = -1;	// 分割後、当該述語は文末にくるので係り先はなし(-1)
				toIndex = indexOfC(predicateID) + 1;	// 述語も含めて切り取るため+1
				Sentence subSent = subSentence(fromIndex, toIndex);
				// 共通の目的語を挿入(あれば)
				if(!commonObjects.isEmpty()) {
					List<Integer> commonObjectsReplica = Chunk.copy(commonObjects); 
					for(final int obj: commonObjectsReplica) Chunk.get(obj).dependUpon = predicateID;
					subSent.chunkIDs.addAll(0, commonObjectsReplica);	// 直前の述語と共通する目的語を挿入
					commonObjects.clear();;
				}
				// 文頭の主語は全ての分割後の文に係る
				List<Integer> commonSubjects = (fromIndex!=0)
						? Chunk.copy(commonSubjectsOrigin)	// 後続の分割文なら複製した主語群
						: commonSubjectsOrigin;				// 最初の分割文なら元の主語群
				if(fromIndex!=0)	// 最初の分割文は、新たに主語を挿入する必要ない
					subSent.chunkIDs.addAll(0, commonSubjects);	// 共通の主語を挿入
	
				// 主語の係り先を正す
				for(final int sbj: commonSubjects) Chunk.get(sbj).dependUpon = predicateID;
				// 係り元の更新
				subSent.updateDependency();
				partSentList.add(subSent);
				// 直後に述語が控え、かつこの述語が接続助詞"て"で終わる場合
				// 目的語を次の述語にも係るように持ち越す
				if(adjoin && predicate.endWith(tagConj, true)) {
					int objFrom = subSent.indexOfC(commonSubjects.get(commonSubjects.size()-1)) + 1;// 主語の次から
					int objTo = subSent.indexOfC(predicateID);										// 述語の手前まで
					commonObjects.addAll(subSent.chunkIDs.subList(objFrom, objTo));
				}
				fromIndex = toIndex;
			}
		}else {
			partSentList.add(this);
		}
		return partSentList;
	}
	
	/* 文章から関係を見つけtripleにする */
	public List<List<String>> extractRelation() {
		List<List<String>> relations = new ArrayList<List<String>>();
		/* 主語を探す */
		String[][] tag_Ha = {{"係助詞", "は"}};	// 助詞"は"を含むChunkを探す
		List<Integer> subjectChunkList = collectTagChunks(tag_Ha);
		if(subjectChunkList.isEmpty()) return relations;
				
		Chunk subjectChunk = Chunk.get(subjectChunkList.get(0));		// 主節("は"を含む)
		Word subjectWord = Word.get(subjectChunk.getMainWord());		// 主語
		String subject = subjectWord.wordName;
		Chunk predicateChunk = Chunk.get(subjectChunk.dependUpon);		// 述節
		Word predicateWord = Word.get(predicateChunk.getMainWord());	// 述語
		//Chunk complementChunk;										// 補節(いつか使うかも)
		//Word complementWord;											// 補語
		String predicatePart = subSentence(chunkIDs.indexOf(subjectChunk.chunkID)+1, chunkIDs.size()).toString();	// 述部(主節に続く全ての節)
		String[][] tag_Not = {{"助動詞", "ない"}};
		boolean not = (predicateChunk.haveAllTagWord(tag_Not))? true: false;	// 述語が否定かどうか
		
		
		printDep();
		/*
		System.out.println("prePart: " + predicatePart + "________\t" + "preChunk: " + predicateChunk.toString());
		System.out.println("[" + subjectChunk.toString() + "][" + predicateChunk.toString() + "]");
		*/
		
		/* 述語が[<名詞>である。]なのか[<動詞>する。]なのか[<形容詞>。]なのか */
		String[][] tagVerb = {{"動詞"}, {"サ変接続"}};
		String[][] tagAdjective = {{"形容詞"}};
		
		/* 述語が動詞 */
		if( predicateChunk.haveAllTagWord(tagVerb) ) {
			/* "がある"かどうか */
			String[][] tag_Have = {{"ある"}, {"もつ"}};		// 動詞の"ある"(助動詞ではない)
			boolean boolHave = predicateChunk.haveAllTagWord(tag_Have);
			/* "~の総称" */
			String regexGnrnm = "(.*?)(の総称)";				// 「〜の総称」を探す
			Pattern ptrnGnrnm = Pattern.compile(regexGnrnm);
			Matcher mtchGnrnm = ptrnGnrnm.matcher(predicateChunk.toString());
			boolean boolGnrnm = mtchGnrnm.matches();
						
			if(boolHave) {			// "~がある","~をもつ"
				int idxprd = indexOfC(predicateChunk.chunkID);
				Chunk previousChunk = Chunk.get(chunkIDs.get(idxprd-1));		// 動詞の一つ前の文節
				String part = Word.get(previousChunk.getMainWord()).wordName;	// その主辞の文字列
				String[][] tag_Ga_Wo = {{"格助詞", "が"}, {"格助詞", "を"}};
				if(previousChunk.haveAllTagWord(tag_Ga_Wo)) {
					relations.add( Arrays.asList(part, "dcterms:isPartOf", subject) );
					relations.add( Arrays.asList(subject, "dcterms:hasPart", part) );
				}
				
			}else if(boolGnrnm) {	// "~の総称"
				relations.add( Arrays.asList(subjectWord.wordName, "owl:equivalentClass", mtchGnrnm.group(1)) );				
		
			}else {					// その他の動詞
				String verb = predicateWord.tags.get(6);	// 原形を取り出すためのget(6)
				String object = null;
					
				// 格助詞"に","を","へ"などを元に目的語を探す
				String[][] tag_Ni_Wo = {{"格助詞", "に"}, {"格助詞", "を"}};	// 目的語oと述語pを結ぶ助詞
				List<Integer> chunks_Ni_Wo = collectTagChunks(tag_Ni_Wo);
				if(!chunks_Ni_Wo.isEmpty()) {	// 目的語あり
					Chunk chunk_Ni_Wo = Chunk.get(chunks_Ni_Wo.get(0));
					Word word_Ni_Wo = Word.get(chunk_Ni_Wo.getMainWord());	// "に"または"を"の主辞
					object = word_Ni_Wo.wordName;
				}else {							// 目的語なし
					object = null;
				}
				String[] spo = {subject, verb, object};
				relations.addAll(makeObjectiveProperty(spo, not));
			}
			
			
		/* 述語が形容詞 */
		}else if(predicateChunk.haveAllTagWord(tagAdjective)) {
			
			
		/* 述語が名詞または助動詞 */
		}else {
			// 述語が動詞でない-> (親クラス, 子クラス)を記述
			/* リテラル情報かどうか */
			String regexLiteral = "(.*?)(\\d+(\\.\\d+)?)([ア-ンa-zA-Zー－]+)(.*?)";	// 「~(数字)(単位)~」を探す
			Pattern ptrnLiteral = Pattern.compile(regexLiteral);
			Matcher mtchLiteral = ptrnLiteral.matcher(predicateChunk.toString());
			boolean boolLiteral = mtchLiteral.matches();
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
			
			if(boolLiteral) {
				String blank = "_:"+subjectWord.wordName+"-size";
				relations.add( Arrays.asList(subjectWord.wordName, "ex:size", blank) );			// 空白ノード
				relations.add( Arrays.asList(blank, "rdf:value", mtchLiteral.group(2)) );		// リテラル
				relations.add( Arrays.asList(blank, "exterms:units", mtchLiteral.group(4)) );	// 単位
			}else if(boolSynonym) {
				relations.add( Arrays.asList(subjectWord.wordName, "owl:sameClassAs", mtchSynonym.group(1)) );
			}else if(boolKind) {
				relations.add( Arrays.asList(subjectWord.wordName, "rdf:type", mtchKind.group(1)) );
			}else {
				relations.add( Arrays.asList(subjectWord.wordName, "rdfs:subClassOf", predicateWord.wordName) );
			}
		}
		
		return relations;
	}
	
	/* (s,p,o)の三つ組を受け取り、否定のオントロジーにして返す */
	private List<List<String>> makeObjectiveProperty(String[] spo, boolean not) {
		String s = spo[0], p = spo[1], o = spo[2];
		List<List<String>> spoList = new LinkedList<List<String>>();
				
		spoList.add( Arrays.asList(p, "rdf:type", "rdfs:ObjectProprety") );
		spoList.add( Arrays.asList(p, "rdfs:domain", s) );
		if(o==null) {	// 目的語なし
			o = "_:" + p+sentID + "-object";
		}else {			// 目的語あり
			spoList.add( Arrays.asList(p, "rdfs:range", o) );
		}
		if(!not) {	// 原形
			spoList.add(Arrays.asList(s,p,o));
		}else {		// 否定形
			spoList.addAll(makeNegation(spo));
		}
		return spoList;
	}
	/* (s,p,o)の否定のオントロジーを返す */
	private List<List<String>> makeNegation(String[] spo) {
		String s = spo[0], p = spo[1], o = spo[2];
		List<List<String>> spoList = new LinkedList<List<String>>();
		String blank = "_:" + p+sentID + "-not";	// 空白ノードの名前を決める
		spoList.add(Arrays.asList(blank, "rdf:type", "owl:NegativePropertyAssertion"));
		spoList.add(Arrays.asList(blank, "owl:sourceIndividual", s));
		spoList.add(Arrays.asList(blank, "owl:assertionProperty", p));
		if(o!=null)	// 目的語が存在する場合のみ
			spoList.add(Arrays.asList(blank, "owl:targetIndividual", o));
		
		return spoList;
	}
	
	/* ChunkのIDのリストからWordのIDのリストにする */
	public List<Integer> wordIDs() {
		List<Integer> wordIDlist = new ArrayList<Integer>();
		for(int chunkID: chunkIDs) {
			wordIDlist.addAll(Chunk.get(chunkID).wordIDs);
		}
		return wordIDlist;
	}
	
	/* chunkの係り受け関係を更新 */
	/* 全てのChunkインスタンスのdependUponが正しいことが前提の設計 */
	public void updateDependency() {
		for(final int chunkID: chunkIDs) {
			Chunk chunk = Chunk.get(chunkID);
			chunk.beDepended.clear();	// 一度全ての被係り受けをまっさらにする
		}
		for(final int chunkID: chunkIDs) {
			Chunk chunk = Chunk.get(chunkID);
			int depto = chunk.dependUpon;
			if(depto != -1) Chunk.get(depto).beDepended.add(chunk.chunkID);
		}
	}
	
	public String toString() {
		String str = new String();
		for(int cid: chunkIDs) {
			str +=Chunk.get(cid).toString();
		}
		return str;
	}
	public void printW() {
		for(int wid: wordIDs()) {
			System.out.print("("+wid+")" + Word.get(wid).wordName);
		}
		System.out.println();
	}
	public void printSF() {
		for(int wid: wordIDs()) {
			boolean sf = Word.get(wid).sbj_fnc;
			String t_f = sf? "T": "F";
			System.out.print("("+t_f+")" + Word.get(wid).wordName);
		}
		System.out.println();
	}
	public void printC() {
		for(int cid: chunkIDs) {
			System.out.print("("+cid+")" + Chunk.get(cid).toString());
		}
		System.out.println();
	}
	public void printDep() {
		for(int id: chunkIDs) {
			Chunk ch = Chunk.get(id);
			System.out.print("(" + ch.chunkID + ">" + ch.dependUpon + ")" + ch.toString());
		}
		System.out.println();
	}
	/* 文を区切りを挿入して出力する */
	public void printS() {
		for(int wid: wordIDs()) { // Word単位で区切る
			System.out.print(Word.get(wid).wordName + "|");
		}
		System.out.println();
		for(int cid: chunkIDs) { // Chunk単位で区切る
			System.out.print(Chunk.get(cid).toString() + "|");
		}
		System.out.println();
	}
}
