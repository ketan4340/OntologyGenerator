package grammar;

import java.util.*;

public class Word implements GrammarInterface{
	public static int wordsSum = 0;
	public static List<Word> allWordList = new ArrayList<Word>();

	public final int id;				// 通し番号。Wordを特定する
	public String name;				// 単語の文字列
	public List<String> tags;		// 品詞・活用形、読みなど
	public boolean isCategorem;		// 自立語か付属語か
	
	public Clause comeUnder;			// どのClauseに所属するか

	private Word() {
		id = wordsSum++;
		allWordList.add(this);
	}
	public Word(String name, List<String> tags, Clause belongClause, boolean isCategorem) {
		this();
		this.name = name;
		this.tags = new ArrayList<>(tags);
		this.comeUnder = belongClause;
		this.isCategorem = isCategorem;
		supplementTags();
	}
	public Word(String name, List<String> tags) {
		this(name, tags, null, false);
	}
	
	public void setWord(String name, List<String> tags, Clause belongClause, boolean isCategorem) {
		this.name = name;
		this.tags = new ArrayList<>(tags);
		this.comeUnder = belongClause;
		this.isCategorem = isCategorem;
		supplementTags();
	}
	public void setIsCategorem(boolean ctgrm_adjnc) {
		this.isCategorem = ctgrm_adjnc;
	}
	private void supplementTags() {
//		printDetail();
		while (tags.size() < 9) {
			tags.add("*");	// tagの数が最低9個になるように
		}
		if(tags.get(6).equals("*")) {	// 特殊漢字の原形をここで入力してあげる
			tags.set(6, name);
		}
		if(tags.contains("記号")) isCategorem = false;	// 記号なら主辞とはしない
	}

	public static Word get(int id) {
		if(id < 0) return null;
		return allWordList.get(id);
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
	/* 渡されたTagをどれか"1つでも"持って入れば真、それ以外は偽を返す */
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
		replica.setWord(name, tags, comeUnder, isCategorem);
		return replica;
	}

	@Override
	public String toString() {
		return name;
	}
	@Override
	public void printDetail() {
		System.out.println(name);
	}

	/* 渡されたIDのリストを一つの文字列に変える */
	public static String toStringList(List<Integer> wordIDs) {
		String wordNames = new String();
		for(final int id: wordIDs) wordNames+=Word.get(id).name;
		return wordNames;
	}
}
