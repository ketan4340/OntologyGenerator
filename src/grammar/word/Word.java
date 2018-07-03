package grammar.word;

import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import data.RDF.RDFconvertable;
import data.RDF.vocabulary.JASS;
import data.id.Identifiable;
import grammar.SyntacticChild;
import grammar.GrammarInterface;
import grammar.clause.Clause;
import grammar.concept.Concept;
import grammar.morpheme.Morpheme;
import grammar.morpheme.PartOfSpeechInterface;

public class Word 
	implements Identifiable, RDFconvertable, GrammarInterface, 
	PartOfSpeechInterface, SyntacticChild<Clause<? extends Word>>
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
	
	public boolean isLiteral() {
		//concept.
		return true;
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
	public int id() {
		return id;
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
	@Override
	public String getURI() {
		return JASS.uri+getClass().getSimpleName()+id();
	}
	@Override
	public Resource toRDF(Model model) {
		Resource morphemeNode = model.createList(concept.getMorphemes().stream()
				.map(m -> m.toRDF(model)).iterator());
		
		Resource wordResource = model.createResource(getURI())
				.addProperty(RDF.type, JASS.Word)
				.addProperty(JASS.means, concept.toRDF(model))		
				.addProperty(JASS.consistsOfMorphemes, morphemeNode)
				.addLiteral(JASS.name, name())
				.addLiteral(JASS.mainPoS, mainPoS())
				.addLiteral(JASS.subPoS1, subPoS1())
				.addLiteral(JASS.subPoS2, subPoS2())
				.addLiteral(JASS.subPoS3, subPoS3())
				.addLiteral(JASS.inflection, inflection())
				.addLiteral(JASS.conjugation, conjugation())
				.addLiteral(JASS.infinitive, infinitive())
				.addLiteral(JASS.kana, kana())
				.addLiteral(JASS.pronunsiation, pronunciation());
		return wordResource;
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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((concept == null) ? 0 : concept.hashCode());
		result = prime * result + id;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Word other = (Word) obj;
		if (concept == null) {
			if (other.concept != null)
				return false;
		} else if (!concept.equals(other.concept))
			return false;
		if (id != other.id)
			return false;
		return true;
	}
}