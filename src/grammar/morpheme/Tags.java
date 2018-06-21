package grammar.morpheme;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** CaboCha準拠の品詞セット */
public class Tags implements Comparable<Tags>, PartOfSpeechInterface {
	public static final Tags EMPTY_TAGS = new Tags("", "", "", "", "", "", "empty", "empty", "epmty"); 
	public static final int MINIMUM_TAGS_SIZE = 7;
	public static final int MAXIMUM_TAGS_SIZE = 9;
	/** 品詞 */
	public static final int MAIN_PoS = 0;
	/** 品詞細分類1 */
	public static final int SUB_PoS1 = 1;
	/** 品詞細分類2 */
	public static final int SUB_PoS2 = 2;
	/** 品詞細分類3 */
	public static final int SUB_PoS3 = 3;
	/** 活用形 */
	public static final int INFLECTION = 4;
	/** 活用型 */
	public static final int CONJUGATION = 5;
	/** 原形 (半角文字ではデフォルトで"*") */
	public static final int INFINITIVE = 6;
	/** 読み (半角文字ではもともとない) */
	public static final int KANA = 7;
	/** 発音 (半角文字ではもともとない) */
	public static final int PRONUNCIATION = 8;

	private final List<String> tagList;


	/****************************************/
	/**********     Constructor    **********/
	/****************************************/
	private Tags(List<String> tagList) {
		this.tagList = tagList;
	}
	public Tags(String mainPoS, String subPoS1, String subPoS2, String subPoS3,
			String inflection, String conjugation,
			String infinitive, String kana, String pronunciation) {
		this(new ArrayList<>(Arrays.asList(mainPoS, subPoS1, subPoS2, subPoS3,
				inflection, conjugation, infinitive, kana, pronunciation)));
	}
	
	public static Optional<Tags> of(List<String> tagList, String name) {
		if (tagList.size() < MINIMUM_TAGS_SIZE || MAXIMUM_TAGS_SIZE < tagList.size())
			return null;
		if (tagList.size() < MAXIMUM_TAGS_SIZE) {	// sizeが7,8
			tagList = Stream.concat(tagList.stream(), Stream.generate(()->name))
					.limit(MAXIMUM_TAGS_SIZE)
					.collect(Collectors.toList());
			tagList.set(6, name);
		}
		return Optional.ofNullable(new Tags(tagList));
	}


	/****************************************/
	/**********   Member  Method   **********/
	/****************************************/
	public boolean contains(String tag) {
		return tagList.contains(tag);
	}

	/****************************************/
	/**********  Interface Method  **********/
	/****************************************/
	@Override
	public int compareTo(Tags o) {
		int comparison = 0;
		ListIterator<String> itr1 = tagList.listIterator();
		ListIterator<String> itr2 = o.tagList.listIterator();
		while (itr1.hasNext() && itr2.hasNext()) {
			comparison = itr1.next().compareTo(itr2.next());
			if (comparison != 0)
				return comparison;
		}
		return itr1.hasNext()? 1
				: itr2.hasNext()? -1
				: comparison;
	}
	@Override
	public String name() {
		return infinitive();
	}
	@Override
	public String mainPoS() {
		return tagList.get(MAIN_PoS);
	}
	@Override
	public String subPoS1() {
		return tagList.get(SUB_PoS1);
	}
	@Override
	public String subPoS2() {
		return tagList.get(SUB_PoS2);
	}
	@Override
	public String subPoS3() {
		return tagList.get(SUB_PoS3);
	}
	@Override
	public String inflection() {
		return tagList.get(INFLECTION);
	}
	@Override
	public String conjugation() {
		return tagList.get(CONJUGATION);
	}
	@Override
	public String infinitive() {
		return tagList.get(INFINITIVE);
	}
	@Override
	public String kana() {
		return tagList.get(KANA);
	}
	@Override
	public String pronunciation() {
		return tagList.get(PRONUNCIATION);
	}


	/****************************************/
	/**********       Getter       **********/
	/****************************************/
	public List<String> getTagList() {
		return tagList;
	}


	/****************************************/
	/**********   Object  Method   **********/
	/****************************************/
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tagList == null) ? 0 : tagList.hashCode());
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
		Tags other = (Tags) obj;
		if (tagList == null) {
			if (other.tagList != null)
				return false;
		} else if (!tagList.equals(other.tagList))
			return false;
		return true;
	}
}