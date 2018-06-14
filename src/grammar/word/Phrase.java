package grammar.word;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import grammar.Concept;
import grammar.clause.Clause;
import grammar.morpheme.Morpheme;

/**
 * 名詞句を想定
 * @author tanabekentaro
 */
public class Phrase extends Word{

	/** 従属部 */
	private List<? extends Clause<?>> dependent;
	/** 主要部 */
	private Word head;
	
	

	/***********************************/
	/**********  Constructor  **********/
	/***********************************/
	public Phrase(List<? extends Clause<?>> dependent, Word head) {
		super(concatConcept(dependent, head));
		this.dependent = dependent;
		this.head = head;
	}
	private static Concept concatConcept(List<? extends Clause<?>> dependent, Word head) {
		Stream<Morpheme> dependentMorphemes = dependent.stream()
				.flatMap(c -> c.getChildren().stream())
				.map(w -> w.getConcept())
				.flatMap(c -> c.getMorphemes().stream());
		Stream<Morpheme> headMorphemes = head.concept.getMorphemes().stream();
		 List<Morpheme> morphemes = Stream.concat(dependentMorphemes, headMorphemes).collect(Collectors.toList());
		return Concept.getOrNewInstance(morphemes);
	}
	
	/***********************************/
	/**********  MemberMethod **********/
	/***********************************/
	/**
	 * 全く同じWordを複製する
	 */
	@Override
	public Phrase clone() {
		List<Clause<?>> cloneDependent = dependent.stream()
				.map(c -> c.clone()).collect(Collectors.toList());
		Word cloneHead = head.clone();
		return new Phrase(cloneDependent, cloneHead);
	}
	

	/**********************************/
	/********** Objectメソッド **********/
	/**********************************/
	@Override
	public String toString() {
		return dependent.stream()
				.map(Clause::toString)
				.collect(Collectors.joining("/")) + 
				"-" + head.toString();
	}
}