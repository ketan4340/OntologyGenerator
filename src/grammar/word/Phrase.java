package grammar.word;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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
import grammar.morpheme.MorphemeFactory;
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
		setNETag(head.getNETag().orElse(null));
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
	
	private static final Set<Word> KOTO_WORDS = Stream.of("こと", "事", "コト")
			.map(s -> new Word(MorphemeFactory.getInstance().getMorpheme(
					s, TagsFactory.getInstance().getCabochaTags("名詞", "非自立", "一般", "*", "*", "*", s, "コト", "コト"))))
			.collect(Collectors.toSet());
	private static final Set<Word> MONO_WORDS = Stream.of("もの", "物", "モノ")
			.map(s -> new Word(MorphemeFactory.getInstance().getMorpheme(
					s, TagsFactory.getInstance().getCabochaTags("名詞", "非自立", "一般", "*", "*", "*", s, "モノ", "モノ"))))
			.collect(Collectors.toSet());
	private static final ClausePattern ADJ = ClausePattern.compile(new String[][]{{"形容詞", "-連用テ接続"}, {"%o", "$"}});
	private static final ClausePattern PREN = ClausePattern.compile(new String[][]{{"連体詞"}, {"%o", "$"}});
	private static final ClausePattern PART = ClausePattern.compile(new String[][]{{"助詞", "連体化"}, {"%o", "$"}});
	private static final ClausePattern AUX = ClausePattern.compile(new String[][]{{"助動詞", "体言接続"}, {"%o", "$"}});
	@Override
	public Resource createCategoremResource(Model m) {
		List<? extends Clause<?>> dpdtCopy = new LinkedList<>(dependent.getChildren());
		/*
		「こと」ならその直前の文節(従属部の最後尾)の自立語のリソース (「Xのこと」のX部分)
		「もの」なら空白ノード
		そうでなければ主要部に、従属部の文節の情報を付け足していく
		 */
		Resource mainRsrc;
		if (KOTO_WORDS.contains(head)) {			// こと
			Categorem dpdtTail = dpdtCopy.remove(dpdtCopy.size()-1).getCategorem();
			Resource dpdtTailRsrc = dpdtTail.createCategoremResource(m);
			if (dpdtTail.mainPoS().equals("名詞")) {
				mainRsrc = dpdtTailRsrc;
			} else if (dpdtTail.mainPoS().equals("動詞")) {
				dpdtTailRsrc.addProperty(RDFS.subClassOf, m.createResource("https://schema.org/Action"));
				mainRsrc = dpdtTailRsrc;
			} else {	// 名詞と動詞以外はまずない。とりあえず空白ノード
				mainRsrc = m.createResource().addProperty(MoS.attributeOf, dpdtTailRsrc);
			}
		} else if (MONO_WORDS.contains(head)) {	// もの
			mainRsrc = m.createResource();
		} else {								// 他一般名詞
			//mainRsrc = m.createResource().addProperty(RDF.type, head.createCategoremResource(m));
			mainRsrc = head.createCategoremResource(m);
		}
		
		dpdtCopy.forEach(dep -> {
			Resource depRsrc = dep.getCategorem().createCategoremResource(m);
			if (ADJ.matches(dep)) {
				// "大きい"など。連用テ接続は"大きく(て)"のように並列する表現
				Resource d_anon = m.createResource().addProperty(MoS.attributeOf, depRsrc);
				RDFList list = m.createList(new RDFNode[]{mainRsrc, d_anon});
				m.createResource().addProperty(OWL2.intersectionOf, list);
			} else if (PREN.matches(dep)) {
				// "大きな"、"こういう"、"あの"、など。
				// "大きな"は"大きい"の活用形ではないことに注意	
				mainRsrc.addProperty(DCTerms.relation, depRsrc);
			} else if (PART.matches(dep)) {
				// "の"のみ該当
				mainRsrc.addProperty(MoS.of, depRsrc);
			} else if (AUX.matches(dep)) {
				// "変な"の"な"など
				Resource d_anon = m.createResource().addProperty(MoS.attributeOf, depRsrc);
				RDFList list = m.createList(new RDFNode[]{mainRsrc, d_anon});
				m.createResource().addProperty(OWL2.intersectionOf, list);
			} else {
			}
		});
		return mainRsrc;
	}
	
	/* ================================================== */
	/* ================== Object Method ================= */ 
	/* ================================================== */
	@Override
	public int hashCode() {
		return Objects.hash(dependent, head);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Phrase))
			return false;
		Phrase other = Phrase.class.cast(obj);
		return Objects.equals(this.dependent, other.dependent) && 
				Objects.equals(this.head, other.head);
	}
	
	@Override
	public String toString() {
		return "[" +
				dependent.getChildren().stream()
				.map(Clause::toString)
				.collect(Collectors.joining("/")) + "-" +
				head.toString() + "]";
	}
}
