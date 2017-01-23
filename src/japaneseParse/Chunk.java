package japaneseParse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class Chunk {
	public static int chunkSum = 0; 
	public static List<Chunk> allChunksList = new ArrayList<Chunk>();
	
	public int chunkID;
	public List<Integer> wordIDs;		// 構成するWordのidを持つ
	public int dependUpon;				// どのChunkに係るか
	public List<Integer> beDepended;	// どのChunkから係り受けるか
	public int originID;				// このChunkが別Chunkのコピーである場合，そのIDを示す
	public List<Integer> cloneIDs;		// このChunkのクローン達のID

	public Chunk() {
		chunkID = chunkSum++;
		allChunksList.add(this);
		wordIDs = new ArrayList<Integer>();
		dependUpon = -1;
		beDepended = new ArrayList<Integer>();
		originID = -1;
		cloneIDs = new ArrayList<Integer>();
	}
	public void setChunk(List<Integer> wdl, int depto) {
		for(Iterator<Integer> itr = wdl.iterator(); itr.hasNext(); ) {
			int wd = itr.next();
			addWord(wd);
		}
		dependUpon = depto;
	}
	
	public static Chunk get(int id) {
		if(id == -1) return null; 
		return allChunksList.get(id);
	}
	
	public List<Integer> getMainWords() {
		List<Integer> mains = new ArrayList<Integer>();
		for(int wordID: wordIDs) {
			Word word = Word.get(wordID);
			if(word.sb_fc) {
				mains.add(wordID);
			}
		}
		return mains;
	}
	
	public List<Integer> getAllDepending() {
		List<Integer> allDepending = new ArrayList<Integer>();
		for(int depto = this.dependUpon; depto != -1; depto = Chunk.get(depto).dependUpon) {
			allDepending.add(depto);
		}
		return allDepending;
	}
	
	public void uniteChunks(List<Integer> baseChunks) {
		List<Integer> phraseWords = new ArrayList<Integer>();		// 新しいPhraseの元になるWord
		List<Integer> conjunctionWords = new ArrayList<Integer>();	// Phrase完成後につなげる接続詞を保持
		int depto = -1;												// 最後尾のChunkがどのChunkに係るか
		
		for(Iterator<Integer> itr = baseChunks.iterator(); itr.hasNext(); ) {
			int chID = itr.next();
			Chunk ch = Chunk.get(chID);
			for(int wdID: ch.wordIDs) {		// 元ChunkのWordはこの新しいChunkに属するように変える
				Word.get(wdID).belongChunk = this.chunkID;
			}
			// 全ての元Chunkの係り先を新しいChunkに変える
			for(int bedep: ch.beDepended) {
				Chunk.get(bedep).dependUpon = this.chunkID;
			}
			
			if(!itr.hasNext()) {							// 最後尾の場合
				int head = ch.wordIDs.get(0);				// とりあえず先頭1単語を被修飾語とする *要改善*
				phraseWords.add(head);
				conjunctionWords.addAll(ch.wordIDs);		// 先頭以外を保管したいので
				conjunctionWords.remove(0);					// 全部入れて0番目を消す
				depto = ch.dependUpon;
			}else {
				phraseWords.addAll(ch.wordIDs);
			}
		}

		// 新しいPhraseを作成
		Phrase nph = new Phrase();
		nph.setPhrase(phraseWords, chunkID, false);
		List<Integer> newWordIDs = new ArrayList<Integer>();
		newWordIDs.add(nph.wordID);
		newWordIDs.addAll(conjunctionWords);
		setChunk(newWordIDs, depto);
	}
	
	public void addWord(int wordID) {
		wordIDs.add(wordID);
	}
	
	public int indexOfW(int wordID) {
		return wordIDs.indexOf(wordID);
	}
	
	/* 全く同じChunkを複製する */
	public Chunk copy() {
		Chunk replica = new Chunk();
		List<Integer> subWordIDs = new ArrayList<Integer>(wordIDs.size());
		for(int id: wordIDs) {
			Word subWord = Word.get(id).copy();
			subWord.belongChunk = replica.chunkID;
			subWordIDs.add(subWord.wordID);
		}
		replica.setChunk(subWordIDs, dependUpon);
		replica.originID = this.chunkID;
		cloneIDs.add(replica.chunkID);
		return replica;
	}
	/* 複数のChunkを係り受け関係を維持しつつ複製する */
	public static List<Integer> copy(List<Integer> chunkIDList) {
		List<Integer> replicaList = new ArrayList<Integer>();
		for(final int id: chunkIDList) {
			Chunk origin = Chunk.get(id);
			Chunk replica = origin.copy();
			replicaList.add(replica.chunkID);
		}
		
		return replicaList;
	}
	
	/* 指定の品詞を持つWordが並んでいたら繋げる */
	public void connect(String[][] tagNames) {
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
			if(hasSomeTag) {
				serialNouns.add(word.wordID);
			}else {
				if(!serialNouns.isEmpty()) {	// 初っ端からTagに該当しない場合のif
					Phrase nph = new Phrase();
					nph.setPhrase(serialNouns, chunkID, false);
					newWordIDs.add(nph.wordID);
					serialNouns.clear();
				}
				newWordIDs.add(wordID);
			}
		}
		
		if(!serialNouns.isEmpty()) {		// Chunkの末尾が名詞の場合ここで処理
			Phrase nph = new Phrase();
			nph.setPhrase(serialNouns, chunkID, false);
			newWordIDs.add(nph.wordID);
		}
		wordIDs = newWordIDs;
	}
	
	/* 指定の文字列に一致するWordのIDを返す */
	public List<Integer> collectWords(String name) {
		List<Integer> ids = new ArrayList<Integer>();
		for(final int id: wordIDs) {
			Word wd = Word.get(id);
			if(wd.wordName.equals(name))	ids.add(id);
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
	public boolean haveAllTagWord(String[][] tagNames) {
		List<String[]> tagNamesList = Arrays.asList(tagNames);
		for(final int wordID: wordIDs) {
			Word word = Word.get(wordID);
			for (final String[] tagsArray: tagNamesList){
				if(word.hasAllTags(tagsArray))	return true;
			}
		}
		return false;
	}
	
	/* このChunkの最後尾が渡された品詞のWordかどうか */
	/* 最後尾が読点"、"の場合は無視 */
	public boolean endWith(String[][] tagNames) {
		boolean endWith = true;
		int tagIndex = tagNames.length-1;
		String[] tagComma = {"読点"};
		
		for(ListIterator<Integer> li = wordIDs.listIterator(wordIDs.size()-1); li.hasPrevious(); ) {
			int wordID = li.previous();
			Word word = Word.get(wordID);
			String[] tagName = tagNames[tagIndex];
			if(word.hasAllTags(tagComma)) continue;	// "、"の場合はスルー
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
	
	/* このChunkのうち、指定された範囲のWordを繋げて一つの品詞にする */
	public void nounize(int fromIndex, int toIndex) {
		List<Integer> mainIDs = wordIDs.subList(fromIndex, toIndex);
		
		Phrase properNoun = new Phrase();	// 固有名詞として扱う
		properNoun.setPhrase(mainIDs, chunkID, true);
		mainIDs.clear();
		this.wordIDs.add(fromIndex, properNoun.wordID);
	}

	/* Chunkを文字列で返す */
	public String toString() {
		String chunkName = new String();
		for(int orgid: wordIDs) {
			chunkName += Word.get(orgid).wordName;
		}
		return chunkName;
	}
	
	/* chunkの係り受け関係を更新 */
	/* 全てのChunkインスタンスのdependUponが正しいことが前提の設計 */
	public static void updateAllDependency() {
		for(final Chunk chk: Chunk.allChunksList) chk.beDepended.clear();	// 一度全ての被係り受けをまっさらにする
		for(final Chunk chk: Chunk.allChunksList) {
			int depto = chk.dependUpon;
			if(depto != -1) Chunk.get(depto).beDepended.add(chk.chunkID);
		}
	}
	
}
