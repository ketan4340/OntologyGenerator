package grammar.morpheme;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class CabochaTags implements Comparable<CabochaTags>, CabochaPoSInterface {
	private static final HashMap<String, HashSet<CabochaTags>> ALL_TAGS = new HashMap<>();
	public static final CabochaTags EMPTY_TAGS = new CabochaTags("", "", "", "", "", "", "empty", "empty", "empty");


	/** 品詞 */
	private final String mainPoS;
	/** 品詞細分類1 */
	private final String subPoS1;
	/** 品詞細分類2 */
	private final String subPoS2;
	/** 品詞細分類3 */
	private final String subPoS3;
	/** 活用形 */
	private final String inflection;
	/** 活用型 */
	private final String conjugation;
	/** 原形 (半角文字ではデフォルトで"*") */
	private final String infinitive;
	/** 読み (半角文字ではもともとない) */
	private final String kana;
	/** 発音 (半角文字ではもともとない) */
	private final String pronunciation;

	/* ================================================== */
	/* ==========          Constructor         ========== */
	/* ================================================== */
	private CabochaTags(String mainPoS, String subPoS1, String subPoS2, String subPoS3, String inflection,
			String conjugation, String infinitive, String kana, String pronunciation) {
		this.mainPoS = mainPoS;
		this.subPoS1 = subPoS1;
		this.subPoS2 = subPoS2;
		this.subPoS3 = subPoS3;
		this.inflection = inflection;
		this.conjugation = conjugation;
		this.infinitive = infinitive;
		this.kana = kana;
		this.pronunciation = pronunciation;
	}

	/* ===== Factory Method ===== */
	public static CabochaTags getInstance(String mainPoS, String subPoS1, String subPoS2, String subPoS3, String inflection,
			String conjugation, String infinitive, String kana, String pronunciation) {
		Optional<CabochaTags> optionalTags = getEquivalentTags(mainPoS, subPoS1, subPoS2, subPoS3, inflection, conjugation, infinitive, kana, pronunciation);
		CabochaTags tags = optionalTags.orElseGet(()->new CabochaTags(mainPoS, subPoS1, subPoS2, subPoS3, inflection, conjugation, infinitive, kana, pronunciation));
		ALL_TAGS.get(tags.mainPoS + tags.subPoS1).add(tags);
		return tags;
	}
	public static CabochaTags getInstance(String[] tagArray, String infinitive) {
		if (tagArray.length < MINIMUM_TAGS_SIZE || MAXIMUM_TAGS_SIZE < tagArray.length)
			return EMPTY_TAGS;
		if (tagArray.length < MAXIMUM_TAGS_SIZE) {	// sizeが7，または8．つまり半角文字
			tagArray = Stream.concat(Stream.of(tagArray), Stream.generate(()->infinitive))
					.limit(MAXIMUM_TAGS_SIZE)
					.toArray(String[]::new);
			tagArray[6] = infinitive;
		}
		CabochaTags tags = getInstance(tagArray[0], tagArray[1], tagArray[2], tagArray[3], tagArray[4], tagArray[5], tagArray[6], tagArray[7], tagArray[8]);
		return tags;
	}
	private static Optional<CabochaTags> getEquivalentTags(String mainPoS, String subPoS1, String subPoS2, String subPoS3, String inflection,
			String conjugation, String infinitive, String kana, String pronunciation) {
		String key = mainPoS + subPoS1;
		Optional<Set<CabochaTags>> tagsset = Optional.ofNullable(ALL_TAGS.get(key));
		if (tagsset.isPresent()) {
			for (CabochaTags tags : tagsset.get()) {
				List<String> newTaglist = Arrays.asList(mainPoS, subPoS1, subPoS2, subPoS3, inflection, conjugation, infinitive, kana, pronunciation);
				List<String> taglist = tags.toList();
				if (newTaglist.equals(taglist))
					return Optional.of(tags);
			}
		} else {
			ALL_TAGS.put(key, new HashSet<>());
		}
		return Optional.empty();
	}


	/* ================================================== */
	/* ==========        Member  Method        ========== */
	/* ================================================== */
	public List<String> toList() {
		return Arrays.asList(mainPoS, subPoS1, subPoS2, subPoS3, inflection, conjugation, infinitive,
				kana, pronunciation);
	}

	public boolean contains(Object o) {
		return toList().contains(o);
	}

	/* ================================================== */
	/* ==========       Interface Method       ========== */
	/* ================================================== */
	@Override
	public int compareTo(CabochaTags o) {
		int comparison = 0;
		ListIterator<String> itr1 = toList().listIterator();
		ListIterator<String> itr2 = o.toList().listIterator();
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
		return mainPoS;
	}
	@Override
	public String subPoS1() {
		return subPoS1;
	}
	@Override
	public String subPoS2() {
		return subPoS2;
	}
	@Override
	public String subPoS3() {
		return subPoS3;
	}
	@Override
	public String inflection() {
		return inflection;
	}
	@Override
	public String conjugation() {
		return conjugation;
	}
	@Override
	public String infinitive() {
		return infinitive;
	}
	@Override
	public String kana() {
		return kana;
	}
	@Override
	public String pronunciation() {
		return pronunciation;
	}

}