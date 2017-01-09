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
		Sentence partS = new Sentence();
		List<Integer> partIDs = new ArrayList<Integer>(chunkIDs.subList(fromIndex, toIndex));
		partS.setSentence(partIDs);
		return partS;
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
	/* 渡された品詞に一致するWordのIndexを配列で返す
	 * [Sentence上のChunkのIndex, Chunk上のWordのIndex] */
	public List<Integer[]> collectTagIndexS(String[][] tagNames) {
		
		return null;
	}
	
	/* Tagの配列に文のWord列がマッチするか判定 */
	public boolean matchTags(String[][] tags) {
		List<Integer[]> wdl = new ArrayList<Integer[]>();
		List<Integer> wdIDList = wordIDs();
		boolean match = false;
		int i = 0;
		
		for(final int wdID: wdIDList) {
			Word wd = Word.get(wdID);
			if(wd.hasTags(tags[i])) {
				match = true;
				i++;
			}else {
				match = false;
				i = 0;
			}
			
			if(i >= tags.length) {
				
			}
		}
		return match;
	}
	
	public void concatenate3(String[][] tags) {
		List<Integer> wdIDList = wordIDs();
		
		for(String[] tag: tags) {
			
		}
	}
	/* 連続した名詞を繋いで一つの名詞にする */
	public void concatenateNouns() {
		for(final int chID: chunkIDs) {
			Chunk ch = Chunk.get(chID);
			List<Integer> nwdIDs = new ArrayList<Integer>();
			List<Integer> serialNouns = new ArrayList<Integer>();
			while( !ch.wordIDs.isEmpty() ) {
				int wdID = ch.wordIDs.remove(0);
				Word wd = Word.get(wdID);
				
				String[] nounTag = {"名詞"};
				String[] prefixTag = {"名詞接続"};
				String[] sufixTag = {"接尾"};	
				if( wd.hasTags(nounTag) | wd.hasTags(prefixTag) | wd.hasTags(sufixTag)) {
					serialNouns.add(wd.wordID);
				}else {
					if(!serialNouns.isEmpty()) {
						Phrase nph = new Phrase();
						nph.setPhrase(serialNouns, ch.chunkID);
						nwdIDs.add(nph.wordID);
					}
					nwdIDs.add(wdID);
					serialNouns.clear();
				}
			}
			if(!serialNouns.isEmpty()) {		// Chunkの末尾が名詞の場合ここで処理
				Phrase nph = new Phrase();
				nph.setPhrase(serialNouns, ch.chunkID);
				nwdIDs.add(nph.wordID);
			}
			ch.wordIDs = nwdIDs;
		}
	}
	
	/* 渡された修飾語のWordを被修飾語につなげ、新しいPhraseを作る */
	/* 結合元のWordをPhraseに置き換えたSentenceを返す */
	/* 現状のChunk依存の結合方法からWord結合に治すべき*要改善* */
	public void concatenate1(List<Integer> modifyWordList) {
		List<Integer> newIDlist = chunkIDs;

		List<Integer> modifyChunkList = new ArrayList<Integer>();
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
		}
		Chunk.updateAllDependency();
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
	
	/* 複数の述語を持つ文を述語ごと短文に切り分ける */
	public List<Sentence> separate() {
		List<Sentence> partSentList = new ArrayList<Sentence>();
		/* 主語を探す */
		String[][] spTag = {{"助詞", "係助詞"}};	// 主語と述語を結ぶ係助詞"は"を探す
		List<Integer> ptcls_sp = collectTagWords(spTag);
		if(ptcls_sp.isEmpty()) return partSentList;
		int ptcl_sp = ptcls_sp.get(0);			// 文中に1つしかないと仮定しているのでget(0) *要注意*
		
		Chunk subjectChunk = Chunk.get(Word.get(ptcl_sp).inChunk);		// 主節
		
		int nextPredicateID = subjectChunk.dependUpon;			// 次の述語のID
		if(nextPredicateID == -1) return partSentList;
		int fromIndex = indexOfC(subjectChunk.chunkID)+1;		// 述部を切り取るための始点
		int toIndex = indexOfC(nextPredicateID)+1;				// 述部を切り取るための終点
		while(nextPredicateID != -1) {
			Chunk nextPredicateChunk = Chunk.get(nextPredicateID);
			List<Integer> newPredicates = chunkIDs.subList(fromIndex, toIndex);
			System.out.println("(" + fromIndex + "," + toIndex + ")" + newPredicates);
			List<Integer> partChunkList = new ArrayList<Integer>();
			// 主節は新しいインスタンスを用意
			Chunk newSubject = new Chunk();
			newSubject.setChunk(subjectChunk.wordIDs, nextPredicateID);	// 主節の係り先だけ変更
			partChunkList.add(newSubject.chunkID);
			partChunkList.addAll(newPredicates);
			// 短文生成
			Sentence partSent = new Sentence();
			partSent.setSentence(partChunkList);
			System.out.println(partSent.toString());
			partSentList.add(partSent);
			
			// 次の述語を見つけ，fromとtoを更新
			nextPredicateID = nextPredicateChunk.dependUpon;
			fromIndex = toIndex;
			toIndex = indexOfC(nextPredicateID)+1;
		}
		return partSentList;
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
	public List<Sentence> separate2() {
		List<Sentence> partSentList = new ArrayList<Sentence>(5);
		
		/* 主語を全て探し，それらが連続しているか否かを調べる */
		List<Integer> s_pParticleList;		// 係助詞のリスト
		List<Integer> subjectList;			// 主節のリスト
		List<Boolean> sbjContinuityList;	// 主節の連続性を表す真偽値のリスト
		String[][] s_pTag = {{"助詞", "係助詞"}};		// subject-predicate
		s_pParticleList = collectTagWords(s_pTag);			// 係助詞"は"探し
		if(s_pParticleList.isEmpty()) return partSentList;	// 係助詞がなければ終わり
		subjectList = new ArrayList<Integer>(s_pParticleList.size());	
		for(final int s_pParticle: s_pParticleList) {
			subjectList.add(Word.get(s_pParticle).inChunk);		// 係助詞リストから主節リストを作る
		}
		sbjContinuityList = getContinuity(subjectList);		// 主節の連続性を真偽値で表す
		
		System.out.println(subjectList);
		System.out.println(sbjContinuityList);
		
		List<Integer> headSubjectList = new ArrayList<Integer>(subjectList.size());
		for(int i=0; i < subjectList.size(); i++) {
			int spParticle = s_pParticleList.get(i);		// 係助詞
			int sbjID = subjectList.get(i);					// 主節のID
			Chunk directSubject = Chunk.get(sbjID);			// 主節のChunk
			boolean sbjContinuity = sbjContinuityList.get(i);		// 主節のあとに別の主節が隣接しているか
			
			if(sbjContinuity) {	// このChunkの次も主節である場合
				Chunk headSubject = directSubject.copy();
				Word no = new Word();	// 助詞・連体化"の"を新たに用意
				no.setWord("の", Arrays.asList("助詞","連体化"), headSubject.chunkID);
				headSubject.wordIDs.set(headSubject.wordIDs.indexOf(spParticle), no.wordID);	// "は"の代わりに"の"を挿入
				headSubjectList.add(headSubject.chunkID);	// 連続した主節は貯め置きしとく
				
			}else {				// このChunkの次は主節ではない場合
				List<Integer> partChunkList = new ArrayList<Integer>();		// 短文を構成するChunkのリスト
				// 主部をまとめる
				// 新しい主節のインスタンスを用意
				Chunk partSubject = directSubject.copy();
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
				int fromIndex = indexOfC(directSubject.chunkID)+1;	// 述部切り取りの始点は主節の次
				int toIndex = (i+1<sbjContinuityList.size())			// 述部切り取りの終点は
						? indexOfC(subjectList.get(i+1))				// 次の主節の位置
						: chunkIDs.size();								// なければ文末
				System.out.println("\t(from: " + fromIndex + ", to: " + toIndex + ")");
				List<Integer> partPredicates = chunkIDs.subList(fromIndex, toIndex);	// 切り取った述部
				Chunk partEndChunk = Chunk.get(partPredicates.get(partPredicates.size()-1));
				partEndChunk.dependUpon = -1;	// 最後尾の述語はどこにも係らない
				// 述部の分割
				int nextPredicateID = directSubject.dependUpon;			// 次の述語のID
				if(nextPredicateID == -1) break;
				int fromPrdIndex = indexOfC(directSubject.chunkID)+1;	// 述部分割の始点
				int toPrdIndex = indexOfC(nextPredicateID)+1;			// 述部分割の終点
				while(nextPredicateID != -1) {
					Chunk nextPredicateChunk = Chunk.get(nextPredicateID);
					List<Integer> piecePredicates = chunkIDs.subList(fromPrdIndex, toPrdIndex);
					System.out.println("(" + fromPrdIndex + "," + toPrdIndex + ")" + piecePredicates);
					// 主節は新しいインスタンスを用意
					Chunk newSubject_c = newSbjChunk.copy();
					newSubject_c.dependUpon = nextPredicateID;
					
					partChunkList.add(newSubject_c.chunkID);	// 結合主部セット
					partChunkList.addAll(piecePredicates);		// 部分述部セット
					// 短文生成
					Sentence partSent = new Sentence();
					partSent.setSentence(partChunkList);
					partSent.printC();
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
	
	/* 文章から関係を見つけtripleにする */
	public List<List<String>> extractRelation() {
		List<List<String>> relations = new ArrayList<List<String>>();
		/* 主語を探す */
		String[][] spTag = {{"助詞", "係助詞"}};	// 主語と述語を結ぶ係助詞"は"を探す
		List<Integer> ptcls_sp = collectTagWords(spTag);
		if(ptcls_sp.isEmpty()) return relations;
		int ptcl_sp = ptcls_sp.get(0);			// 文中に1つしかないと仮定しているのでget(0) *要注意*
		
		Chunk subjectChunk = Chunk.get(Word.get(ptcl_sp).inChunk);		// 主節("は"を含む)
		Word subjectWord = Word.get(subjectChunk.wordIDs.get(0));		// 主語
		Chunk predicateChunk = Chunk.get(subjectChunk.dependUpon);		// 述節
		Word predicateWord = Word.get(predicateChunk.wordIDs.get(0));	// 述語
		//Chunk complementChunk;										// 補節(いつか使うかも)
		//Word complementWord;											// 補語
		String predicatePart = subSentence(chunkIDs.indexOf(subjectChunk.chunkID)+1, chunkIDs.size()).toString();	// 述部(主節に続く全ての節)
		printDep();
		//System.out.println(subjectChunk.name() + predicateChunk.name());
		
		String[][] verbTag = {{"動詞"}};
		/* 述語が[<名詞>である。]なのか[<動詞>する。]なのか[<形容詞>。]なのか */
		// 述語が動詞でない-> (親クラス, 子クラス)を記述
		if( predicateChunk.collectTagWords(verbTag).isEmpty()) {
			/* リテラル情報かどうか */
			String regexLiteral = "(.*?)(\\d+)([ア-ンa-zA-Z　ー－]+)(.*?)";	// ~(数字)(単位)~を探す
			Pattern ptrnLiteral = Pattern.compile(regexLiteral);
			Matcher mtchLiteral = ptrnLiteral.matcher(predicateChunk.toString());
			boolean boolLiteral = mtchLiteral.matches();
			/* 別名・同義語かどうか */
			String regexSynonym = "(.*?)((に同じ)|(の別名)|(の略)|(のこと))";	// 「〜の別名」「〜に同じ」を探す
			Pattern ptrnSynonym = Pattern.compile(regexSynonym);
			Matcher mtchSynonym = ptrnSynonym.matcher(predicatePart);
			boolean boolSynonym = mtchSynonym.matches();
			/* 一種・一品種かどうか */
			String regexKind = "(.*?)((の一種)|(の一品種))";	// 「〜の別名」「〜に同じ」を探す
			Pattern ptrnKind = Pattern.compile(regexKind);
			Matcher mtchKind = ptrnKind.matcher(predicatePart);
			boolean boolKind = mtchKind.matches();
			if(boolLiteral) {
				String blank = subjectWord.wordName+"_size";
				relations.add( Arrays.asList(subjectWord.wordName, "ex:size", blank) );			// 空白ノード
				relations.add( Arrays.asList(blank, "rdf:value", mtchLiteral.group(2)) );		// リテラル
				relations.add( Arrays.asList(blank, "exterms:units", mtchLiteral.group(3)) );	// 単位
			}else if(boolSynonym) {
				relations.add( Arrays.asList(subjectWord.wordName, "owl:sameAs", mtchSynonym.group(1)) );
				//relations.add( Arrays.asList(subjectWord.wordName, "owl:equivalentClass", mtchSynonym.group(1)) );
			}else if(boolKind) {
				relations.add( Arrays.asList(subjectWord.wordName, "rdf:type", mtchSynonym.group(1)) );
			}else {
				relations.add( Arrays.asList(subjectWord.wordName, "rdfs:subClassOf", predicateWord.wordName) );
			}
			
		// 述語が動詞である
		}else {
			List<String> relation = Arrays.asList(predicateWord.tags.get(6), "rdf:type", "rdfs:Proprety");
			relations.add(relation);
			relation = new ArrayList<String>();
			// 格助詞"で","に","を","へ"などを元に目的語を探す
			String[][] opTagName = {{"助詞", "格助詞"}};	// 目的語oと述語pを結ぶ助詞
			List<Integer> ptcls_op = collectTagWords(opTagName);
			
			/* 目的語の有無 */
			// 目的語なし
			if(ptcls_op.isEmpty()) {
				// (property, domain, subject)を記述
				// 動詞の原形が欲しいのでget(6)
				relation = Arrays.asList(predicateWord.tags.get(6), "rdfs:domain", subjectWord.wordName);
				relations.add(relation);
				relation = Arrays.asList(subjectWord.wordName, predicateWord.tags.get(6), "NoObject");  // rdfでobjectなしってどうすんの
				relations.add(relation);
			// 目的語あり
			}else {
				int ptcl_op = ptcls_op.get(0);	// 文中に1つしかないと仮定しているのでget(0) 要改善
				Chunk objectChunk = Chunk.get(Word.get(ptcl_op).inChunk);
				Word objectWord = Word.get(objectChunk.wordIDs.get(0));		// 目的語
				// (subject, property, object)を記述
				relations.add( Arrays.asList(predicateWord.tags.get(6), "rdfs:domain", subjectWord.wordName) );
				relations.add( Arrays.asList(predicateWord.tags.get(6), "rdfs:range", objectWord.wordName) );	
				relations.add( Arrays.asList(subjectWord.wordName, predicateWord.tags.get(6), objectWord.wordName) );
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
