package grammar.morpheme;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

public class Tags implements Comparable<Tags>{
	/* CaboCha準拠の品詞 */
	public static final int MAINPoS = 0;			// 品詞
	public static final int SUBPoS1 = 1;			// 品詞細分類1
	public static final int SUBPoS2 = 2;			// 品詞細分類2
	public static final int SUBPoS3 = 3;			// 品詞細分類3
	public static final int INFLECTION = 4;		// 活用形
	public static final int CONJUGATION = 5;		// 活用型
	public static final int LEXEME = 6;			// 原形 (半角文字にはない)
	public static final int KANA = 7;			// 読み (半角文字にはない)
	public static final int PRONUNCIATION = 8;	// 発音 (半角文字にはない)
	
	
	private final List<String> tagList;
	

	/***********************************/
	/**********  Constructor  **********/
	/***********************************/
	public Tags(List<String> tagList) {
		this.tagList = new ArrayList<>(tagList);
	}
	public Tags(String mainPoS, String subPoS1, String subPoS2, String subPoS3,
			String inflection, String conjugation) {
		this.tagList = Arrays.asList(
				mainPoS, subPoS1, subPoS2, subPoS3, 
				inflection, conjugation);
	}
	public Tags(String mainPoS, String subPoS1, String subPoS2, String subPoS3,
			String inflection, String conjugation, 
			String lexeme, String kana, String pronunciation) {
		this.tagList = Arrays.asList(
				mainPoS, subPoS1, subPoS2, subPoS3, 
				inflection, conjugation, 
				lexeme, kana, pronunciation);
	}
	

	/***********************************/
	/**********   Interface   **********/
	/***********************************/
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
	
	/**********************************/
	/**********    Getter    **********/
	/**********************************/
	/**********   準Getter   **********/
	public List<String> getTagList() {
		return tagList;
	}
	protected String mainPoS() {
		return tagList.get(MAINPoS);
	}
	protected String subPoS1() {
		return tagList.get(SUBPoS1);
	}
	protected String subPoS2() {
		return tagList.get(SUBPoS2);
	}
	protected String subPoS3() {
		return tagList.get(SUBPoS3);
	}
	protected String inflection() {
		return tagList.get(INFLECTION);
	}
	protected String conjugation() {
		return tagList.get(CONJUGATION);
	}
	protected String lexeme() {
		return tagList.get(LEXEME);
	}
	protected String kana() {
		return tagList.get(KANA);
	}
	protected String pronunciation() {
		return tagList.get(PRONUNCIATION);
	}
	

	/**********************************/
	/********** Objectメソッド **********/
	/**********************************/
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