package japaneseParse;

import java.util.*;

public class Word {
	public static int wordsSum = 0;
	public static List<Word> allWordsList = new ArrayList<Word>();
	
	public int wordID;			// 通し番号。Wordを特定する
	public String wordName;		// 単語の文字列
	public List<String> tags;	// 品詞・活用形、読みなど
	public int inChunk;			// どのChunkに所属するか
	//public Map<Integer,String> dependUpon; // 従属関係のMap<従属先, 関係性>
	//public Map<Integer,String> beDepended; // 被従属関係のMap<従属先, 関係性>
	
	public Word() {
		wordID = wordsSum++;
		allWordsList.add(this);
		wordName = new String();
		tags = new ArrayList<String>();
		inChunk = -1;
	}
	public void setWord(String nWordName, List<String> nWordTags, int chunkID) {
		wordName = nWordName;
		tags.addAll(nWordTags);
		inChunk = chunkID;
	}
	
	public static Word get(int id) {
		return allWordsList.get(id);
	}
	
	/* 渡されたTagを全て持って入れば真、それ以外は偽を返す */
	public boolean hasTags(String[] tagNames) {
		boolean match = true;
		for(String tag: tagNames) {
			if(tag.equals(".")) {	// .は任意のタグ
				return true;		// よって必ずtrue
			}
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
	
	/* 同じWordか確かめる */
	public boolean equalsW(Word wd) {
		if(wordID == wd.wordID) return true;
		else return false;
	}
	
	/* 渡されたIDのリストを文字列のリストに変える */
	public static List<String> toStringList(List<Integer> wordIDs) {
		List<String> wordNames = new ArrayList<String>();
		for(final int id: wordIDs) wordNames.add(Word.get(id).wordName);
		return wordNames;
	}
	
	public static void printAllWords() {
		for(Word wd: allWordsList) {
			System.out.println("W"+wd.wordID + "@(C"+wd.inChunk+"):\t" + wd.wordName + "("+wd.tags+")");
		}
	}
}
