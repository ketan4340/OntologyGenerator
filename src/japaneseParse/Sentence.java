package japaneseParse;

import java.util.*;

public class Sentence {
	// Chunkのリストで文を構成する
	public List<Integer> chunkIDs;
	
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
			taggedWords.addAll(Chunk.get(chk).collectTagWords(tagNames));	// 各Chunk内を探す
		}
		return taggedWords;
	}
	
	/* 文から2つの引数で挟まれた部分だけ切り取る */
	public Sentence subSentence(int fromIndex, int toIndex) {
		Sentence subsent = new Sentence();
		subsent.setSentence(chunkIDs.subList(fromIndex, toIndex));
		return subsent;
	}
	
	/* 渡された修飾語のWordを被修飾語につなげ、新しいPhraseを作る */
	public Sentence concatenate(List<Integer> wdl_NP) {
		Sentence newsent = new Sentence();
		List<Integer> newIDlist = chunkIDs;
		
		List<Integer> chl_NP = new ArrayList<Integer>();
		for(int wd: wdl_NP) {
			chl_NP.add(Word.get(wd).inChunk);
		}
		
		List<List<Integer>> phraseMaterials_ch = new ArrayList<List<Integer>>();
		
		for(int ch: chl_NP) {
			List<Integer> chunks4phrase = new ArrayList<Integer>();
			 int nextIndex = chunkIDs.indexOf(ch) + 1;
			if(nextIndex != chunkIDs.size()) {		// このChunkが文の最後の場合
				int nextch = chunkIDs.get(nextIndex);	// 修飾語と被修飾語が連続していることが前提の設計 *要改善*
				chunks4phrase.add(ch);
				if( !chl_NP.contains(nextch)) chunks4phrase.add(nextch);
				phraseMaterials_ch.add(chunks4phrase);
			}
		}
		
		// 複数のChunkを結合して新しいChunkを作成
		for(List<Integer> chunks_ph: phraseMaterials_ch) {
			Chunk nch = new Chunk();
			nch.uniteChunks(chunks_ph);
			// 古いChunkを削除して新しいChunkを挿入
			newIDlist.add(newIDlist.indexOf(chunks_ph.get(0)), nch.chunkID);
			newIDlist.removeAll(chunks_ph);
		}
		Chunk.updateAllDependency();
		newsent.setSentence(newIDlist);
		return newsent;
	}
	
	public List<List<String>> extractRelation() {
		List<List<String>> relations = new ArrayList<List<String>>();
		/* 主語を探す */
		String[][] spTag = {{"助詞", "係助詞"}};	// 主語と述語を結ぶ係助詞"は"を探す
		List<Integer> ptcls_sp = collectTagWords(spTag);
		int ptcl_sp = ptcls_sp.get(0);			// 文中に1つしかないと仮定しているのでget(0) *要注意*
		
		Chunk subjectChunk = Chunk.get(Word.get(ptcl_sp).inChunk);		// 主節("は"を含む)
		Word subjectWord = Word.get(subjectChunk.wordIDs.get(0));		// 主語
		Chunk predicateChunk = Chunk.get(subjectChunk.dependUpon);		// 述節
		Word predicateWord = Word.get(predicateChunk.wordIDs.get(0));	// 述語
		//Chunk complementChunk;										// 補節(いつか使うかも)
		//Word complementWord;											// 補語
		printW();
		printDep();
	
		String[][] verbTag = {{"動詞"}};
		/* 述語が[<名詞>である。]なのか[<動詞>する。]なのか[<形容詞>。]なのか */
		if( predicateChunk.collectTagWords(verbTag).isEmpty()) {	// 述語が動詞でない-> (親クラス, 子クラス)を記述
			List<String> relation = Arrays.asList(subjectWord.wordName, "rdfs:subClassOf", predicateWord.wordName);
			relations.add(relation);
			
		}else {		// 述語が動詞である
			List<String> relation = Arrays.asList(predicateWord.tags.get(6), "rdf:type", "rdfs:Proprety");
			relations.add(relation);
			relation = new ArrayList<String>();
			// 格助詞"で","に","を","へ"などを元に目的語を探す
			String[][] opTagName = {{"助詞", "格助詞"}};	// 目的語oと述語pを結ぶ助詞
			List<Integer> ptcls_op = collectTagWords(opTagName);
			/* 目的語の有無 */
			if(ptcls_op.isEmpty()) {		// 目的語なし
				// (property, domain, subject)を記述
				// 動詞の原形が欲しいのでget(6)
				relation = Arrays.asList(predicateWord.tags.get(6), "rdfs:domain", subjectWord.wordName);
				relations.add(relation);
				relation = Arrays.asList(subjectWord.wordName, predicateWord.tags.get(6), "NoObject");  // rdfでobjectなしってどうすんの
				relations.add(relation);
			}else {							// 目的語あり
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
		List<Integer> wordlist = new ArrayList<Integer>();
		for(int chunk: chunkIDs) {
			wordlist.addAll(Chunk.get(chunk).wordIDs);
		}
		return wordlist;
	}
	
	/* 文をWord型のリストにする */
	public List<Word> getWordList() {
		List<Word> words = new ArrayList<Word>();
		List<Integer> wordlist = wordIDs();
		for(int id: wordlist) {
			words.add(Word.get(id));
		}
		return words;
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
