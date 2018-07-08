package grammar.tags;

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

public final class CabochaTags implements Comparable<CabochaTags>, CabochaPoSInterface, TagsFactory {
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
	CabochaTags(String mainPoS, String subPoS1, String subPoS2, String subPoS3,
			String inflection, String conjugation, String infinitive, String kana, String pronunciation) {
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
	public static CabochaTags getInstance(String mainPoS, String subPoS1, String subPoS2, String subPoS3,
			String inflection, String conjugation, String infinitive, String kana, String pronunciation) {
		return TagsFactory.intern(mainPoS, subPoS1, subPoS2, subPoS3, inflection, conjugation, infinitive, kana, pronunciation);
	}
	@Override
	public Object[] initArgs() {
		return new Object[] {mainPoS, subPoS1, subPoS2, subPoS3, inflection, conjugation, infinitive, kana, pronunciation};
	}


	/* ================================================== */
	/* ==========        Member  Method        ========== */
	/* ================================================== */
	public List<String> toList() {
		return Arrays.asList(mainPoS, subPoS1, subPoS2, subPoS3,
				inflection, conjugation, infinitive, kana, pronunciation);
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



	/* ================================================== */
	/* ==========        Object  Method        ========== */
	/* ================================================== */
	@Override
	public int hashCode() {
		return Objects.hash(mainPoS, subPoS1, subPoS2, subPoS3,
				inflection, conjugation, infinitive, kana, pronunciation);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CabochaTags other = (CabochaTags) obj;
		if (!conjugation.equals(other.conjugation))
			return false;
		if (!infinitive.equals(other.infinitive))
			return false;
		if (!inflection.equals(other.inflection))
			return false;
		if (!kana.equals(other.kana))
			return false;
		if (!mainPoS.equals(other.mainPoS))
			return false;
		if (!pronunciation.equals(other.pronunciation))
			return false;
		if (!subPoS1.equals(other.subPoS1))
			return false;
		if (!subPoS2.equals(other.subPoS2))
			return false;
		if (!subPoS3.equals(other.subPoS3))
			return false;
		return true;
	}

}