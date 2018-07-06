package grammar.morpheme;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import util.tuple.Tuple;

/** CaboCha準拠の品詞セット */
public class Tags extends Tuple implements Comparable<Tags>, CabochaPoSInterface {
	private static final long serialVersionUID = -3714579060521127807L;

	private static final HashSet<Tags> ALL_TAGS = new HashSet<>();
	public static final Tags EMPTY_TAGS = new Tags(Arrays.asList("", "", "", "", "", "", "empty", "empty", "empty"));

	public static final int MINIMUM_TAGS_SIZE = 7;
	public static final int MAXIMUM_TAGS_SIZE = 9;
	/** 品詞 */
	public static final int MAIN_PoS 		= 0;
	/** 品詞細分類1 */
	public static final int SUB_PoS1 		= 1;
	/** 品詞細分類2 */
	public static final int SUB_PoS2 		= 2;
	/** 品詞細分類3 */
	public static final int SUB_PoS3 		= 3;
	/** 活用形 */
	public static final int INFLECTION 		= 4;
	/** 活用型 */
	public static final int CONJUGATION 	= 5;
	/** 原形 (半角文字ではデフォルトで"*") */
	public static final int INFINITIVE 		= 6;
	/** 読み (半角文字ではもともとない) */
	public static final int KANA 			= 7;
	/** 発音 (半角文字ではもともとない) */
	public static final int PRONUNCIATION 	= 8;


	/* ================================================== */
	/* ==========          Constructor         ========== */
	/* ================================================== */
	private Tags(List<String> tagList) {
		super(tagList);
	}

	/* ===== Factory Method ===== */
	public static Tags getInstance(List<String> tagList, String infinitive) {
		if (tagList.size() < MINIMUM_TAGS_SIZE || MAXIMUM_TAGS_SIZE < tagList.size())
			return EMPTY_TAGS;
		if (tagList.size() < MAXIMUM_TAGS_SIZE) {	// sizeが7，または8．つまり半角文字
			tagList = Stream.concat(tagList.stream(), Stream.generate(()->infinitive))
					.limit(MAXIMUM_TAGS_SIZE)
					.collect(Collectors.toList());
			tagList.set(6, infinitive);
		}
		Tags tags = new Tags(tagList);
		return tags;
	}
	public static Tags getInstance(String mainPoS, String subPoS1, String subPoS2, String subPoS3,
			String inflection, String conjugation,
			String infinitive, String kana, String pronunciation) {
		List<String> taglist = Arrays.asList(mainPoS, subPoS1, subPoS2, subPoS3, inflection, conjugation, infinitive, kana, pronunciation);
		Tags tags = new Tags(taglist);
		return tags;
	}


	/* ================================================== */
	/* ==========       Interface Method       ========== */
	/* ================================================== */
	@Override
	public int compareTo(Tags o) {
		int comparison = 0;
		ListIterator<String> itr1 = listIterator();
		ListIterator<String> itr2 = o.listIterator();
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
	public String mainPoS() {
		return get(MAIN_PoS);
	}
	@Override
	public String subPoS1() {
		return get(SUB_PoS1);
	}
	@Override
	public String subPoS2() {
		return get(SUB_PoS2);
	}
	@Override
	public String subPoS3() {
		return get(SUB_PoS3);
	}
	@Override
	public String inflection() {
		return get(INFLECTION);
	}
	@Override
	public String conjugation() {
		return get(CONJUGATION);
	}
	@Override
	public String infinitive() {
		return get(INFINITIVE);
	}
	@Override
	public String kana() {
		return get(KANA);
	}
	@Override
	public String pronunciation() {
		return get(PRONUNCIATION);
	}

}