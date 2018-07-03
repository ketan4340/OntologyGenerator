package grammar.concept;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import data.RDF.RDFizable;
import data.RDF.vocabulary.GOO;
import data.RDF.vocabulary.JASS;
import grammar.GrammarInterface;
import grammar.morpheme.Morpheme;
import grammar.morpheme.PartOfSpeechInterface;
import util.uniqueSet.UniqueSet;
import util.uniqueSet.Uniqueness;

public class Concept implements GrammarInterface, Uniqueness<Concept>, PartOfSpeechInterface, RDFizable {
	private static final UniqueSet<Concept> CONCEPTS_UNIQUESET = new UniqueSet<>(100);
	public static final Concept EMPTY_CONCEPT = new Concept(Collections.emptyList());

	private final List<Morpheme> morphemes;

	/****************************************/
	/**********     Constructor    **********/
	/****************************************/
	private Concept(List<Morpheme> morphemes) {
		this.morphemes = morphemes;
		CONCEPTS_UNIQUESET.add(this);
	}

	/**
	 * 同一の概念が存在していればそれを，なければ新しいインスタンスを作って返す.
	 * @param morphemes 形態素のリストか配列
	 * @return 渡された文字列と形態素に一致する概念
	 */
	public static Concept getOrNewInstance(List<Morpheme> morphemes) {
		Concept c = new Concept(morphemes);
		return CONCEPTS_UNIQUESET.getExistingOrIntact(c);
	}
	public static Concept getOrNewInstance(Morpheme... morphemes) {
		Concept c = new Concept(Arrays.asList(morphemes));
		return CONCEPTS_UNIQUESET.getExistingOrIntact(c);
	}


	/****************************************/
	/**********   Member  Method   **********/
	/****************************************/
	public boolean containsTag(String tag) {
		return getTailMorpheme().containsTag(tag);
	}
	public boolean isComplex() {
		return morphemes.size() > 1;
	}

	/****************************************/
	/**********  Interface Method  **********/
	/****************************************/
	@Override
	public int compareTo(Concept o) {
		int comparison = 0;
		ListIterator<Morpheme> itr1 = morphemes.listIterator();
		ListIterator<Morpheme> itr2 = o.morphemes.listIterator();
		while (itr1.hasNext() && itr2.hasNext()) {
			comparison = itr1.next().compareTo(itr2.next());
			if (comparison != 0)
				return comparison;
		}
		return itr1.hasNext()? 1
				: itr2.hasNext()? -1
				: comparison; // =0
	}
	@Override
	public String name() {
		return morphemes.stream().map(Morpheme::name).collect(Collectors.joining());
	}
	@Override
	public String mainPoS() {
		return getTailMorpheme().mainPoS();
	}
	@Override
	public String subPoS1() {
		return getTailMorpheme().subPoS1();
	}
	@Override
	public String subPoS2() {
		return getTailMorpheme().subPoS2();
	}
	@Override
	public String subPoS3() {
		return getTailMorpheme().subPoS3();
	}
	@Override
	public String inflection() {
		return getTailMorpheme().inflection();
	}
	@Override
	public String conjugation() {
		return getTailMorpheme().conjugation();
	}
	@Override
	public String infinitive() {
		return morphemes.stream().map(m -> m.infinitive()).collect(Collectors.joining());
	}
	@Override
	public String kana() {
		return morphemes.stream().map(m -> m.kana()).collect(Collectors.joining());
	}
	@Override
	public String pronunciation() {
		return morphemes.stream().map(m -> m.pronunciation()).collect(Collectors.joining());
	}
	@Override
	public String getURI() {
		return GOO.uri + name();
	}
	@Override
	public Resource toRDF(Model model) {
		Resource conceptResource = model.createResource(getURI())
				.addProperty(RDF.type, JASS.Concept);
		return conceptResource;
	}

	/**********************************/
	/**********    Getter    **********/
	/**********************************/
	public List<Morpheme> getMorphemes() {
		return morphemes;
	}
	public Morpheme getHeadMorpheme() {
		return morphemes.get(0);
	}
	public Morpheme getTailMorpheme() {
		return morphemes.get(morphemes.size()-1);
	}

	/**********************************/
	/********** ObjectMethod **********/
	/**********************************/
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((morphemes == null) ? 0 : morphemes.hashCode());
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
		Concept other = (Concept) obj;
		if (morphemes == null) {
			if (other.morphemes != null)
				return false;
		} else if (!morphemes.equals(other.morphemes))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return morphemes.stream().map(m -> m.toString()).collect(Collectors.joining());
	}
}