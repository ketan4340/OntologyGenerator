package grammar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Concept implements GrammarInterface{
	private static Map<String, Concept> allConcepts = new HashMap<>();

	private final int id;					// 通し番号。Conceptを特定する
	private final List<Morpheme> morphemes;	// 形態素たち
	
	public Concept(List<Morpheme> morphemes) {
		this.id = allConcepts.size();
		this.morphemes = morphemes;
		
		String keyword = morphemes.stream().map(m -> m.getName()).collect(Collectors.joining());
		allConcepts.put(keyword, this);
	}
	
	public static Concept getOrNewInstance(String keyword, List<Morpheme> morphemes) {
		return allConcepts.getOrDefault(keyword, new Concept(morphemes));
	}
	
	
	
	/**********************************/
	/**********    Getter    **********/
	/**********************************/
	public int getId() {
		return id;
	}
	public List<Morpheme> getMorphemes() {
		return morphemes;
	}
	
	
	
	/**********************************/
	/********** Objectメソッド **********/
	/**********************************/
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		if (id != other.id)
			return false;
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
	@Override
	public void printDetail() {
		System.out.println(toString());
	}
}