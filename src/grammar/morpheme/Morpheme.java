package grammar.morpheme;

import java.util.List;

import util.UniqueSet;

public class Morpheme extends EnMorpheme {
	private static UniqueSet<Morpheme> uniqueset = new UniqueSet<>(100);	// EnMorphemeの同名staticフィールドを隠蔽


	/***********************************/
	/**********  Constructor  **********/
	/***********************************/
	private Morpheme(String name, List<String> tagList) {
		super(name, tagList);
		
		Morpheme.uniqueset.add(this);	
	}
	private Morpheme(List<String> name_tags) {
		this(name_tags.get(0), name_tags.subList(1, name_tags.size()));
	}
	
	
	public static Morpheme getOrNewInstance(String name, List<String> tags) {
		Morpheme m = new Morpheme(name, tags);
		return uniqueset.getExistingOrIntact(m);
	}
	public static Morpheme getOrNewInstance(List<String> name_tags) {
		Morpheme m = new Morpheme(name_tags);
		return uniqueset.getExistingOrIntact(m);
	}
	
	
	/***********************************/
	/**********   Interface   **********/
	/***********************************/
	
	
	/**********************************/
	/**********    Getter    **********/
	/**********************************/
	/**********   準Getter   **********/
	@Override
	public List<String> tags() {
		return tags.getTagList();
	}
	public String lexeme() {
		return tags.lexeme();
	}
	public String kana() {
		return tags.kana();
	}
	public String pronunciation() {
		return tags.pronunciation();
	}
	
	
	/**********************************/
	/********** Objectメソッド **********/
	/**********************************/
	
}