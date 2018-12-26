package decisiontree;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import grammar.clause.Clause;
import grammar.sentence.Sentence;
import grammar.word.Word;

public class RecordCreator {



	public static String sentence2record(Sentence sentence) {
		List<String> values = new ArrayList<>();

		List<Clause<?>> subjectList = sentence.subjectList(true);	// 主語を整えたところで再定義
		if (subjectList.isEmpty())
			return "";

		Clause<?> subjectClause = subjectList.get(0);			// 主節(!!最初の1つしか使っていない!!)
		// 述節
		Clause<?> predicateClause = subjectClause.getDepending();
		if (predicateClause == null)
			return "";
		Word predicateWord = predicateClause.getCategorem();	// 述語
		if (predicateWord == null)
			return "";
		// 述部(主節に続く全ての節)
		// String predicatePart = subSentence(clauses.indexOf(subjectClause)+1,
		// clauses.size()).toString();

		// String[][] tag_Not = {{"助動詞", "ない"}, {"助動詞", "不変化型", "ん"}, {"助動詞", "不変化型",
		// "ぬ"}};
		// boolean not = predicateClause.haveSomeTagWord(tag_Not); // 述語が否定かどうか

		// 述語が[<名詞>である。]なのか[<動詞>する。]なのか[<形容詞>。]なのか
			// 述語が動詞
		if (predicateClause.containsWordHas(new String[]{ "動詞" }) ||
				predicateClause.containsWordHas(new String[]{ "サ変接続" })) {
			if (predicateClause.containsWordHas(new String[]{ "接尾", "れる" }) || 
					predicateClause.containsWordHas(new String[]{ "接尾", "られる" }))
				values.add("passive");
			else
				values.add("verb");
			values.add(predicateWord.infinitive());
			// 述語が形容詞
		} else if (predicateClause.containsWordHas(new String[]{"形容詞"}) ||
				predicateClause.containsWordHas(new String[]{"形容動詞語幹"})) {
			values.add("adjc");
			values.add(predicateWord.infinitive());
			// 述語が名詞または助動詞
		} else {
			values.add("noun");
			String predNoun = predicateWord.infinitive();
			values.add(predNoun.substring(predNoun.length() - 2));	// 最後の一文字だけ
		}
		return values.stream().collect(Collectors.joining(","));
	}
}
