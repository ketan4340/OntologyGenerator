package grammar.word;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import data.RDF.vocabulary.JASS;
import grammar.GrammarInterface;
import grammar.SyntacticChild;
import grammar.SyntacticParent;
import grammar.morpheme.Morpheme;
import grammar.morpheme.MorphemeFactory;
import language.pos.CabochaPoSInterface;
import language.pos.CabochaTags;
import language.pos.Concatable;

public class Word extends SyntacticParent<Morpheme>
		implements SyntacticChild, GrammarInterface, CabochaPoSInterface, Concatable<Word> {
	private static int SUM = 0;

	private final int id;
	private byte coreMphmIdx;	// 1単語に128以上の形態素なんてないはず

	/* ================================================== */
	/* ================== Constructor =================== */
	/* ================================================== */
	public Word(List<Morpheme> morphemes) {
		super(morphemes);
		this.id = SUM++;
	}
	public Word(Morpheme... morphemes) {
		this(Arrays.asList(morphemes));
	}
	public Word(String name, CabochaTags tags) {
		this(MorphemeFactory.getInstance().getMorpheme(name, tags));
	}

	/* ================================================== */
	/* ================== Static Method ================= */
	/* ================================================== */
	
	/* ================================================== */
	/* ================== Member Method ================= */
	/* ================================================== */
	public Morpheme coreMorpheme() {
		return children.get(coreMphmIdx);
	}
	
	private void setCoreMorphemeIndex(List<Morpheme> morphemes) {
		this.coreMphmIdx = coreMorphemeIndex(morphemes);
	}
	private byte coreMorphemeIndex(List<Morpheme> morphemes) {
		ListIterator<Morpheme> li = morphemes.listIterator(morphemes.size());
		while (li.hasPrevious())
			if (!li.previous().contains("接尾"))
				return (byte)(li.previousIndex() + 1);
		return (byte)morphemes.size(); 
	}
	
	/**
	 * 渡されたTagを"全て"持って入れば真、それ以外は偽を返す
	 */
	public boolean hasAllTag(String[] tags) {
		boolean match = true;	// デフォがtrueなので空の配列は任意の品詞とみなされる
		for (String tag: tags) {
			boolean not = false;	// NOT検索用のフラグ
			if (tag.startsWith("-")) {	// Tag名の前に-をつけるとそのタグを含まない時にtrue
				not = true;
				tag = tag.substring(1);	// -を消しておく
			}
			match = coreMorpheme().contains(tag);
			match = not? !match : match;

			if (!match) break;	// falseなら即終了
		}
		return match;
	}

	/** 全く同じWordを複製する */
	@Override
	public Word clone() {
		return new Word(children);
	}

	/* ================================================== */
	/* ================ Interface Method ================ */ 
	/* ================================================== */
	@Override
	public int id() {
		return id;
	}
	@Override
	public String name() {
		return children.stream().map(Morpheme::name).collect(Collectors.joining());
	}
	@Override
	public String mainPoS() {
		return coreMorpheme().mainPoS();
	}
	@Override
	public String subPoS1() {
		return coreMorpheme().subPoS1();
	}
	@Override
	public String subPoS2() {
		return coreMorpheme().subPoS2();
	}
	@Override
	public String subPoS3() {
		return coreMorpheme().subPoS3();
	}
	@Override
	public String inflection() {
		return coreMorpheme().inflection();
	}
	@Override
	public String conjugation() {
		return coreMorpheme().conjugation();
	}
	@Override
	public String infinitive() {
		return children.stream().map(m -> m.infinitive()).collect(Collectors.joining());
	}
	@Override
	public String yomi() {
		return children.stream().map(m -> m.yomi()).collect(Collectors.joining());
	}
	@Override
	public String pronunciation() {
		return children.stream().map(m -> m.pronunciation()).collect(Collectors.joining());
	}
	@Override
	public boolean contains(String pos) {
		return children.stream().anyMatch(m -> m.contains(pos));
	}
	@Override
	public boolean containsAll(Collection<String> poss) {
		return children.stream().anyMatch(m -> m.containsAll(poss));
	}
	
	@Override
	public Resource toJASS(Model model) {
		Resource morphemeNode = model.createList(children.stream()
				.map(m -> m.toJASS(model)).iterator());

		Resource wordResource = model.createResource(getJassURI())
				.addProperty(RDF.type, JASS.Word)
				.addProperty(JASS.morphemes, morphemeNode)
				.addLiteral(JASS.name, name())
				.addLiteral(JASS.mainPoS, mainPoS())
				.addLiteral(JASS.subPoS1, subPoS1())
				.addLiteral(JASS.subPoS2, subPoS2())
				.addLiteral(JASS.subPoS3, subPoS3())
				.addLiteral(JASS.conjugation, conjugation())
				.addLiteral(JASS.inflection, inflection())
				.addLiteral(JASS.infinitive, infinitive())
				.addLiteral(JASS.kana, yomi())
				.addLiteral(JASS.pronunsiation, pronunciation());
		return wordResource;
	}
	
	@Override
	public Word concat(Word other) {
		this.children.addAll(other.children);
		return this;
	}

	@Override
	public void onChanged(Change<? extends Morpheme> c) {
		setCoreMorphemeIndex(children);
	}
	
	/* ================================================== */
	/* ================== Object Method ================= */ 
	/* ================================================== */
	@Override
	public String toString() {
		return children.stream().map(m -> m.toString()).collect(Collectors.joining());
	}
	

}