package japaneseParse;

import java.util.*;

public class Word {
	public static int wordsSum = 0;
	public static List<Word> allWordsList = new ArrayList<Word>();
	
	public int wordID;				// 通し番号。Wordを特定する
	public String wordName;			// 単語の文字列
	public List<String> tags;		// 品詞・活用形、読みなど
	public int belongChunk;				// どのChunkに所属するか
	public int originID;			// このWordが別Wordのコピーである場合，そのIDを示す
	public List<Integer> cloneIDs;	// このWordのクローン達のID
	public boolean sbj_fnc;			// 主辞か機能語か
		
	public Word() {
		wordID = wordsSum++;
		allWordsList.add(this);
		wordName = new String();
		tags = new ArrayList<String>();
		belongChunk = -1;
		originID = -1;
		cloneIDs = new ArrayList<Integer>();
		sbj_fnc = false;
	}
	public void setWord(String nWordName, List<String> nWordTags, int chunkID, boolean sf) {
		wordName = nWordName;
		tags.addAll(nWordTags);
		if(tags.size() < 9) tags.addAll(Arrays.asList("*", "*"));	// tagの数が最低9個になるように
		if(tags.get(6).equals("*")) {	// 特殊漢字の原形をここで入力してあげる
			tags.set(6, nWordName);
		}
		if(tags.contains("記号")) sf = false;	// 記号なら明らかに主辞ではない
		belongChunk = chunkID;
		sbj_fnc = sf;
	}
	
	public static Word get(int id) {
		return allWordsList.get(id);
	}
	
	/* 渡されたTagを"全て"持って入れば真、それ以外は偽を返す */
	public boolean hasAllTags(String[] tagNames) {
		boolean match = true;	// デフォがtrueなので空の配列は任意の品詞とみなされる
		for(String tag: tagNames) {
			boolean not = false;	// NOT検索用のフラグ
			if(tag.startsWith("-")) {	// Tag名の前に-をつけるとそのタグを含まない時にtrue
				not = true;
				tag = tag.substring(1);	// -を消しておく
			}
			
			if( tags.contains(tag) ) {
				match = (not)? false: true;
			}else {
				match = (not)? true: false;
			}
			if(!match) break;	// falseなら即終了
		}
		return match;
	}
	/* 渡されたTagを"1つでも"持って入れば真、それ以外は偽を返す */
	public boolean hasSomeTags(String[] tagNames) {
		if(tagNames.length == 0) return true;	// 空の品詞を渡されたらtrue
		boolean match = false;
		for(String tag: tagNames) {
			boolean not = false;	// NOT検索用のフラグ
			if(tag.startsWith("-")) {	// Tag名の前に-をつけるとそのタグを含まない時にtrue
				not = true;
				tag = tag.substring(1);	// -を消しておく
			}
			
			if( tags.contains(tag) ) {
				match = (not)? false: true;
			}else {
				match = (not)? true: false;
			}
			if(match) break;	// trueなら即終了
		}
		return match;
	}
	
	/* 全く同じWordを複製する */
	public Word copy() {
		Word replica = new Word();
		replica.setWord(wordName, tags, belongChunk, sbj_fnc);
		replica.originID = this.wordID;
		cloneIDs.add(replica.wordID);
		return replica;
	}
	
	/* 渡されたIDのリストを一つの文字列に変える */
	public static String toStringList(List<Integer> wordIDs) {
		String wordNames = new String();
		for(final int id: wordIDs) wordNames+=Word.get(id).wordName;
		return wordNames;
	}
	
	public static void printAllWords() {
		for(Word wd: allWordsList) {
			System.out.println("W"+wd.wordID + "@(C"+wd.belongChunk+"):\t" + wd.wordName + "("+wd.tags+")");
		}
	}
}
