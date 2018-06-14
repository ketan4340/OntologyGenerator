package grammar.word;

import java.util.List;

import grammar.Concept;
import grammar.clause.Clause;
import grammar.morpheme.Morpheme;
import grammar.morpheme.PartOfSpeechInterface;
import grammar.structure.Child;
import grammar.structure.GrammarInterface;

public class Word 
	implements GrammarInterface, PartOfSpeechInterface, 
	Child<Clause<? extends Word>>
{
	private static int wordsSum = 0;

	public final int id;
	
	/** 単語の親要素，文節. */
	protected Clause<?> parentClause;
	/** 事実上の子要素，概念. */
	protected Concept concept;

	
	/***********************************/
	/**********  Constructor  **********/
	/***********************************/
	public Word(Concept concept, Clause<?> parentClause) {
		this.id = wordsSum++;
		this.concept = concept;
		setParent(parentClause);
	}
	public Word(Concept concept) {
		this(concept, null);
	}


	/***********************************/
	/**********  MemberMethod **********/
	/***********************************/
	/**
	 * 渡されたTagを"全て"持って入れば真、それ以外は偽を返す
	 */
	public boolean hasTagAll(String[] tags) {
		boolean match = true;	// デフォがtrueなので空の配列は任意の品詞とみなされる
		for (String tag: tags) {
			boolean not = false;	// NOT検索用のフラグ
			if (tag.startsWith("-")) {	// Tag名の前に-をつけるとそのタグを含まない時にtrue
				not = true;
				tag = tag.substring(1);	// -を消しておく
			}
			match = concept.containsTag(tag);
			match = not? !match : match; 
			
			if (!match) break;	// falseなら即終了
		}
		return match;
	}

	/** 全く同じWordを複製する */
	@Override
	public Word clone() {
		return new Word(this.concept);
	}


	/****************************************/
	/**********  Interface Method  **********/
	/****************************************/
	@Override
	public Clause<? extends Word> getParent() {
		return parentClause;
	}
	@Override
	public void setParent(Clause<? extends Word> parent) {
		this.parentClause = parent;
	}
	@Override
	public String name() {
		return concept.name();
	}
	@Override
	public String mainPoS() {
		return concept.mainPoS();
	}
	@Override
	public String subPoS1() {
		return concept.subPoS1();
	}
	@Override
	public String subPoS2() {
		return concept.subPoS2();
	}
	@Override
	public String subPoS3() {
		return concept.subPoS3();
	}
	@Override
	public String inflection() {
		return concept.inflection();
	}
	@Override
	public String conjugation() {
		return concept.conjugation();
	}
	@Override
	public String infinitive() {
		return concept.infinitive();
	}
	@Override
	public String kana() {
		return concept.kana();
	}
	@Override
	public String pronunciation() {
		return concept.pronunciation();
	}

	/****************************************/
	/**********   Getter, Setter   **********/
	/****************************************/
	public int getID() {
		return id;
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


	/****************************************/
	/**********   Object  Method   **********/
	/****************************************/
	@Override
	public String toString() {
		return concept.toString();
	}
}