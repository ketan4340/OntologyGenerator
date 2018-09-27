package grammar.morpheme;

import java.util.Objects;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import data.RDF.rule.RDFizable;
import data.RDF.vocabulary.JASS;
import grammar.GrammarInterface;
import grammar.tags.CabochaPoSInterface;
import grammar.tags.CabochaTags;

public final class Morpheme implements GrammarInterface,
	CabochaPoSInterface, RDFizable, MorphemeFactory {
	private static int MORPHEME_SUM = 0;

	private final int id;		// 通し番号
	private final String name;	// 形態素の文字列
	private final CabochaTags tags;	// 品詞リスト

	/****************************************/
	/**********     Constructor    **********/
	/****************************************/
	Morpheme(String name, CabochaTags tags) {
		this.id = MORPHEME_SUM++;
		this.name = name;
		this.tags = tags;
	}
	public static Morpheme getInstance(String newname, CabochaTags newtags) {
		return MorphemeFactory.intern(newname, newtags);
	}
	@Override
	public Object[] initArgs() {
		return new Object[] {name, tags};
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
	//@Override
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
	public String yomi() {
		return tags.yomi();
	}
	@Override
	public String pronunciation() {
		return tags.pronunciation();
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
				.addLiteral(JASS.kana, yomi())
				.addLiteral(JASS.pronunsiation, pronunciation());
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