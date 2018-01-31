package grammar;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import grammar.morpheme.Morpheme;
import util.UniqueSet;
import util.Uniqueness;

public class Concept implements GrammarInterface, Uniqueness<Concept> {
	private static UniqueSet<Concept> uniqueset = new UniqueSet<>(100);

	private final List<Morpheme> morphemes;	// 形態素たち
	
	

	public static final Concept ZEROCONCEPT = new Concept(new ArrayList<>());
	
	/***********************************/
	/**********  Constructor  **********/
	/***********************************/
	private Concept(List<Morpheme> morphemes) {
		this.morphemes = morphemes;
		
		uniqueset.add(this);
	}
	
	/**
	 * 同一の概念が存在していればそれを，なければ新しいインスタンスを作って返す.
	 * @param morphemes
	 * @return 渡された文字列と形態素に一致する概念
	 */
	public static Concept getOrNewInstance(List<Morpheme> morphemes) {
		Concept c = new Concept(morphemes);
		return uniqueset.getExistingOrIntact(c);
	}
	@SafeVarargs
	public static Concept getOrNewInstance(List<String>... tagLists) {
		List<Morpheme> morphemes = Stream.of(tagLists)
				.map(name_tags -> Morpheme.getOrNewInstance(name_tags))
				.collect(Collectors.toList());
		Concept c = new Concept(morphemes);
		return uniqueset.getExistingOrIntact(c);
	}
	
	
	
	/***********************************/
	/**********  MemberMethod **********/
	/***********************************/
	public boolean containsTag(String tag) {
		return getTailMorpheme().containsTag(tag);
	}

	public String infinitive() {
		return morphemes.stream().map(m -> m.infinitive()).collect(Collectors.joining());
	}
	
	/***********************************/
	/**********   Interface   **********/
	/***********************************/
	@Override	
	public String name() {
		return morphemes.stream().map(m -> m.name()).collect(Collectors.joining());
	}
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