package grammar.word;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import data.RDF.RDFizable;
import data.RDF.vocabulary.JASS;
import data.id.Identifiable;
import grammar.GrammarInterface;
import grammar.SyntacticParent;
import grammar.morpheme.Morpheme;
import grammar.tags.CabochaPoSInterface;

public class Word extends SyntacticParent<Morpheme>
	implements Identifiable, RDFizable, GrammarInterface,
	CabochaPoSInterface
{
	private static int wordsSum = 0;

	private final int id;


	/***********************************/
	/**********  Constructor  **********/
	/***********************************/
	public Word(List<Morpheme> morphemes) {
		super(morphemes);
		this.id = wordsSum++;
	}
	public Word(Morpheme... morphemes) {
		this(Arrays.asList(morphemes));
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
			match = tail().containsTag(tag);
			match = not? !match : match;

			if (!match) break;	// falseなら即終了
		}
		return match;
	}

	/** 全く同じWordを複製する */
	@Override
	public Word clone() {
		return new Word(children);
	}


	/****************************************/
	/**********  Interface Method  **********/
	/****************************************/
	@Override
	public int id() {
		return id;
	}
	@Override
	public String name() {
		return children.stream().map(Morpheme::name).collect(Collectors.joining());
	}
	@Override
	public String mainPoS() {
		return tail().mainPoS();
	}
	@Override
	public String subPoS1() {
		return tail().subPoS1();
	}
	@Override
	public String subPoS2() {
		return tail().subPoS2();
	}
	@Override
	public String subPoS3() {
		return tail().subPoS3();
	}
	@Override
	public String inflection() {
		return tail().inflection();
	}
	@Override
	public String conjugation() {
		return tail().conjugation();
	}
	@Override
	public String infinitive() {
		return children.stream().map(m -> m.infinitive()).collect(Collectors.joining());
	}
	@Override
	public String kana() {
		return children.stream().map(m -> m.kana()).collect(Collectors.joining());
	}
	@Override
	public String pronunciation() {
		return children.stream().map(m -> m.pronunciation()).collect(Collectors.joining());
	}
	@Override
	public String getURI() {
		return JASS.uri+getClass().getSimpleName()+id();
	}
	@Override
	public Resource toRDF(Model model) {
		Resource morphemeNode = model.createList(children.stream()
				.map(m -> m.toRDF(model)).iterator());

		Resource wordResource = model.createResource(getURI())
				.addProperty(RDF.type, JASS.Word)
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
	/**********   Object  Method   **********/
	/****************************************/
	@Override
	public String toString() {
		return children.stream().map(m -> m.toString()).collect(Collectors.joining());
	}


}