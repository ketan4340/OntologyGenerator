package grammar.word;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.OWL2;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

import data.RDF.vocabulary.JASS;
import data.RDF.vocabulary.MoS;
import grammar.clause.Clause;
import grammar.morpheme.Morpheme;
import grammar.pattern.ClausePattern;
import language.pos.TagsFactory;

/**
 * 名詞句を想定
 * @author tanabekentaro
 */
public class Phrase extends Categorem {
	
	/** 従属部 */
	private final Dependent dependent;
	/** 主要部 */
	private final Categorem head;

	/* ================================================== */
	/* ================== Constructor =================== */
	/* ================================================== */
	public Phrase(Dependent dependent, Categorem head) {
		super(collectMorphemes(dependent, head));
		this.dependent = dependent;
		this.head = head;
	}
	private static List<Morpheme> collectMorphemes(Dependent dependent, Word head) {
		Stream<Morpheme> dependentMorphemes = dependent.getChildren().stream()
				.flatMap(c -> c.getChildren().stream())
				.flatMap(c -> c.getChildren().stream());
		Stream<Morpheme> headMorphemes = head.getChildren().stream();
		return Stream.concat(dependentMorphemes, headMorphemes).collect(Collectors.toList());
	}
	private Phrase(Phrase other) {
		this(other.dependent.clone(), other.head.clone());
	}

	/* ================================================== */
	/* ================== Member Method ================= */
	/* ================================================== */


	/* ================================================== */
	/* ================ Interface Method ================ */ 
	/* ================================================== */
	/**
	 * 全く同じPhraseを複製する
	 */
	@Override
	public Phrase clone() {
		return new Phrase(this);
	}
	
	@Override
	public Resource toJASS(Model model) {
		Resource dependentList = 
				model.createList(dependent.getChildren().stream().map(m -> m.toJASS(model)).iterator())
				.addProperty(RDF.type, JASS.ClauseList);

		return super.toJASS(model)
				.addProperty(RDF.type, JASS.Phrase)
				.addProperty(JASS.dependent, dependentList)
				.addProperty(JASS.head, head.toJASS(model));
	}
	
	private static final ClausePattern ADJ = ClausePattern.Reader.read(new String[][]{{"形容詞", "-連用テ接続"}, {"%o", "$"}});
	private static final ClausePattern PREN = ClausePattern.Reader.read(new String[][]{{"連体詞"}, {"%o", "$"}});
	private static final ClausePattern PART = ClausePattern.Reader.read(new String[][]{{"助詞", "連体化"}, {"%o", "$"}});
	private static final ClausePattern AUX = ClausePattern.Reader.read(new String[][]{{"助動詞", "体言接続"}, {"%o", "$"}});
	@Override
	public Resource createResource(Model m) {
		TagsFactory factory = TagsFactory.getInstance();
		Stream<Word> kotoWords = Stream.of("こと", "事", "コト")
				.map(s -> new Word(s, factory.getCabochaTags("名詞", "非自立", "一般", "*", "*", "*", s, "コト", "コト")));
		Stream<Word> monoWords = Stream.of("もの", "物", "モノ")
				.map(s -> new Word(s, factory.getCabochaTags("名詞", "非自立", "一般", "*", "*", "*", s, "モノ", "モノ")));

		List<? extends Clause<?>> dpdtCopy = new LinkedList<>(dependent.getChildren());
		/*
		「こと」ならその直前の文節(従属部の最後尾)の自立語のリソース (「Xのこと」のX部分)
		「もの」なら空白ノード
		そうでなければ主要部に、従属部の文節の情報を付け足していく
		 */
		Resource r;
		if (kotoWords.anyMatch(head::equals)) {			// こと
			Categorem dpdtTail = dpdtCopy.remove(dpdtCopy.size()-1).getCategorem();
			Resource dpdtTailRsrc = dpdtTail.createResource(m);
			if (dpdtTail.mainPoS().equals("名詞")) {
				r = dpdtCopy.isEmpty()? dpdtTailRsrc :
						m.createResource().addProperty(RDFS.subClassOf, dpdtTailRsrc);
			} else if (dpdtTail.mainPoS().equals("動詞")) {
				dpdtTailRsrc.addProperty(RDFS.subClassOf, m.createResource("https://schema.org/Action"));
				r = dpdtCopy.isEmpty()? dpdtTailRsrc :
						m.createResource().addProperty(RDF.type, dpdtTailRsrc);
			} else {	// 名詞と動詞以外はまずない。とりあえず空白ノード
				r = m.createResource().addProperty(MoS.attributeOf, dpdtTailRsrc);
			}
		} else if (monoWords.anyMatch(head::equals)) {	// もの
			Resource dpdtTailRsrc = dpdtCopy.remove(dpdtCopy.size()-1).getCategorem().createResource(m);
			r = m.createResource().addProperty(RDF.type, dpdtTailRsrc);
		} else {
			r = m.createResource().addProperty(RDF.type, head.createResource(m));
		}
		
		dpdtCopy.forEach(dep -> {
			Resource depRsrc = dep.getCategorem().createResource(m);
			if (dep.matchWith(ADJ, true)) {
				// "大きい"など。連用テ接続は"大きく(て)"のように並列する表現
				Resource d_anon = m.createResource().addProperty(MoS.attributeOf, depRsrc);
				RDFList list = m.createList(new RDFNode[]{r, d_anon});
				m.createResource().addProperty(OWL2.intersectionOf, list);
			} else if (dep.matchWith(PREN, true)) {
				// "大きな"、"こういう"、"あの"、など。
				// "大きな"は"大きい"の活用形ではないことに注意	
				r.addProperty(DCTerms.relation, depRsrc);
			} else if (dep.matchWith(PART, true)) {
				// "の"のみ該当
				r.addProperty(MoS.of, depRsrc);
			} else if (dep.matchWith(AUX, true)) {
				// "変な"の"な"など
				Resource d_anon = m.createResource().addProperty(MoS.attributeOf, depRsrc);
				RDFList list = m.createList(new RDFNode[]{r, d_anon});
				m.createResource().addProperty(OWL2.intersectionOf, list);
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
				dependent.getChildren().stream()
				.map(Clause::toString)
				.collect(Collectors.joining("/")) + "-" +
				head.toString() + "]";
	}
}
