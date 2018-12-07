package grammar.morpheme;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import data.RDF.vocabulary.JASS;
import dic.Immutable;
import grammar.GrammarInterface;
import grammar.SyntacticChild;
import pos.CabochaPoSInterface;
import pos.CabochaTags;

public final class Morpheme implements SyntacticChild, GrammarInterface,
	CabochaPoSInterface, Immutable {
	private static int MORPHEME_SUM = 0;

	private final int id;			// 通し番号
	private final String name;		// 形態素の文字列
	private final CabochaTags tags;	// 品詞

	/****************************************/
	/**********     Constructor    **********/
	/****************************************/
	Morpheme(String name, CabochaTags tags) {
		this.id = MORPHEME_SUM++;
		this.name = name;
		this.tags = tags;
	}


	/* ================================================== */
	/* ================== Static Method ================= */
	/* ================================================== */
	public static Morpheme concat(List<Morpheme> morphemes) {
		String newname = morphemes.stream()
				.map(Morpheme::name)
				.collect(Collectors.joining());
		CabochaTags newtags = morphemes.stream()
				.map(Morpheme::getTags)
				.reduce(CabochaTags.EMPTY_TAGS, (c1, c2) -> CabochaTags.concat(c1, c2));
		return MorphemeFactory.getInstance().getMorpheme(newname, newtags);
	}

	/****************************************/
	/**********   Member  Method   **********/
	/****************************************/
	public boolean containsTag(String tag) {
		return tags.contains(tag);
	}

	public CabochaTags getTags() {
		return tags;
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
	public String conjugation() {
		return tags.conjugation();
	}
	@Override
	public String inflection() {
		return tags.inflection();
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
	public Resource toJASS(Model model) {
		return model.createResource(getJassURI())
				.addProperty(RDF.type, JASS.Morpheme)
				.addLiteral(JASS.name, name())
				.addLiteral(JASS.mainPoS, mainPoS())
				.addLiteral(JASS.subPoS1, subPoS1())
				.addLiteral(JASS.subPoS2, subPoS2())
				.addLiteral(JASS.subPoS3, subPoS3())
				.addLiteral(JASS.conjugation, conjugation())
				.addLiteral(JASS.inflection, inflection())
				.addLiteral(JASS.infinitive, infinitive())
				.addLiteral(JASS.kana, yomi())
				.addLiteral(JASS.pronunsiation, pronunciation());
	}
	@Override
	public Object[] initArgs() {
		return new Object[] {name, tags};
	}


	/****************************************/
	/**********   Object  Method   **********/
	/****************************************/
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Objects.hashCode(name);
		result = prime * result + Objects.hashCode(tags);
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (! (obj instanceof Morpheme))
			return false;
		Morpheme other = (Morpheme) obj;
		return Objects.equals(this.name, other.name)
				&& Objects.equals(this.tags, other.tags);
	}
	
	@Override
	public String toString() {
		return Objects.toString(name, "nullMorpheme");
	}

}