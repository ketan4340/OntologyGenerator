package japaneseParse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Chunk {
	public static int chunkSum = 0; 
	public static List<Chunk> allChunksList = new ArrayList<Chunk>();
	
	public int chunkID;
	public List<Integer> wordIDs;		// 構成するWordのidを持つ
	public int dependUpon;	// どのChunkに係るか
	public List<Integer> beDepended;	// どのChunkから係り受けるか

	public Chunk() {
		chunkID = chunkSum++;
		allChunksList.add(this);
		wordIDs = new ArrayList<Integer>();
		dependUpon = -1;
		beDepended = new ArrayList<Integer>();
	}
	public void setChunk(List<Integer> wdl, int depto) {
		for(Iterator<Integer> itr = wdl.iterator(); itr.hasNext(); ) {
			int wd = itr.next();
			addWord(wd);
		}
		dependUpon = depto;
	}
	
	public void uniteChunks(List<Integer> chunks_ph) {
		List<Integer> phraseWords = new ArrayList<Integer>();		// 新しいPhraseの元になるWord
		List<Integer> conjunctionWords = new ArrayList<Integer>();	// Phrase完成後につなげる接続詞を保管
		int depto = -1;												// 最後尾のChunkがどのChunkに係るか
		
		for(Iterator<Integer> itr = chunks_ph.iterator(); itr.hasNext(); ) {
			int chID = itr.next();
			Chunk ch = Chunk.get(chID);
			for(int wdID: ch.wordIDs) {
				Word wd = Word.get(wdID);
				wd.inChunk = this.chunkID;
			}
			// 全ての元Chunkの係り先を新しいChunkに変える
			for(int bedep: ch.beDepended) Chunk.get(bedep).dependUpon = this.chunkID;	
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
		nph.setPhrase(phraseWords, chunkID);
		List<Integer> chunkSource_wd = new ArrayList<Integer>();
		chunkSource_wd.add(nph.wordID);
		chunkSource_wd.addAll(conjunctionWords);
		setChunk(chunkSource_wd, depto);
	}
	
	public static Chunk get(int id) {
		return allChunksList.get(id);
	}
	
	public void addWord(int wordID) {
		wordIDs.add(wordID);
	}
	
	/* Chunkを文字列で返す */
	public String name() {
		String chunkName = new String();
		for(int orgid: wordIDs) {
			chunkName += Word.get(orgid).wordName;
		}
		return chunkName;
	}
	
	/* Chunkに指定の品詞が含まれるかを判定 */
	public List<Integer> collectTagWords(String[][] tagNames) {
		List<String[]> tagNamesList = Arrays.asList(tagNames);
		List<Integer> taggedIDs = new ArrayList<Integer>();
		for(final int id: wordIDs) {
			for (final String[] tagsArray: tagNamesList){
				List<String> taglist = Arrays.asList(tagsArray);
				if(Word.get(id).tags.containsAll(taglist)) taggedIDs.add(id);
			}
		}
		return taggedIDs;
	}
	
	/* chunkの係り受け関係を更新 */
	/* 全てのChunkインスタンスのdependUponが正しいことが前提の設計 */
	public static void updateAllDependency() {
		for(Chunk chk: Chunk.allChunksList) chk.beDepended.clear();	// 一度全ての被係り受けをまっさらにする
		for(Chunk chk: Chunk.allChunksList) {
			int depto = chk.dependUpon;
			if(depto != -1) Chunk.get(depto).beDepended.add(chk.chunkID);
		}
	}
	
	public static void printAllChunks() {
		for(Chunk ch: allChunksList) {
			System.out.println("C" + ch.chunkID + ": " + ch.name() + "\t->" + ch.dependUpon);
		}
	}
}
