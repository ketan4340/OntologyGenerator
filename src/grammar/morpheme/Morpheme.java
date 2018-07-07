package grammar.morpheme;

import java.util.Objects;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import data.RDF.RDFizable;
import data.RDF.vocabulary.JASS;
import data.id.Identifiable;
import grammar.GrammarInterface;
import grammar.tags.CabochaPoSInterface;
import grammar.tags.CabochaTags;
import util.uniqueSet.UniqueSet;
import util.uniqueSet.Uniqueness;

public class Morpheme implements GrammarInterface, Uniqueness<Morpheme>,
CabochaPoSInterface, Identifiable, RDFizable {
	private static UniqueSet<Morpheme> MORPHEMES_UNIQUESET = new UniqueSet<>(100);	// EnMorphemeの同名staticフィールドを隠蔽->もうしてない

	public final int id;		// 通し番号
	private final String name;	// 形態素の文字列
	private final CabochaTags tags;	// 品詞リスト

	/****************************************/
	/**********     Constructor    **********/
	/****************************************/
	private Morpheme(String name, CabochaTags tags) {
		this.id = MORPHEMES_UNIQUESET.size();
		this.name = name;
		this.tags = tags;

		Morpheme.MORPHEMES_UNIQUESET.add(this);
	}
	public static Morpheme getOrNewInstance(String name, CabochaTags tags) {
		return MORPHEMES_UNIQUESET.getExistingOrIntact(new Morpheme(name, tags));
	}


	/****************************************/
	/**********   Member  Method   **********/
	/****************************************/
	public boolean containsTag(String tag) {
		return tags.contains(tag);
	}


	/****************************************/
	/**********  Interface Method  **********/
	/****************************************/
	@Override
	public int id() {
		return id;
	}
	@Override
	public int compareTo(Morpheme o) {
		int comparison = name.compareTo(o.name);
		return comparison!=0? comparison : tags.compareTo(o.tags);
	}
	@Override
	public String name() {
		return name;
	}
	@Override
	public String mainPoS() {
		return tags.mainPoS();
	}
	@Override
	public String subPoS1() {
		return tags.subPoS1();
	}
	@Override
	public String subPoS2() {
		return tags.subPoS2();
	}
	@Override
	public String subPoS3() {
		return tags.subPoS3();
	}
	@Override
	public String inflection() {
		return tags.inflection();
	}
	@Override
	public String conjugation() {
		return tags.conjugation();
	}
	@Override
	public String infinitive() {
		return tags.infinitive();
	}
	@Override
	public String kana() {
		return tags.kana();
	}
	@Override
	public String pronunciation() {
		return tags.pronunciation();
	}
	@Override
	public String getURI() {
		return JASS.uri + getClass().getSimpleName() + id();
	}
	@Override
	public Resource toRDF(Model model) {
		return model.createResource(getURI())
				.addProperty(RDF.type, JASS.Morpheme)
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
	}

	/****************************************/
	/**********       Getter       **********/
	/****************************************/
	public String getName() {
		return name;
	}
	public CabochaTags getTags() {
		return tags;
	}


	/****************************************/
	/**********   Object  Method   **********/
	/****************************************/
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((tags == null) ? 0 : tags.hashCode());
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
		Morpheme other = (Morpheme) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (tags == null) {
			if (other.tags != null)
				return false;
		} else if (!tags.equals(other.tags))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return Objects.toString(name, "nullMorpheme");
	}
}