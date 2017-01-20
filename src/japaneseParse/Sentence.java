package japaneseParse;

import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class Sentence {
	public List<Integer> chunkIDs; // Chunkのリストで文を構成する
	public Chunk haChunk;
	public Chunk gaChunk;
	public Chunk niChunk;
	public Chunk woChunk;
	public Chunk deChunk;
			
	public Sentence() {
		chunkIDs = new ArrayList<Integer>();
	}
	public void setSentence(List<Integer> chunkList) {
		chunkIDs = chunkList;
	}
	
	public void setChunksEachParticle() {
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
	}
	
	public int indexOfC(int chunkID) {
		return chunkIDs.indexOf(chunkID);
	}
	public List<Integer> indexesOfC(List<Integer> chunkIDList) {
		List<Integer> indexList = new ArrayList<Integer>(chunkIDList.size());
		for(int chunkID: chunkIDList) {
			indexList.add(indexOfC(chunkID));
		}
		return indexList;
	}
	public int indexOfW(int wordID) {
		int indexW = 0;
		for(int chunkID: chunkIDs) {
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
		
	/* 渡された品詞の連続にマッチする単語を連結する */
	public void connectPattern(String[][] pattern, int baseIndex) {
		if(baseIndex >= pattern.length) System.out.println("index error");
		List<Integer> newChunkIDs = chunkIDs;
		
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
		
		// Wordの列からChunkの列に直す
		Set<List<Integer>> connectChunksSet = new HashSet<List<Integer>>(matchingWordsSet.size());
		for(final List<Integer> matchedWords: matchingWordsSet) {
			List<Integer> connectChunks = new ArrayList<Integer>(matchingWords.size());
			for(final int id: matchedWords) {
				Word matchedWord = Word.get(id);
				if(connectChunks.contains(matchedWord.inChunk))
					connectChunks.add(matchedWord.inChunk);	// どのChunkに所属するか
			}
		}
		// 複数のChunkを結合して新しいChunkを作成
		for(final List<Integer> connectChunks: connectChunksSet) {
			Chunk nch = new Chunk();
			nch.uniteChunks(connectChunks);
			// 古いChunkを削除して新しいChunkを挿入
			newChunkIDs.add(newChunkIDs.indexOf(connectChunks.get(0)), nch.chunkID);
			newChunkIDs.removeAll(connectChunks);
			//Chunk.updateAllDependency();
			updateDependency();
		}
		setSentence(newChunkIDs);
		
	}
	
	/* 連続した名詞を繋いで一つの名詞にする */
	/* Chunkを跨いで連結はしない */
	public void connectNouns() {
		for(final int chID: chunkIDs) {
			Chunk ch = Chunk.get(chID);
			String[][] tagNames = {{"名詞"}, {"名詞接続"}, {"接尾"}, {"形容詞"}};
			ch.connect(tagNames);
		}
	}
	/* 連続した名詞を繋いで一つの名詞にする */
	/* Chunkを跨いで連結はしない */
	public void connectVerbs() {
		for(final int chID: chunkIDs) {
			Chunk ch = Chunk.get(chID);
			String[][] tagNames = {{"名詞"}, {"動詞", "する"}};
			ch.connect(tagNames);
		}
	}
	/* 渡された修飾語のWordを被修飾語につなげ、新しいPhraseを作る */
	/* 結合元のWordをPhraseに置き換えたSentenceを返す */
	/* 現状のChunk依存の結合方法からWord結合に治すべき*要改善* */
	public void connectModifer(List<Integer> modifyChunkList) {
		List<Integer> newIDlist = chunkIDs;
		List<List<Integer>> phChunksList = makeModificationList(modifyChunkList);
		
		// 複数のChunkを結合して新しいChunkを作成
		for(final List<Integer> phChunks: phChunksList) {
			Chunk nch = new Chunk();
			nch.uniteChunks(phChunks);
			// 古いChunkを削除して新しいChunkを挿入
			newIDlist.add(newIDlist.indexOf(phChunks.get(0)), nch.chunkID);
			newIDlist.removeAll(phChunks);
			//Chunk.updateAllDependency();
			updateDependency();
		}
		setSentence(newIDlist);
	}
	
	/* 上記concatenateの補助 */
	/* 修飾節のリストから修飾節被修飾節のセットを作る */
	private List<List<Integer>> makeModificationList(List<Integer> modifyChunkList) {
		List<List<Integer>> phChunksList = new ArrayList<List<Integer>>();
		List<Integer> phChunks = new ArrayList<Integer>();
		for(final int modifych: modifyChunkList) {
			int nextIndex = chunkIDs.indexOf(modifych) + 1;	// 修飾節の次の文節が被修飾節だろうという前提
			if(nextIndex != chunkIDs.size()) {	// 修飾節が文末なら回避
				int nextch = chunkIDs.get(nextIndex);			// 修飾語の直後に被修飾語があることが前提の設計
				phChunks.add(modifych);
				if( !modifyChunkList.contains(nextch) ) {	// 三文節以上連続の可能性を考慮
					phChunks.add(nextch);
					phChunksList.add(phChunks);
					phChunks = new ArrayList<Integer>();
				}
			}
		}
		return phChunksList;
	}
	
	/* 渡したChunkが文中で連続しているかをBooleanリストで返す */
	/* 例:(2,3,4,6,8,9)なら(T,T,F,F,T,F) */
	public List<Boolean> getContinuity(List<Integer> chunkIDList) {
		List<Boolean> continuity = new ArrayList<Boolean>(chunkIDList.size());
		List<Integer> chunkIndexList = indexesOfC(chunkIDList);
		int chIdx = chunkIndexList.remove(0);
		int nextIdx = 0;
		for(Iterator<Integer> li = chunkIndexList.listIterator(); li.hasNext(); ) {
			nextIdx = li.next();
			if(chIdx+1 == nextIdx) {	// indexが連続しているか
				continuity.add(true);
			}else {						// 否か
				continuity.add(false);
			}
			chIdx = nextIdx;
		}
		continuity.add(false);		// 最後はどうせ連続しないからfalse
		
		return continuity;
	}
	
	public List<Sentence> separate2() {
		List<Sentence> shortSentList = new ArrayList<Sentence>(5);
		/* 主語を全て探し，それらが連続しているか否かを調べる */
		List<Integer> subjectList;			// 主節のリスト
		List<Boolean> sbjContinuityList;	// 主節の連続性を表す真偽値のリスト
		String[][] tag_Ha = {{"係助詞", "は"}};	// "は"
		String[][] tag_De = {{"格助詞", "で"}};	// "で"
		List<Integer> chunk_Ha_List = new ArrayList<Integer>(collectTagChunks(tag_Ha));	// 係助詞"は"を含むChunk
		List<Integer> chunk_De_List = new ArrayList<Integer>(collectTagChunks(tag_De));	// 格助詞"で"を含むChunk
		chunk_Ha_List.removeAll(chunk_De_List);			// "は"を含むChunkのうち、"で"を含まないものが主語
		subjectList = chunk_Ha_List;
		if(subjectList.isEmpty()) return shortSentList;		// 文中に主語がなければ終了
		
		int lastChunkID = chunkIDs.get(chunkIDs.size()-1);	// 文の最後尾Chunk
		Chunk lastChunk = Chunk.get(lastChunkID);
		if(subjectList.contains(lastChunkID)) {	// 文の最後尾が主節の場合
			// おそらく固有名詞を正しく判定できていないせい
			// 最後尾の文節は一つの名詞にする
			lastChunk.nounize(0, lastChunk.wordIDs.size());
			int idx = subjectList.indexOf(lastChunkID);
			subjectList.remove(idx);
		}

		Chunk mainSubject = Chunk.get(subjectList.get(0));
		int sum = 0;
		for(int subjectID: subjectList) {
			Chunk ch = Chunk.get(subjectID);
			int nsum = ch.getAllDepending().size();
			if(nsum > sum) {
				sum = nsum;
				mainSubject = ch;
			}
		}
		List<Integer> predicateList = new ArrayList<Integer>(mainSubject.getAllDepending());
		printC();
		System.out.println("subjects:" + subjectList + "\tpredicates:" + predicateList);
		for(int prd: predicateList) {
			System.out.println(prd + " :" + Chunk.get(prd).beDepended);
		}
		
		
		return shortSentList;
	}
	
	/* 複数の述語を持つ文を述語ごと短文に切り分ける */
	public List<Sentence> separate() {
		List<Sentence> partSentList = new ArrayList<Sentence>(5);
		
		/* 主語を全て探し，それらが連続しているか否かを調べる */
		List<Integer> subjectList;			// 主節のリスト
		List<Boolean> sbjContinuityList;	// 主節の連続性を表す真偽値のリスト
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
		
		sbjContinuityList = getContinuity(subjectList);		// 主節の連続性を真偽値で表す
		
		List<Integer> headSubjectList = new ArrayList<Integer>(subjectList.size());
		for(int i=0; i < subjectList.size(); i++) {
			int sbjID = subjectList.get(i);					// 主節のID
			Chunk directSubject = Chunk.get(sbjID);			// 主節のChunk
			boolean sbjContinuity = sbjContinuityList.get(i);		// 主節のあとに別の主節が隣接しているか
			Chunk copySubject = directSubject.copy();
			
			if(sbjContinuity) {	// このChunkの次も主節である場合
				/*
				Word no = new Word();	// 助詞・連体化"の"を新たに用意
				no.setWord("の", Arrays.asList("助詞","連体化","*","*","*","*","の","ノ","ノ"), copySubject.chunkID, false);
				int index_Ha = copySubject.indexOfW(copySubject.collectTagWords(tag_Ha).get(0));
				copySubject.wordIDs.set(index_Ha, no.wordID);	// "は"の代わりに"の"を挿入
				*/
				headSubjectList.add(copySubject.chunkID);		// 連続した主節は貯め置きしとく
				
			}else {				// このChunkの次は主節ではない場合
				// 主部をまとめる
				// 新しい主節のインスタンスを用意
				//headSubjectList.add(copySubject.chunkID);
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
				int fromIndex = indexOfC(directSubject.chunkID)+1;		// 述部切り取りの始点は主節の次
				int toIndex = (i+1<sbjContinuityList.size())			// 述部切り取りの終点は
						? indexOfC(subjectList.get(i+1))				// 次の主節の位置
						: chunkIDs.size();								// なければ文末
				//System.out.println("\t"+"mainPre(" + fromIndex + "~" + toIndex + ")");
				List<Integer> partPredicates = chunkIDs.subList(fromIndex, toIndex);	// 切り取った述部
				Chunk partEndChunk = Chunk.get(partPredicates.get(partPredicates.size()-1));
				partEndChunk.dependUpon = -1;	// 最後尾の述語はどこにも係らない
				
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
		}
		
		return partSentList;
	}
	
	
	
	/* 文章から関係を見つけtripleにする */
	public List<List<String>> extractRelation() {
		List<List<String>> relations = new ArrayList<List<String>>();
		/* 主語を探す */
		String[][] tagSP = {{"係助詞", "は"}};	// 主語と述語を結ぶ助詞"は"を探す
		List<Integer> ptcls_SP = collectTagWords(tagSP);
		if(ptcls_SP.isEmpty()) return relations;
		int ptcl_SP = ptcls_SP.get(0);			// 文中に1つしかないと仮定しているのでget(0) *要注意*
		
		Chunk subjectChunk = Chunk.get(Word.get(ptcl_SP).inChunk);		// 主節("は"を含む)
		Word subjectWord = Word.get(subjectChunk.wordIDs.get(0));		// 主語
		Chunk predicateChunk = Chunk.get(subjectChunk.dependUpon);		// 述節
		Word predicateWord = Word.get(predicateChunk.wordIDs.get(0));	// 述語
		//Chunk complementChunk;										// 補節(いつか使うかも)
		//Word complementWord;											// 補語
		String predicatePart = subSentence(chunkIDs.indexOf(subjectChunk.chunkID)+1, chunkIDs.size()).toString();	// 述部(主節に続く全ての節)
		/*
		printDep();
		System.out.println("prePart: " + predicatePart + "________\t" + "preChunk: " + predicateChunk.toString());
		System.out.println("[" + subjectChunk.toString() + "][" + predicateChunk.toString() + "]");
		*/
		/* 述語が[<名詞>である。]なのか[<動詞>する。]なのか[<形容詞>。]なのか */
		String[][] tagVerb = {{"動詞"}, {"サ変接続"}};
		String[][] tagAdjective = {{"形容詞"}};
		
		
		/* 述語が動詞 */
		if( predicateChunk.haveAllTagWord(tagVerb) ) {
			String[][] tag_Not = {{"助動詞", "ない"}};
			
			int idxprd = indexOfC(predicateChunk.chunkID);
			Chunk previousChunk = Chunk.get(chunkIDs.get(idxprd-1));	// 動詞の一つ前の文節
			Word part = previousChunk.getMainWord();					// その主辞

			/* "がある"かどうか */
			String[][] tag_Have = {{"ある"}, {"もつ"}};		// 動詞の"ある"(助動詞ではない)
			boolean boolHave = predicateChunk.haveAllTagWord(tag_Have);
			String[][] tag_Do = {{"する"}};		// 動詞の"する"
			boolean boolDo = predicateChunk.haveAllTagWord(tag_Do);
			/* "~の総称" */
			String regexGnrnm = "(.*?)(の総称)";					// 「〜の総称」を探す
			Pattern ptrnGnrnm = Pattern.compile(regexGnrnm);
			Matcher mtchGnrnm = ptrnGnrnm.matcher(predicateChunk.toString());
			boolean boolGnrnm = mtchGnrnm.matches();
						
			if(boolHave) {			// "~がある","~をもつ"
				String[][] tag_Ga_Wo = {{"格助詞", "が"}, {"格助詞", "を"}};
				if(previousChunk.haveAllTagWord(tag_Ga_Wo)) {
					relations.add( Arrays.asList(part.wordName, "dcterms:isPartOf", subjectWord.wordName) );
					relations.add( Arrays.asList(subjectWord.wordName, "dcterms:hasPart", part.wordName) );
				}
			}else if(boolGnrnm) {	// "~の総称"
				relations.add( Arrays.asList(subjectWord.wordName, "owl:equivalentClass", mtchGnrnm.group(1)) );				
			}else {					// その他の動詞
				String predicate = predicateWord.tags.get(6);	// 原形を取り出すためのget(6)
				relations.add( Arrays.asList(predicate, "rdf:type", "rdfs:Proprety") );
								
				// 格助詞"で","に","を","へ"などを元に目的語を探す
				String[][] tag_Ni = {{"格助詞", "に"}};	// 目的語oと述語pを結ぶ助詞
				List<Integer> ptcls_ni = collectTagWords(tag_Ni);
				String[][] tag_Wo = {{"格助詞", "を"}};	// 目的語oと述語pを結ぶ助詞
				List<Integer> ptcls_wo = collectTagWords(tag_Wo);
				
				/* 目的語の有無 */
				if(!ptcls_ni.isEmpty()) {		// 目的語"に"あり
					int ptcl_ni = ptcls_ni.get(0);	// 文中に1つしかないと仮定しているのでget(0) 要改善
					Chunk niChunk = Chunk.get(Word.get(ptcl_ni).inChunk);
					Word niWord = Word.get(niChunk.wordIDs.get(0));		// 目的語"に"
					// (subject, property, object)を記述
					relations.add( Arrays.asList(predicate, "rdfs:domain", subjectWord.wordName) );
					relations.add( Arrays.asList(predicate, "rdfs:range", niWord.wordName) );	
					relations.add( Arrays.asList(subjectWord.wordName, predicateWord.tags.get(6), niWord.wordName) );
				
				}else if(!ptcls_wo.isEmpty()) {	// 目的語"を"あり
					int ptcl_wo = ptcls_wo.get(0);	// 文中に1つしかないと仮定しているのでget(0) 要改善
					Chunk woChunk = Chunk.get(Word.get(ptcl_wo).inChunk);
					Word woWord = Word.get(woChunk.wordIDs.get(0));		// 目的語"を"
					// (subject, property, object)を記述
					relations.add( Arrays.asList(predicate, "rdfs:domain", subjectWord.wordName) );
					relations.add( Arrays.asList(predicate, "rdfs:range", woWord.wordName) );	
					relations.add( Arrays.asList(subjectWord.wordName, predicateWord.tags.get(6), woWord.wordName) );
					
				}else {							// 目的語なし
					// (property, domain, subject)を記述
					// 動詞の原形が欲しいのでget(6)
					relations.add( Arrays.asList(predicate, "rdfs:domain", subjectWord.wordName) );
					relations.add( Arrays.asList(subjectWord.wordName, predicateWord.tags.get(6), "NoObject") );  // rdfでobjectなしってどうすんの
				}
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
				String blank = subjectWord.wordName+"_size";
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
