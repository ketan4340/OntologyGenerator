package grammar.word;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;

import data.RDF.vocabulary.JASS;
import data.RDF.vocabulary.MoS;
import grammar.clause.Clause;
import grammar.morpheme.Morpheme;

/**
 * 名詞句を想定
 * @author tanabekentaro
 */
public class Phrase extends Categorem {

	/** 従属部 */
	private final List<? extends Clause<?>> dependent;
	/** 主要部 */
	private final Categorem head;

	/* ================================================== */
	/* ================== Constructor =================== */
	/* ================================================== */
	public Phrase(List<? extends Clause<?>> dependent, Categorem head) {
		super(concatMorphemes(dependent, head));
		this.dependent = dependent;
		this.head = head;
	}
	private static List<Morpheme> concatMorphemes(List<? extends Clause<?>> dependent, Word head) {
		Stream<Morpheme> dependentMorphemes = dependent.stream()
				.flatMap(c -> c.getChildren().stream())
				.flatMap(c -> c.getChildren().stream());
		Stream<Morpheme> headMorphemes = head.getChildren().stream();
		return Stream.concat(dependentMorphemes, headMorphemes).collect(Collectors.toList());
	}

	/* ================================================== */
	/* ================== Member Method ================= */
	/* ================================================== */
	/**
	 * 全く同じPhraseを複製する
	 */
	@Override
	public Phrase clone() {
		List<Clause<?>> cloneDependent = dependent.stream()
				.map(c -> c.clone()).collect(Collectors.toList());
		Categorem cloneHead = head.clone();
		return new Phrase(cloneDependent, cloneHead);
	}

	/* ================================================== */
	/* ================ Interface Method ================ */ 
	/* ================================================== */
	@Override
	public Resource toJASS(Model model) {
		Resource clauseNode = model.createList(
				dependent.stream().map(m -> m.toJASS(model)).iterator());

		return super.toJASS(model)
				.addProperty(RDF.type, JASS.Phrase)
				.addProperty(JASS.consistsOfDependent, clauseNode)
				.addProperty(JASS.consistsOfHead, head.toJASS(model));
	}
	
	@Override
	public Resource createResource(Model m) {
		String[] koto = {"こと", "事"};
		String[] mono = {"もの", "物"};
		List<? extends Clause<?>> depcopy = new ArrayList<>(dependent);
		/*
		 * 「こと」ならその直前の文節(従属部の最後尾)の自立語のリソース (「Xのこと」のX)
		 * 「もの」なら空白ノード
		 * そうでなければ主要部に、従属部の文節の情報を付け足していく
		 */
		Resource r = 
				Arrays.stream(koto).anyMatch(mk -> head.subPoS1().equals("非自立")&&head.infinitive().equals(mk))?
						depcopy.remove(depcopy.size()-1).getCategorem().createResource(m) :
				Arrays.stream(mono).anyMatch(mk -> head.subPoS1().equals("非自立")&&head.infinitive().equals(mk))?
						m.createResource() : head.createResource(m);
		
		depcopy.forEach(dep -> {
			Resource depResource = dep.getCategorem().createResource(m);
			if ( dep.endWith(new String[][]{{"形容詞","-連用テ接続"}}, true) ) {
				// "大きい"など。連用テ接続は"大きく(て)"のように並列する表現
				r.addProperty(MoS.attributeOf, depResource);

			} else if ( dep.endWith(new String[][]{{"連体詞"}}, true)) {
				// "大きな"、"こういう"、"あの"、など。
				// "大きな"は"大きい"の活用形ではないことに注意	
				r.addProperty(DCTerms.relation, depResource);
			} else if ( dep.endWith(new String[][]{{"助詞","連体化"}}, true)) {
				// "の"のみ該当
				r.addProperty(DCTerms.relation, depResource);
			} else if ( dep.endWith(new String[][]{{"助動詞","体言接続"}}, true)) {
				// "変な"の"な"など
				r.addProperty(MoS.attributeOf, depResource);
			} else {
				
			}
		});
		return r;
	}
	

	/* ================================================== */
	/* ================== Object Method ================= */ 
	/* ================================================== */
	@Override
	public String toString() {
		return "[" +
				dependent.stream()
				.map(Clause::toString)
				.collect(Collectors.joining("/")) + "-" +
				head.toString() + "]";
	}
}
