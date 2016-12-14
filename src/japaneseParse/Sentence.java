package japaneseParse;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

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
	public List<Integer> indexesOfW(List<Integer> wdl) {
		List<Integer> indexList = new ArrayList<Integer>();
		for(int wd: wdl) {
			indexList.add(indexOfW(wd));
		}
		return indexList;
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
	public void concatenate2() {
		for(final int chID: chunkIDs) {
			Chunk ch = Chunk.get(chID);
			List<Integer> nwdIDs = new ArrayList<Integer>();
			List<Integer> serialNouns = new ArrayList<Integer>();
			while( !ch.wordIDs.isEmpty() ) {
				int wdID = ch.wordIDs.remove(0);
				Word wd = Word.get(wdID);
				
				String[] nounTag = {"名詞"};
				if( wd.hasTags(nounTag) ) {
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
	public Sentence concatenate1(List<Integer> modifyWordList) {
		Sentence newsent = new Sentence();
		List<Integer> newIDlist = chunkIDs;

		List<Integer> modifyChunkList = new ArrayList<Integer>();
		for(int modifywd: modifyWordList) {
			int modchID = Word.get(modifywd).inChunk;
			if(!modifyChunkList.contains(modchID)) {	// 同一Chunk内に2つ修飾語がある場合(例:大地"の"よう"な")
				modifyChunkList.add(modchID);			// 重複回避 *要改善*
			}
		}
		List<List<Integer>> phChunksList = makeModificationList(modifyChunkList);
		
		// 複数のChunkを結合して新しいChunkを作成
		for(List<Integer> phChunks: phChunksList) {
			Chunk nch = new Chunk();
			nch.uniteChunks(phChunks);
			// 古いChunkを削除して新しいChunkを挿入
			newIDlist.add(newIDlist.indexOf(phChunks.get(0)), nch.chunkID);
			newIDlist.removeAll(phChunks);
		}
		Chunk.updateAllDependency();
		newsent.setSentence(newIDlist);
		return newsent;
	}
	
	/* 上記concatenateの補助 */
	/* 修飾節のリストから修飾節被修飾節のセットを作る */
	private List<List<Integer>> makeModificationList(List<Integer> modifyChunkList) {
		List<List<Integer>> phChunksList = new ArrayList<List<Integer>>();
		List<Integer> phChunks = new ArrayList<Integer>();
		for(int modifych: modifyChunkList) {
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
		if(ptcls_sp.isEmpty()) return null;		// **
		int ptcl_sp = ptcls_sp.get(0);			// 文中に1つしかないと仮定しているのでget(0) *要注意*
		
		Chunk subjectChunk = Chunk.get(Word.get(ptcl_sp).inChunk);		// 主節
		Chunk predicateChunk = Chunk.get(subjectChunk.dependUpon);		// 述節
				
		int p2pChunkID = predicateChunk.chunkID;
		int fromIndex = indexOfC(subjectChunk.chunkID)+1;
		int toIndex = indexOfC(p2pChunkID)+1;
		while(p2pChunkID != -1) {
			Chunk nextPredicateChunk = Chunk.get(p2pChunkID);
			System.out.println("(" + fromIndex + "," + toIndex + ")" + chunkIDs.subList(fromIndex, toIndex));
			List<Integer> partChunkList = new ArrayList<Integer>();
			partChunkList.add(subjectChunk.chunkID);
			partChunkList.addAll(chunkIDs.subList(fromIndex, toIndex));
			// 短文生成
			Sentence partSent = new Sentence();
			partSent.setSentence(partChunkList);
			partSent.simplePrint();
			partSentList.add(partSent);
			
			// 次の述語を見つけ，fromとtoを更新
			p2pChunkID = nextPredicateChunk.dependUpon;
			fromIndex = toIndex;
			toIndex = indexOfC(p2pChunkID)+1;
		}
		return partSentList;
	}
	
	/* 文章から関係を見つけtripleにする */
	public List<List<String>> extractRelation() {
		List<List<String>> relations = new ArrayList<List<String>>();
		/* 主語を探す */
		String[][] spTag = {{"助詞", "係助詞"}};	// 主語と述語を結ぶ係助詞"は"を探す
		List<Integer> ptcls_sp = collectTagWords(spTag);
		if(ptcls_sp.isEmpty()) return new ArrayList<List<String>>(); 
		int ptcl_sp = ptcls_sp.get(0);			// 文中に1つしかないと仮定しているのでget(0) *要注意*
		
		Chunk subjectChunk = Chunk.get(Word.get(ptcl_sp).inChunk);		// 主節("は"を含む)
		Word subjectWord = Word.get(subjectChunk.wordIDs.get(0));		// 主語
		Chunk predicateChunk = Chunk.get(subjectChunk.dependUpon);		// 述節
		Word predicateWord = Word.get(predicateChunk.wordIDs.get(0));	// 述語
		//Chunk complementChunk;										// 補節(いつか使うかも)
		//Word complementWord;											// 補語
		printDep();
	
		String[][] verbTag = {{"動詞"}};
		/* 述語が[<名詞>である。]なのか[<動詞>する。]なのか[<形容詞>。]なのか */
		// 述語が動詞でない-> (親クラス, 子クラス)を記述
		if( predicateChunk.collectTagWords(verbTag).isEmpty()) {
			List<String> relation = Arrays.asList(subjectWord.wordName, "rdfs:subClassOf", predicateWord.wordName);
			relations.add(relation);
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
				relation = Arrays.asList(predicateWord.tags.get(6), "rdfs:domain", subjectWord.wordName);
				relations.add(relation);
				relation = Arrays.asList(predicateWord.tags.get(6), "rdfs:range", objectWord.wordName);
				relations.add(relation);
				relation = Arrays.asList(subjectWord.wordName, predicateWord.tags.get(6), objectWord.wordName);
				relations.add(relation);
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
	
	public void simplePrint() {
		for(int cid: chunkIDs) {
			System.out.print(Chunk.get(cid).name());
		}
		System.out.println();
	}
	public void printW() {
		for(int wid: wordIDs()) {
			System.out.print("("+wid+")" + Word.get(wid).wordName);
		}
		System.out.println();
	}
	public void printC() {
		for(int cid: chunkIDs) {
			System.out.print("("+cid+")" + Chunk.get(cid).name());
		}
		System.out.println();
	}
	public void printDep() {
		for(int id: chunkIDs) {
			Chunk ch = Chunk.get(id);
			System.out.println("C" + ch.chunkID + ": " + ch.name() + "\t->" + ch.dependUpon);
		}
	}
	/* 文を区切りを挿入して出力する */
	public void printS() {
		for(int wid: wordIDs()) { // Word単位で区切る
			System.out.print(Word.get(wid).wordName + "|");
		}
		System.out.println();
		for(int cid: chunkIDs) { // Chunk単位で区切る
			System.out.print(Chunk.get(cid).name() + "|");
		}
		System.out.println();
	}
}
