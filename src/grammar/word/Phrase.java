package grammar.word;

import java.util.ArrayList;
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
import language.pos.TagsFactory;

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
		super(collectMorphemes(dependent, head));
		this.dependent = dependent;
		this.head = head;
	}
	private static List<Morpheme> collectMorphemes(List<? extends Clause<?>> dependent, Word head) {
		Stream<Morpheme> dependentMorphemes = dependent.stream()
				.flatMap(c -> c.getChildren().stream())
				.flatMap(c -> c.getChildren().stream());
		Stream<Morpheme> headMorphemes = head.getChildren().stream();
		return Stream.concat(dependentMorphemes, headMorphemes).collect(Collectors.toList());
	}
	private Phrase(Phrase other) {
		this(new ArrayList<>(other.dependent), 
				other.head.clone());
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
				model.createList(dependent.stream().map(m -> m.toJASS(model)).iterator())
				.addProperty(RDF.type, JASS.ClauseList);

		return super.toJASS(model)
				.addProperty(RDF.type, JASS.Phrase)
				.addProperty(JASS.dependent, dependentList)
				.addProperty(JASS.head, head.toJASS(model));
	}
	private static String[][] ADJ = new String[][]{{"形容詞","-連用テ接続"}};
	private static String[][] PREN = new String[][]{{"連体詞"}};
	private static String[][] PART = new String[][]{{"助詞","連体化"}};
	private static String[][] AUX = new String[][]{{"助動詞","体言接続"}};
	@Override
	public Resource createResource(Model m) {
		TagsFactory factory = TagsFactory.getInstance();
		Stream<Word> kotoWords = Stream.of("こと", "事")
				.map(s -> new Word(s, factory.getCabochaTags("名詞", "非自立", "一般", "*", "*", "*", s, "コト", "コト")));
		Stream<Word> monoWords = Stream.of("もの", "物")
				.map(s -> new Word(s, factory.getCabochaTags("名詞", "非自立", "一般", "*", "*", "*", s, "モノ", "モノ")));

		List<? extends Clause<?>> depcopy = new LinkedList<>(dependent);
		/*
		 * 「こと」ならその直前の文節(従属部の最後尾)の自立語のリソース (「Xのこと」のX部分)
		 * 「もの」なら空白ノード
		 * そうでなければ主要部に、従属部の文節の情報を付け足していく
		 */
		Resource r;
		if (kotoWords.anyMatch(head::equals)) {
			Categorem prevDep = depcopy.remove(depcopy.size()-1).getCategorem();
			Resource prevRsrc = prevDep.createResource(m);
			if (prevDep.mainPoS().equals("名詞")) {
				r = depcopy.isEmpty()?
						prevRsrc :
						m.createResource().addProperty(RDF.type, prevRsrc);
			} else if (prevDep.mainPoS().equals("動詞")) {
				prevRsrc.addProperty(RDFS.subClassOf, m.createResource("https://schema.org/Action"));
				r = depcopy.isEmpty()?
						prevRsrc :
						m.createResource().addProperty(RDF.type, prevRsrc);
			} else {
				r = m.createResource()
						.addProperty(MoS.attributeOf, prevRsrc);
			}
		} else if (monoWords.anyMatch(head::equals)) {
			Categorem prevDep = depcopy.remove(depcopy.size()-1).getCategorem();
			Resource main = prevDep.createResource(m);
			r = m.createResource()
					.addProperty(RDF.type, main);
		} else {
			r = m.createResource().addProperty(RDF.type, head.createResource(m));
		}
		
		depcopy.forEach(dep -> {
			Resource depRsrc = dep.getCategorem().createResource(m);
			if (dep.endWith(ADJ, true)) {
				// "大きい"など。連用テ接続は"大きく(て)"のように並列する表現
				Resource d_anon = m.createResource().addProperty(MoS.attributeOf, depRsrc);
				RDFList list = m.createList(new RDFNode[]{r, d_anon});
				m.createResource().addProperty(OWL2.intersectionOf, list);
			} else if (dep.endWith(PREN, true)) {
				// "大きな"、"こういう"、"あの"、など。
				// "大きな"は"大きい"の活用形ではないことに注意	
				r.addProperty(DCTerms.relation, depRsrc);
			} else if (dep.endWith(PART, true)) {
				// "の"のみ該当
				r.addProperty(MoS.of, depRsrc);
			} else if (dep.endWith(AUX, true)) {
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
				dependent.stream()
				.map(Clause::toString)
				.collect(Collectors.joining("/")) + "-" +
				head.toString() + "]";
	}
}
