package englishParse;

import java.util.*;

public class Word {
	public static int wordsSum = 0; 
	public static List<Word> allWordsList = new ArrayList<Word>();
		
	public int serialNum; // 通し番号。Wordを特定する要素
	public String wordName;
	public String nonTerminal; // 品詞(非終端記号)
	public Map<Integer,String> dependUpon; // 従属関係のMap<従属先, 関係性>
	public Map<Integer,String> beDepended; // 被従属関係のMap<従属先, 関係性>
	
	public Word() {
		serialNum = wordsSum++;
		allWordsList.add(this);
		wordName = new String();
		nonTerminal = new String();
		dependUpon = new HashMap<Integer, String>();
		beDepended = new HashMap<Integer, String>();
	}
	
	public void setWord(String nWordName, String nWordTag) {
		wordName = nWordName;
		nonTerminal = nWordTag;
	}
	
	public static Word get(int sn) {
		return allWordsList.get(sn);
	}
	
	// 通し番号fromに関する従属・被従属関係をtoのものにする
	public static void replaceDependence(int from, int to) {
		for(Word wd: allWordsList) {
			for(Iterator<Integer> itr = wd.dependUpon.keySet().iterator(); itr.hasNext();) {
				Integer i = itr.next();
				if(i == from) {
					wd.dependUpon.replace(to, wd.dependUpon.get(from));
				}
			}
			for(Iterator<Integer> itr = wd.beDepended.keySet().iterator(); itr.hasNext();) {
				Integer i = itr.next();
				if(i == from) {
					wd.beDepended.replace(to, wd.beDepended.get(from));	
				}
			}
		}
	}
	
	public void copyDependency(int src) {
		// 自インスタンスが持つ従属関係はsrcからそのままコピー
		dependUpon.clear();
		dependUpon.putAll(Word.get(src).dependUpon);
		beDepended.clear();
		beDepended.putAll(Word.get(src).beDepended);
		// 他インスタンスが持つ従属関係にはsrcと同じ関係を追加
		for(Word wd: allWordsList) {
			if(wd.dependUpon.containsKey(src)) {
				wd.dependUpon.put(serialNum, wd.dependUpon.get(src));
			}
			if(wd.beDepended.containsKey(src)) {
				wd.beDepended.put(serialNum, wd.beDepended.get(src));
			}
		}
	}
	
	public boolean equalsWord(Word wd) {
		if(serialNum == wd.serialNum) {
			return true;
		}else {
			return false;
		}
	}
	
	/* 引数のWordと従属関係にあればその関係性を関係がなければnullを返す */
	public String hasRelation(int sn) {
		if(dependUpon.containsKey(Word.get(sn).serialNum)) {
			return dependUpon.get(sn);
		}else if(beDepended.containsKey(Word.get(sn).serialNum)) {
			return beDepended.get(sn);
		}else {
			return null;
		}
	}
	
	public static void printAllWords() {
		for(Word wd: allWordsList) {
			System.out.println(wd.serialNum + "\t: " + wd.wordName);
		}
	}
}
