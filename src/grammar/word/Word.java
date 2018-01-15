package grammar.word;

import java.util.*;

import grammar.Concept;
import grammar.GrammarInterface;
import grammar.clause.Clause;

public class Word implements GrammarInterface{
	public static List<Word> allWordList = new ArrayList<Word>();

	private final int id;			// 通し番号。Wordを特定する
	private String name;				// 単語の文字列
	private List<String> tags;		// 品詞・活用形、読みなど
	public boolean isCategorem;		// 自立語か付属語か
	
	public Clause parentClause;		// どのClauseに所属するか
	
	private Concept concept;

	private Word() {
		id = allWordList.size();
		allWordList.add(this);
	}
	public Word(String name, List<String> tags, Clause parentClause, boolean isCategorem) {
		this();
		this.name = name;
		this.tags = new ArrayList<>(tags);
		this.parentClause = parentClause;
		this.isCategorem = isCategorem;
		supplementTags();
	}
	public Word(String name, List<String> tags) {
		this(name, tags, null, false);
	}
	
	/** 新型コンストラクタ */
	public Word(Concept concept, Clause belongClause) {
		this();
		this.concept = concept;
		this.parentClause = belongClause;
	}
	
	public void setWord(String name, List<String> tags, Clause parentClause, boolean isCategorem) {
		this.name = name;
		this.tags = new ArrayList<>(tags);
		this.parentClause = parentClause;
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
		replica.setWord(name, tags, parentClause, isCategorem);
		return replica;
	}
	
	
	public int getID() {
		return id;
	}
	public String getName() {
		return name;
	}
	public List<String> getTags() {
		return tags;
	}
	public boolean isCategorem() {
		return isCategorem;
	}
	public Clause getParentClause() {
		return parentClause;
	}
	public Concept getConcept() {
		return concept;
	}
	@Override
	public String toString() {
		return name;
	}
	@Override
	public void printDetail() {
		System.out.println(id+":" + name);
	}
}