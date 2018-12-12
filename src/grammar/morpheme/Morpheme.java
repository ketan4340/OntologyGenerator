package grammar.morpheme;

import java.util.Collection;
import java.util.Objects;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import data.RDF.vocabulary.JASS;
import dic.Immutable;
import grammar.GrammarInterface;
import grammar.SyntacticChild;
import language.pos.CabochaPoSInterface;
import language.pos.CabochaTags;
import language.pos.Concatable;

public final class Morpheme implements SyntacticChild, GrammarInterface,
	CabochaPoSInterface, Immutable, Concatable<Morpheme> {
	private static int SUM = 0;

	private final int id;			// 通し番号
	private final String name;		// 形態素の文字列
	private final CabochaTags tags;	// 品詞

	/* ================================================== */
	/* =================== Constructor ================== */
	/* ================================================== */
	Morpheme(String name, CabochaTags tags) {
		this.id = SUM++;
		this.name = name;
		this.tags = tags;
	}


	/* ================================================== */
	/* ================== Member Method ================= */
	/* ================================================== */

	/* ================================================== */
	/* ================ Interface Method ================ */
	/* ================================================== */
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
	public boolean contains(String pos) {
		return tags.contains(pos);
	}
	@Override
	public boolean containsAll(Collection<String> poss) {
		return tags.containsAll(poss);
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
	
	@Override
	public Morpheme concat(Morpheme other) {
		String newname = this.name + other.name;
		CabochaTags newtags = this.tags.concat(other.tags);
		return MorphemeFactory.getInstance().getMorpheme(newname, newtags);
	}

	
	/* ================================================== */
	/* ================== Object Method ================= */
	/* ================================================== */
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
