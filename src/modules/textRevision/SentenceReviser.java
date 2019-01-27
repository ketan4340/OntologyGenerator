package modules.textRevision;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import data.id.SentenceIDMap;
import grammar.clause.Clause;
import grammar.clause.SerialClause;
import grammar.morpheme.Morpheme;
import grammar.pattern.BiClausePattern;
import grammar.pattern.ClausePattern;
import grammar.sentence.Sentence;
import grammar.word.Word;
import language.pos.Concatable;
import util.Range;

public class SentenceReviser {
	private static final ClausePattern[] NOUNIZE_PATTERNS = {
			ClausePattern.compile(new String[][]{{"体言接続特殊２"}, {"%o", "$"}})
	};
	private static final BiClausePattern[] NOUNPHRASE_PATTERNS = {
			// 連用テ接続は"大きくて"など
			BiClausePattern.compile(new String[][][]{{{"形容詞", "-連用テ接続"}, {"%o", "$"}},{}}),	
			// "大きな"、"こういう"、"あの"、など
			BiClausePattern.compile(new String[][][]{{{"連体詞"}, {"%o", "$"}}, {}}), 
			// "の"のみ該当
			BiClausePattern.compile(new String[][][]{{{"助詞", "連体化"}, {"%o", "$"}}, {}}), 
			// "変な"の"な"など
			BiClausePattern.compile(new String[][][]{{{"助動詞", "体言接続"}, {"%o", "$"}}, {}}), 
			BiClausePattern.compile(new String[][][]{{{"名詞"}, {"%o", "$"}}, {}}), 
			// "光るもの"など
			BiClausePattern.compile(new String[][][]{{{"動詞"}, {"%o", "$"}}, {{"もの", "非自立"}, {"%o", "^"}}}), 
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
	
	public void connectWord(Sentence sentence) {
		// 別々の形態素に別れてしまっている数値を1つの形態素にする
		Stream.of(sentence)
		.map(Sentence::getChildren).flatMap(List::stream)
		.map(Clause::words).flatMap(List::stream)
		.forEach(w -> weldNumbers(w));
		
		// 指定の品詞を持つ形態素を次の形態素と破壊的に結合する
		Arrays.stream(NOUNIZE_PATTERNS).forEach(np -> {
			for (Optional<Clause<?>> matchedClause_opt = sentence.findFirstClauseMatching(np);
					matchedClause_opt.isPresent();
					matchedClause_opt = sentence.findFirstClauseMatching(np)) 
			{	// 指定の品詞で終わる文節がなくなるまで繰り返し
				if (matchedClause_opt.isPresent()) {
					Clause<?> c = matchedClause_opt.get();
					if (!sentence.connect2Next(c, true)) break;
				}
			}
		});

		// 名詞か形容詞が末尾につく文節を隣の文節につなげる
		for (boolean connected = true; connected; ) {
			connected = false;
			for (BiClausePattern pattern : NOUNPHRASE_PATTERNS) {
				SubsentenceMatcher matcher = pattern.matcher(sentence);
				boolean result = matcher.replaceFirst(SerialClause::join);
				connected = connected || result; // どの文節列パターンでも結合されなかった場合終了			
			}
		}
		
	}

	private void weldNumbers(Word w) {
		List<Morpheme> mphs = w.getChildren();
		Optional<Range> numRange = rangeOfNumbers(mphs);
		numRange.ifPresent(ft -> {
			List<Morpheme> subMph = mphs.subList(ft.from(), ft.to());
			Morpheme welded = Concatable.join(subMph);
			subMph.clear();
			mphs.add(ft.from(), welded);
		});
	}

	private Optional<Range> rangeOfNumbers(List<Morpheme> morphemes) {
		int from = -1, to = -1, i = 0;
		boolean continuing = false;
		for (Morpheme m : morphemes) {
			if (m.contains("数")) {
				if (!continuing) {	// 最初の数字。数字の連続の開始点
					from = to = i;
					continuing = true;
				}
				to++;
			} else if (m.name().equals(".") || m.name().equals(",")) {
				if (continuing)	// 数字が連続中の小数点・カンマはOK。ループ継続
					to++;
				else			// 直前に数字がない小数点・カンマはスルー
					continuing = false;
			} else {
				if (continuing)	// 連続中に数字・小数点・カンマ以外がきたら終了
					break;
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
		return Optional.of(new Range(from, to));
	}
	
	/**
	 * 長文分割.
	 * @param sentenceMap 	キーが文，値がIDタプルのマップエントリ
	 */
	public void divideEachSentence(SentenceIDMap sentenceMap) {
		Map<Sentence, List<Sentence>> replaceMap = 
				sentenceMap.keySet().stream()
				.collect(Collectors.toMap(s->s, s->divideSentence(s)));
		//sentenceMap.forEachKey(s -> System.out.println(replaceMap.containsKey(s) +"--"+ s.hashCode() + s.name()));
		//replaceMap.forEach((s, ls) -> System.out.println(sentenceMap.containsKey(s) + "--" + s.hashCode() + s.name()));
		// なぜかsentenceMapから直接replaceSentence~を呼ぶと、replaceMapにキーとなる文が存在しないと言われヌルポが発生する
		// クローンからreplaceSentence~を呼べば動く。わけわかめ
		SentenceIDMap clone = new SentenceIDMap(sentenceMap);
		SentenceIDMap replacedSntcIDMap = clone.replaceSentence2Sentences(replaceMap);
		sentenceMap.clear();
		sentenceMap.putAll(replacedSntcIDMap);
	}
	
	private List<Sentence> divideSentence(Sentence sentence) {
		return Stream.of(sentence)
				.map(Sentence::divide2).flatMap(List<Sentence>::stream)
				.map(Sentence::divide3).flatMap(List<Sentence>::stream)
				.collect(Collectors.toList());
	}
}
