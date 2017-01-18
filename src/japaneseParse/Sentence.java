package japaneseParse;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class Sentence {
	public List<Integer> chunkIDs; // Chunkのリストで文を構成する
		
	public Sentence() {
		chunkIDs = new ArrayList<Integer>();
	}
	public void setSentence(List<Integer> chunkList) {
		chunkIDs = chunkList;
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
			taggedWords.addAll(ch.collectTagWords(tagNames));	// 各Chunk内を探す
		}
		return taggedWords;
	}
	/* 渡された品詞に一致するWordを含むChunkのIDを返す */
	public List<Integer> collectTagChunks(String[][] tagNames) {
		List<Integer> taggedChunks = new ArrayList<Integer>();
		for(final int chunkID: chunkIDs) {
			Chunk chunk = Chunk.get(chunkID);
			if(chunk.haveTagWord(tagNames))	taggedChunks.add(chunkID);	// 各Chunk内を探す
		}
		return taggedChunks;
	}
		
	/* 連続した名詞を繋いで一つの名詞にする */
	/* Chunkを跨いで連結はしない */
	public void concatenateNouns() {
		for(final int chID: chunkIDs) {
			Chunk ch = Chunk.get(chID);
			String[][] tagNames = {{"名詞"}, {"名詞接続"}, {"接尾"}, {"形容詞"}};
			ch.concatenate(tagNames);
		}
	}
	
	/* 渡された修飾語のWordを被修飾語につなげ、新しいPhraseを作る */
	/* 結合元のWordをPhraseに置き換えたSentenceを返す */
	/* 現状のChunk依存の結合方法からWord結合に治すべき*要改善* */
	public void concatenateModifer(List<Integer> modifyWordList) {
		List<Integer> newIDlist = chunkIDs;

		List<Integer> modifyChunkList = new ArrayList<Integer>(modifyWordList.size());
		for(final int modifywd: modifyWordList) {
			int modchID = Word.get(modifywd).inChunk;
			if(!modifyChunkList.contains(modchID)) {	// 同一Chunk内に2つ修飾語がある場合(例:大地"の"よう"な")
				modifyChunkList.add(modchID);			// 重複回避 *要改善*
			}
		}
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
	
	/* 複数の述語を持つ文を述語ごと短文に切り分ける */
	public List<Sentence> separate() {
		List<Sentence> partSentList = new ArrayList<Sentence>(5);
		
		/* 主語を全て探し，それらが連続しているか否かを調べる */
		List<Integer> subjectList;			// 主節のリスト
		List<Boolean> sbjContinuityList;	// 主節の連続性を表す真偽値のリスト
		String[][] tag_Ha = {{"係助詞", "は"}};	// "は"
		String[][] tag_De = {{"格助詞", "で"}};	// "で"
		List<Integer> kchunk_Ha_List = new ArrayList<Integer>(collectTagChunks(tag_Ha));	// 係助詞"は"を含むChunk
		List<Integer> chunk_De_List = new ArrayList<Integer>(collectTagChunks(tag_De));	// 格助詞"で"を含むChunk
		kchunk_Ha_List.removeAll(chunk_De_List);			// "は"を含むChunkのうち、"で"を含まないものが主語
		subjectList = kchunk_Ha_List;
		if(subjectList.isEmpty()) return partSentList;		// 文中に主語がなければ終了
		

		//System.out.println("sbjList(before): " + subjectList);
		int lastChunkID = chunkIDs.get(chunkIDs.size()-1);	// 文の最後尾Chunk
		if(subjectList.contains(lastChunkID)) {	// 文の最後尾が主節の場合
			// おそらく固有名詞を正しく判定できていないせい
			// 最後尾の文節は一つの名詞にする
			Chunk lastChunk = Chunk.get(lastChunkID);
			lastChunk.nounize(0, lastChunk.wordIDs.size());
			int idx = subjectList.indexOf(lastChunkID);
			subjectList.remove(idx);
		}
		//System.out.println("sbjList(after): " + subjectList);
		if(subjectList.isEmpty()) return partSentList;		// 主節なし！構文解析失敗の可能性高し．空のリストを返す
		sbjContinuityList = getContinuity(subjectList);		// 主節の連続性を真偽値で表す
		
		//System.out.println("cntList: " + sbjContinuityList);
		
		List<Integer> headSubjectList = new ArrayList<Integer>(subjectList.size());
		for(int i=0; i < subjectList.size(); i++) {
			int sbjID = subjectList.get(i);					// 主節のID
			Chunk directSubject = Chunk.get(sbjID);			// 主節のChunk
			boolean sbjContinuity = sbjContinuityList.get(i);		// 主節のあとに別の主節が隣接しているか
			
			if(sbjContinuity) {	// このChunkの次も主節である場合
				Chunk headSubject = directSubject.copy();
				Word no = new Word();	// 助詞・連体化"の"を新たに用意
				no.setWord("の", Arrays.asList("助詞","連体化","*","*","*","*","の","ノ","ノ"), headSubject.chunkID, false);
				int index_Ha = headSubject.indexOfW(headSubject.collectTagWords(tag_Ha).get(0));
				headSubject.wordIDs.set(index_Ha, no.wordID);	// "は"の代わりに"の"を挿入
				headSubjectList.add(headSubject.chunkID);	// 連続した主節は貯め置きしとく
				
			}else {				// このChunkの次は主節ではない場合
				// 主部をまとめる
				// 新しい主節のインスタンスを用意
				Chunk partSubject = directSubject.copy();
				/*
				if(!headSubjectList.contains(topSbjID)) {		// 先頭の主語を必ず入れる
					Chunk topSubject = Chunk.get(topSbjID).copy();
					Word no = new Word();	// 助詞・連体化"の"を新たに用意
					no.setWord("の", Arrays.asList("助詞","連体化","*","*","*","*","の","ノ","ノ"), topSubject.chunkID);
					topSubject.wordIDs.set(topSubject.wordIDs.size()-1, no.wordID);	// "は"の代わりに"の"を挿入 *index*
					headSubjectList.add(0, topSubject.chunkID);
				}
				*/
				List<Integer> copiedHeadSubjectList = new ArrayList<Integer>(headSubjectList);	// 使い回すので複製
				copiedHeadSubjectList.add(partSubject.chunkID);
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
					//System.out.println(partSent.toString());
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
	
	/* 文章から主語・述語・目的語を見つける */
	public void setComponent() {
		
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
		printDep();
		System.out.println("prePart: " + predicatePart + "________\t" + "preChunk: " + predicateChunk.toString());
		System.out.println("[" + subjectChunk.toString() + "][" + predicateChunk.toString() + "]");
		
		
		/* 述語が[<名詞>である。]なのか[<動詞>する。]なのか[<形容詞>。]なのか */
		String[][] tagVerb = {{"動詞"}, {"サ変接続"}};
		if( predicateChunk.collectTagWords(tagVerb).isEmpty()) {	// 述語が動詞でない-> (親クラス, 子クラス)を記述
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
			/* 総称かどうか */
			String regexGeneral = "(.*?)((の総称))";					// 「〜の総称」を探す
			Pattern ptrnGeneral = Pattern.compile(regexGeneral);
			Matcher mtchGeneral = ptrnGeneral.matcher(predicatePart);
			boolean boolGeneral = mtchGeneral.matches();
			
			if(boolLiteral) {
				String blank = subjectWord.wordName+"_size";
				relations.add( Arrays.asList(subjectWord.wordName, "ex:size", blank) );			// 空白ノード
				relations.add( Arrays.asList(blank, "rdf:value", mtchLiteral.group(2)) );		// リテラル
				relations.add( Arrays.asList(blank, "exterms:units", mtchLiteral.group(4)) );	// 単位
			}else if(boolSynonym) {
				relations.add( Arrays.asList(subjectWord.wordName, "owl:sameClassAs", mtchSynonym.group(1)) );
			}else if(boolKind) {
				relations.add( Arrays.asList(subjectWord.wordName, "rdf:type", mtchKind.group(1)) );
			}else if(boolGeneral) {
				relations.add( Arrays.asList(subjectWord.wordName, "owl:equivalentClass", mtchGeneral.group(1)) );
			}else {
				relations.add( Arrays.asList(subjectWord.wordName, "rdfs:subClassOf", predicateWord.wordName) );
			}
			
		// 述語が動詞である
		}else {
			/* "がある"かどうか */
			String[][] tag_Have = {{"ある"}, {"もつ"}};		// 動詞の"ある"(助動詞ではない)
			boolean boolHave = predicateChunk.haveTagWord(tag_Have);
			if(boolHave) {
				int idxprd = indexOfC(predicateChunk.chunkID);
				Chunk previousChunk = Chunk.get(chunkIDs.get(idxprd-1));	// "ある"の一つ前の文節
				String[][] tag_Ga_Wo = {{"格助詞", "が"}, {"格助詞", "を"}};
				if(previousChunk.haveTagWord(tag_Ga_Wo)) {
					Word part = previousChunk.getMainWord();
					relations.add( Arrays.asList(subjectWord.wordName, "partOf", part.wordName) );
				}
			}else {
				relations.add( Arrays.asList(predicateWord.tags.get(6), "rdf:type", "rdfs:Proprety") );
								
				// 格助詞"で","に","を","へ"などを元に目的語を探す
				String[][] tag_Ni = {{"助詞", "格助詞", "に"}};	// 目的語oと述語pを結ぶ助詞
				List<Integer> ptcls_ni = collectTagWords(tag_Ni);
				String[][] tag_Wo = {{"助詞", "格助詞", "を"}};	// 目的語oと述語pを結ぶ助詞
				List<Integer> ptcls_wo = collectTagWords(tag_Wo);
				
				/* 目的語の有無 */
				if(!ptcls_ni.isEmpty()) {		// 目的語"に"あり
					int ptcl_ni = ptcls_ni.get(0);	// 文中に1つしかないと仮定しているのでget(0) 要改善
					Chunk niChunk = Chunk.get(Word.get(ptcl_ni).inChunk);
					Word niWord = Word.get(niChunk.wordIDs.get(0));		// 目的語"に"
					// (subject, property, object)を記述
					relations.add( Arrays.asList(predicateWord.tags.get(6), "rdfs:domain", subjectWord.wordName) );
					relations.add( Arrays.asList(predicateWord.tags.get(6), "rdfs:range", niWord.wordName) );	
					relations.add( Arrays.asList(subjectWord.wordName, predicateWord.tags.get(6), niWord.wordName) );
				
				}else if(!ptcls_wo.isEmpty()) {	// 目的語"を"あり
					int ptcl_wo = ptcls_wo.get(0);	// 文中に1つしかないと仮定しているのでget(0) 要改善
					Chunk woChunk = Chunk.get(Word.get(ptcl_wo).inChunk);
					Word woWord = Word.get(woChunk.wordIDs.get(0));		// 目的語"を"
					// (subject, property, object)を記述
					relations.add( Arrays.asList(predicateWord.tags.get(6), "rdfs:domain", subjectWord.wordName) );
					relations.add( Arrays.asList(predicateWord.tags.get(6), "rdfs:range", woWord.wordName) );	
					relations.add( Arrays.asList(subjectWord.wordName, predicateWord.tags.get(6), woWord.wordName) );
					
				}else {							// 目的語なし
					// (property, domain, subject)を記述
					// 動詞の原形が欲しいのでget(6)
					relations.add( Arrays.asList(predicateWord.tags.get(6), "rdfs:domain", subjectWord.wordName) );
					relations.add( Arrays.asList(subjectWord.wordName, predicateWord.tags.get(6), "NoObject") );  // rdfでobjectなしってどうすんの
				}
			}
		}
		return relations;
	}
	
	/* ChunkのIDのリストからWordのIDのリストにする */
	public List<Integer> wordIDs() {
		List<Integer> wordIDlist = new ArrayList<Integer>();
		for(int chunk: chunkIDs) {
			wordIDlist.addAll(Chunk.get(chunk).wordIDs);
		}
		return wordIDlist;
	}
	
	/* 文をWord型のリストにする */
	public List<Word> getWordList() {
		List<Word> wordList = new ArrayList<Word>();
		for(int chID: chunkIDs) {
			wordList.addAll(Chunk.get(chID).getWordList());
		}
		return wordList;
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
