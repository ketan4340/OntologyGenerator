package grammar;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Morpheme implements GrammarInterface{
	public static Set<Morpheme> allMorphemes = new HashSet<Morpheme>();

	private final int id;			// 通し番号。Morphemeを特定する
	private String name;				// 形態素の文字列
	
	private String partOfSpeech;		// 品詞
	private String subPoS1;			// 品詞細分類1
	private String subPoS2;			// 品詞細分類2
	private String subPoS3;			// 品詞細分類3
	private String inflection;		// 活用形
	private String conjugation;		// 活用型
	private String originalForm;		// 原形 (半角文字にはない)
	private String kana;				// 読み (半角文字にはない)
	private String pronunciation;	// 発音 (半角文字にはない)
	
	
	private Morpheme() {
		id = allMorphemes.size();
		allMorphemes.add(this);
	}
	public Morpheme(String name, String partOfSpeech, String subPoS1, String subPoS2, String subPoS3, String inflection,
			String conjugation, String originalForm, String kana, String pronunciation) {
		this();
		this.name = name;
		this.partOfSpeech = partOfSpeech;
		this.subPoS1 = subPoS1;
		this.subPoS2 = subPoS2;
		this.subPoS3 = subPoS3;
		this.inflection = inflection;
		this.conjugation = conjugation;
		this.originalForm = originalForm;
		this.kana = kana;
		this.pronunciation = pronunciation;
	}
	public Morpheme(String name, List<String> tags) {
		this(name, tags.get(0), tags.get(1), tags.get(2), tags.get(3), tags.get(4), tags.get(5), 
				tags.size() > 6 ? tags.get(6) : name, tags.size() > 7 ? tags.get(7) : name, tags.size() > 8 ? tags.get(8) : name);
	}
	
	
	/* Getter */
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getPartOfSpeech() {
		return partOfSpeech;
	}
	public String getSubPoS1() {
		return subPoS1;
	}
	public String getSubPoS2() {
		return subPoS2;
	}
	public String getSubPoS3() {
		return subPoS3;
	}
	public String getInflection() {
		return inflection;
	}
	public String getConjugation() {
		return conjugation;
	}
	public String getOriginalForm() {
		return originalForm;
	}
	public String getKana() {
		return kana;
	}
	public String getPronunciation() {
		return pronunciation;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((subPoS1 == null) ? 0 : subPoS1.hashCode());
		result = prime * result + ((subPoS2 == null) ? 0 : subPoS2.hashCode());
		result = prime * result + ((subPoS3 == null) ? 0 : subPoS3.hashCode());
		result = prime * result + ((conjugation == null) ? 0 : conjugation.hashCode());
		result = prime * result + id;
		result = prime * result + ((inflection == null) ? 0 : inflection.hashCode());
		result = prime * result + ((kana == null) ? 0 : kana.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((originalForm == null) ? 0 : originalForm.hashCode());
		result = prime * result + ((partOfSpeech == null) ? 0 : partOfSpeech.hashCode());
		result = prime * result + ((pronunciation == null) ? 0 : pronunciation.hashCode());
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
		if (subPoS1 == null) {
			if (other.subPoS1 != null)
				return false;
		} else if (!subPoS1.equals(other.subPoS1))
			return false;
		if (subPoS2 == null) {
			if (other.subPoS2 != null)
				return false;
		} else if (!subPoS2.equals(other.subPoS2))
			return false;
		if (subPoS3 == null) {
			if (other.subPoS3 != null)
				return false;
		} else if (!subPoS3.equals(other.subPoS3))
			return false;
		if (conjugation == null) {
			if (other.conjugation != null)
				return false;
		} else if (!conjugation.equals(other.conjugation))
			return false;
		if (id != other.id)
			return false;
		if (inflection == null) {
			if (other.inflection != null)
				return false;
		} else if (!inflection.equals(other.inflection))
			return false;
		if (kana == null) {
			if (other.kana != null)
				return false;
		} else if (!kana.equals(other.kana))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (originalForm == null) {
			if (other.originalForm != null)
				return false;
		} else if (!originalForm.equals(other.originalForm))
			return false;
		if (partOfSpeech == null) {
			if (other.partOfSpeech != null)
				return false;
		} else if (!partOfSpeech.equals(other.partOfSpeech))
			return false;
		if (pronunciation == null) {
			if (other.pronunciation != null)
				return false;
		} else if (!pronunciation.equals(other.pronunciation))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return name;
	}
	@Override
	public void printDetail() {
		System.out.println(name);
	}
}