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
	/** 活用型 */
	private final String conjugation;
	/** 活用形 */
	private final String inflection;
	/** 原形 (半角文字ではデフォルトで"*") */
	private final String infinitive;
	/** 読み (半角文字ではもともとない) */
	private final String yomi;
	/** 発音 (半角文字ではもともとない) */
	private final String pronunciation;


	/* ================================================== */
	/* ==========          Constructor         ========== */
	/* ================================================== */
	protected CabochaTags(String mainPoS, String subPoS1, String subPoS2, String subPoS3,
			String conjugation, String inflection, String infinitive, String yomi, String pronunciation) {
		this.mainPoS 		= mainPoS;
		this.subPoS1 		= subPoS1;
		this.subPoS2 		= subPoS2;
		this.subPoS3 		= subPoS3;
		this.conjugation 	= conjugation;
		this.inflection 	= inflection;
		this.infinitive 	= infinitive;
		this.yomi 			= yomi;
		this.pronunciation 	= pronunciation;
	}

	/* ===== Factory Method ===== */
	public static CabochaTags getInstance(String mainPoS, String subPoS1, String subPoS2, String subPoS3,
			String conjugation, String inflection, String infinitive, String yomi, String pronunciation) {
		return TagsFactory.intern(mainPoS, subPoS1, subPoS2, subPoS3, conjugation, inflection, infinitive, yomi, pronunciation);
	}
	@Override
	public Object[] initArgs() {
		return new Object[] {mainPoS, subPoS1, subPoS2, subPoS3, conjugation, inflection, infinitive, yomi, pronunciation};
	}


	/* ================================================== */
	/* ==========        Member  Method        ========== */
	/* ================================================== */
	public List<String> toList() {
		return Arrays.asList(mainPoS, subPoS1, subPoS2, subPoS3,
				conjugation, inflection, infinitive, yomi, pronunciation);
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
	public String conjugation() {
		return conjugation;
	}
	@Override
	public String inflection() {
		return inflection;
	}
	@Override
	public String infinitive() {
		return infinitive;
	}
	@Override
	public String yomi() {
		return yomi;
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
				conjugation, inflection, infinitive, yomi, pronunciation);
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
		if (!mainPoS.equals(other.mainPoS))
			return false;
		if (!subPoS1.equals(other.subPoS1))
			return false;
		if (!subPoS2.equals(other.subPoS2))
			return false;
		if (!subPoS3.equals(other.subPoS3))
			return false;
		if (!conjugation.equals(other.conjugation))
			return false;
		if (!inflection.equals(other.inflection))
			return false;
		if (!infinitive.equals(other.infinitive))
			return false;
		if (!yomi.equals(other.yomi))
			return false;
		if (!pronunciation.equals(other.pronunciation))
			return false;
		return true;
	}

}