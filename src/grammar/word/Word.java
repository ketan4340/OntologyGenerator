package grammar.word;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import grammar.Concept;
import grammar.GrammarInterface;
import grammar.Identifiable;
import grammar.clause.AbstractClause;
import grammar.morpheme.Morpheme;
import grammar.structure.SyntacticChild;
import grammar.structure.SyntacticComponent;

public class Word extends SyntacticComponent<AbstractClause<?>, Word> 
implements SyntacticChild, GrammarInterface, Identifiable{
	private static int wordsSum = 0;

	private final int id;
	protected Concept concept;

	private String name;				// 単語の文字列
	private List<String> tags;		// 品詞・活用形、読みなど
	public boolean isCategorem;		// 自立語か付属語か
	


	/***********************************/
	/**********  Constructor  **********/
	/***********************************/
	private Word() {
		super(null);		//TODO 子要素はないってことでいいでしょう
		this.id = wordsSum++;
	}	
	public Word(Concept concept, AbstractClause<?> parentClause) {
		this();
		this.concept = concept;
		setParent(parentClause);
	}
	public Word(Concept concept) {
		this(concept, null);
	}
	

	/**
	 * 渡されたTagを"全て"持って入れば真、それ以外は偽を返す
	 */
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
	
	/**
	 * 渡されたTagをどれか"1つでも"持って入れば真、それ以外は偽を返す
	 */
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
	public Word clone() {
		return new Word(this.concept);
	}
	
	public boolean isCategorem() {
		return isCategorem;
	}
	
	
	/***********************************/
	/**********   Interface   **********/
	/***********************************/
	public int getID() {
		return id;
	}
	public void printDetail() {
		System.out.println(id+":" + name);
	}
	@Override
	public List<Word> getConstituents() {
		return Arrays.asList(this);
	}
	@Override
	public <Ch extends SyntacticChild> void setConstituents(List<Ch> constituents) {}
	
	
	/***********************************/
	/********** Getter/Setter **********/
	/***********************************/
	public String getName() {
		return name;
	}
	public List<String> getTags() {
		return tags;
	}
	public Concept getConcept() {
		return concept;
	}
	public List<Morpheme> getMorphemes() {
		return concept.getMorphemes();
	}
	public void setConcept(Concept concept) {
		this.concept = concept;
	}
	

	/**********************************/
	/********** Objectメソッド **********/
	/**********************************/
	@Override
	public String toString() {
		return Objects.toString(concept, "nullConcept");
	}
}