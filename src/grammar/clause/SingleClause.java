package grammar.clause;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import data.RDF.vocabulary.JASS;
import grammar.morpheme.Morpheme;
import grammar.morpheme.MorphemeFactory;
import grammar.word.Adjunct;
import grammar.word.Categorem;
import grammar.word.Word;
import language.pos.TagsFactory;

public class SingleClause extends Clause<Categorem> {
	public static final SingleClause ROOT =
			new SingleClause(Categorem.EMPTY_CATEGOREM, Collections.emptyList(), Collections.emptyList());

	/* ================================================== */
	/* ================== Constructor =================== */
	/* ================================================== */
	/**
	 * 自立語，付属語のリスト，接辞のリストを受け取って初期化.
	 * @param categorem
	 * @param adjuncts
	 * @param others
	 */
	public SingleClause(Categorem categorem, List<Adjunct> adjuncts, List<Word> others) {
		super(categorem, adjuncts, others);
	}
	private SingleClause(SingleClause other) {
		this(other.categorem.clone(), 
				new ArrayList<>(other.adjuncts), 
				new ArrayList<>(other.others));
	}

	/* ================================================== */
	/* ================== Static Method ================= */
	/* ================================================== */
	/**
	 * 2つの文節の破壊的結合. 前の文節の形態素は全て後ろの文節の自立語に入れられる. 元の文節の情報は取り出せなくなる不可逆処理.
	 * @param front 前の文節
	 * @param back 後ろの文節
	 * @return 結合してできた文節
	 */
	public static SingleClause concatClauseDestructive(Clause<?> front, SingleClause back) {
		List<Morpheme> newCatMorp = front.morphemes();
		// 前の文節の形態素の原形を表層系に変える. CaboChaの仕様への対応策
		newCatMorp = newCatMorp.stream().map(m -> MorphemeFactory.getInstance().getMorpheme(
				m.name(), 
				TagsFactory.getInstance().getCabochaTags(m.mainPoS(), m.subPoS1(), m.subPoS2(), m.subPoS3(), m.conjugation(), m.inflection(), m.name(), m.yomi(), m.pronunciation())
				)).collect(Collectors.toList());
		newCatMorp.addAll(back.categorem.getChildren());
		Categorem newCat = new Categorem(newCatMorp);
		back.setCategorem(newCat);
		return back;
	}
	
	/* ================================================== */
	/* ================== Member Method ================= */
	/* ================================================== */
	/**
	 * この文節に含まれる単語をOthers以外全て繋げて一つの名詞にする.
	 * 構文解析結果を無視して変更する破壊的な処理なので注意.
	 * Phraseとは違い，修飾・被修飾の関係も消える.
	 */
	public boolean nounize() {
		List<Morpheme> morphemes = Stream.concat(
				categorem.getChildren().stream(),
				adjuncts.stream().flatMap(ad -> ad.getChildren().stream()))
				.collect(Collectors.toList());
		Categorem nounedWord = new Categorem(morphemes);

		this.categorem = nounedWord;
		this.adjuncts.clear();
		return true;
	}


	/* ================================================== */
	/* ================= Abstract Method ================ */
	/* ================================================== */
	@Override
	public SingleClause clone() {
		SingleClause clone = new SingleClause(this);
		clone.setDepending(getDepending());
		return clone;
	}

	/* ================================================== */
	/* ================= Interface Method =============== */
	/* ================================================== */
	@Override
	public Resource toJASS(Model model) {
		return super.toJASS(model).addProperty(RDF.type, JASS.SingleClause);
	}

	/* ================================================== */
	/* ================== Object Method ================= */
	/* ================================================== */
	@Override
	public String toString() {
		return getChildren().stream()
				.map(w -> Objects.toString(w, "Word"))
				.collect(Collectors.joining("."));	}
	
}
