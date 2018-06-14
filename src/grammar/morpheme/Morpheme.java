package grammar.morpheme;

import java.util.List;
import java.util.Objects;

import grammar.structure.GrammarInterface;
import util.uniqueSet.UniqueSet;
import util.uniqueSet.Uniqueness;

public class Morpheme implements GrammarInterface, Uniqueness<Morpheme>, PartOfSpeechInterface {
	private static UniqueSet<Morpheme> uniqueset = new UniqueSet<>(100);	// EnMorphemeの同名staticフィールドを隠蔽->もうしてない

	public final int id;			// 通し番号
	private final String name;	// 形態素の文字列
	private final Tags tags;		// 品詞リスト


	/***********************************/
	/**********  Constructor  **********/
	/***********************************/
	private Morpheme(String name, Tags tags) {
		this.id = uniqueset.size();
		this.name = name;
		this.tags = tags;

		Morpheme.uniqueset.add(this);
	}
	private Morpheme(String name, List<String> tagList) {
		this(name, new Tags(tagList));
	}
	private Morpheme(List<String> name_tags) {
		this(name_tags.get(0), name_tags.subList(1, name_tags.size()));
	}

	public static Morpheme getOrNewInstance(String name, Tags tags) {
		return uniqueset.getExistingOrIntact(new Morpheme(name, tags));
	}
	public static Morpheme getOrNewInstance(String name, List<String> tags) {
		return uniqueset.getExistingOrIntact(new Morpheme(name, tags));
	}
	public static Morpheme getOrNewInstance(List<String> name_tags) {
		return uniqueset.getExistingOrIntact(new Morpheme(name_tags));
	}



	/***********************************/
	/**********  MemberMethod **********/
	/***********************************/
	public boolean containsTag(String tag) {
		return tags.contains(tag);
	}



	/***********************************/
	/**********   Interface   **********/
	/***********************************/
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

	/**********************************/
	/**********    Getter    **********/
	/**********************************/
	public String getName() {
		return name;
	}
	public Tags getTags() {
		return tags;
	}
	/**********   準Getter   **********/
	public List<String> tags() {
		return tags.getTagList();
	}


	/**********************************/
	/********** ObjectMethod **********/
	/**********************************/
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