package grammar.word;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import data.RDF.vocabulary.JASS;
import grammar.Concept;
import grammar.clause.Clause;
import grammar.morpheme.Morpheme;

/**
 * 名詞句を想定
 * @author tanabekentaro
 */
public class Phrase extends Word{

	/** 従属部 */
	private final List<? extends Clause<?>> dependent;
	/** 主要部 */
	private final Word head;
	
	
	/****************************************/
	/**********     Constructor    **********/
	/****************************************/
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
	
	/****************************************/
	/**********   Member  Method   **********/
	/****************************************/
	/**
	 * 全く同じPhraseを複製する
	 */
	@Override
	public Phrase clone() {
		List<Clause<?>> cloneDependent = dependent.stream()
				.map(c -> c.clone()).collect(Collectors.toList());
		Word cloneHead = head.clone();
		return new Phrase(cloneDependent, cloneHead);
	}
	
	/****************************************/
	/**********  Interface Method  **********/
	/****************************************/
	@Override
	public Resource toRDF(Model model) {
		Resource clauseNode = model.createList(
				dependent.stream().map(m -> m.toRDF(model)).iterator());
		
		return super.toRDF(model)
				.addProperty(RDF.type, JASS.Phrase)
				.addProperty(JASS.consistsOfDependent, clauseNode)
				.addProperty(JASS.consistsOfHead, head.toRDF(model));
	}
	
	/****************************************/
	/**********   Object  Method   **********/
	/****************************************/
	@Override
	public String toString() {
		return "[" + 
				dependent.stream()
				.map(Clause::toString)
				.collect(Collectors.joining("/")) + "-" + 
				head.toString() + "]";
	}
}