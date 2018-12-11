package modules.textRevision;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import data.id.IDTuple;
import data.id.SentenceIDMap;
import grammar.clause.Clause;
import grammar.morpheme.Morpheme;
import grammar.sentence.Sentence;
import grammar.word.Word;
import pos.Concatable;

public class SentenceReviser {

	/*
	private static final String[][][] TAGS_CATEGOREM_ADJUNCTS = {
			{{"サ変接続"}, {"動詞", "サ変・スル"}},
			{{"動詞"}, {"動詞", "接尾"}}
			};
	 */
	private static final String[][][] TAGS_NOUNPHRASE = {
			{{"形容詞", "-連用テ接続"}},	// 連用テ接続は"大きくて"など
			{{"連体詞"}},					// "大きな"、"こういう"、"あの"、など
			{{"助詞", "連体化"}},			// "の"のみ該当
			{{"助動詞", "体言接続"}},		// "変な"の"な"など
			{{"名詞"}},
			{{"体言接続特殊２"}}
			};

	/* ================================================== */
	/* ================== Constructor =================== */
	/* ================================================== */
	public SentenceReviser() {
	}

	/* ================================================== */
	/* ================== Member Method ================= */
	/* ================================================== */
	public void connectWord(SentenceIDMap sentenceMap) {
		sentenceMap.forEachKey(this::connectWord);
	}
	public Sentence connectWord(Sentence sentence) {
		// 別々の形態素に別れてしまっている数値を1つの形態素にする
		Stream.of(sentence)
		.map(Sentence::getChildren).flatMap(List::stream)
		.map(Clause::words).flatMap(List::stream)
		.forEach(w -> weldNumbers(w));

		// サ変動詞と接尾をもつ動詞をつなげる
		/*
		Stream.of(TAGS_CATEGOREM_ADJUNCTS).forEach(tag_CA -> {
			sentence.getChildren().forEach(c -> c.uniteAdjunct2Categorem(tag_CA[0], tag_CA[1]));
		});
		*/
		// 名詞か形容詞が末尾につく文節を隣の文節につなげる
		Stream.of(TAGS_NOUNPHRASE).forEach(tag_NP -> {
			for (Clause<?> matchedClause = sentence.findFirstClauseEndWith(tag_NP, true);
					matchedClause != null; ) {	// 指定の品詞で終わる文節がなくなるまで繰り返し
				if (!sentence.connect2Next(matchedClause))
					break;
				matchedClause = sentence.findFirstClauseEndWith(tag_NP, true);
			}
		});
		return sentence;
	}

	private void weldNumbers(Word w) {
		List<Morpheme> mphs = w.getChildren();
		Optional<IndexRange> numRange = rangeOfNumbers(mphs);
		numRange.ifPresent(ft -> {
			List<Morpheme> subMph = mphs.subList(ft.from, ft.to);
			Morpheme welded = Concatable.join(subMph);
			subMph.clear();
			mphs.add(ft.from, welded);
		});
	}

	private Optional<IndexRange> rangeOfNumbers(List<Morpheme> morphemes) {
		int from = -1, to = -1, i = 0;
		boolean continuing = false;
		for (Morpheme m : morphemes) {
			if (m.containsTag("数")) {
				if (!continuing) {	// 最初の数字。数字の連続の開始点
					from = to = i;
					continuing = true;
				}
				to++;
			} else if (m.name().equals(".") || m.name().equals(",")) {
				if (continuing)	{// 数字が連続中の小数点・カンマはOK。ループ継続
					to++;
				} else {			// 数字の間に入っていない小数点・カンマはスルー
					continuing = false;
				}
			} else {
				if (continuing)	{// 連続中に数字・小数点・カンマ以外がきたら終了
					break;
				}
			}
			i++;
		}
		if (from == -1)
			return Optional.empty();
		// 末尾にピリオドやカンマが並んでいたら切り落とす
		for (int j = to-1; j > from; j--) {
			String name = morphemes.get(j).name();
			if (name.equals(".") || name.equals(","))
				to--;
			else
				break;
		}
		return Optional.of(new IndexRange(from, to));
	}
	private static class IndexRange {
		public final int from;
		public final int to;
		public IndexRange(int from, int to) {
			this.from = from;
			this.to = to;
		}
	}
	
	/**
	 * 長文分割.
	 * @param sentenceEntry 	キーが文，値がIDタプルのマップエントリ
	 * @return
	 */
	private SentenceIDMap divideSentence(Map.Entry<Sentence, IDTuple> sentenceEntry) {
		Sentence sentence = sentenceEntry.getKey();
		IDTuple ids = sentenceEntry.getValue();
		List<Sentence> dividedSentences = Stream.of(sentence)
				.map(Sentence::divide2)
				.flatMap(List<Sentence>::stream)
				.map(Sentence::divide3)
				.flatMap(List<Sentence>::stream)
				.collect(Collectors.toList());

		SentenceIDMap sm = SentenceIDMap.createFromList(dividedSentences);
		sm.forEachValue(t -> t.copy(ids));

		return sm;
	}
	public void divideEachSentence(SentenceIDMap sentenceMap) {
		SentenceIDMap clone = new SentenceIDMap(sentenceMap);
		sentenceMap.clear();
		clone.entrySet().stream()
		.map(this::divideSentence)
		.flatMap(m -> m.entrySet().stream())
		.peek(e -> e.getKey().uniteSubject())	// 主語結合
		.forEach(e -> sentenceMap.put(e.getKey(), e.getValue()));
	}
}