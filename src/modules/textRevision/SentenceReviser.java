package modules.textRevision;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import data.id.IDTuple;
import data.id.SentenceIDMap;
import grammar.Sentence;
import grammar.clause.AbstractClause;

public class SentenceReviser {

	private static final String[][][] tags_CtgAdj = {
			{{"サ変接続"}, {"動詞", "サ変・スル"}},
			{{"動詞"}, {"動詞", "接尾"}}
			};
	private static final String[][][] tags_NP = {
			{{"形容詞", "-連用テ接続"}},
			{{"連体詞"}},
			{{"助詞", "連体化"}},
			{{"助動詞", "体言接続"}}, 
			{{"名詞"}}
			};
	
	

	/****************************************/
	/**********     Constructor    **********/
	/****************************************/
	public SentenceReviser() {
	}
	

	/****************************************/
	/**********   Member  Method   **********/
	/****************************************/
	public Sentence connectWord(Sentence sentence) {
		// サ変動詞と接尾をもつ動詞をつなげる
		Stream.of(tags_CtgAdj).forEach(tag_CA -> {
			sentence.getChildren().forEach(c -> c.uniteAdjunct2Categorem(tag_CA[0], tag_CA[1]));			
		});
		// 名詞と形容詞だけ取り出す
		// これらがClauseの末尾につくものを隣のClauseにつなげる
		Stream.of(tags_NP).forEach(tag_NP -> {
			for (AbstractClause<?> matchedClause = sentence.findFirstClauseEndWith(tag_NP, true); 
					matchedClause != null; ) {
				if (!sentence.connect2Next(matchedClause)) 
					break;
				matchedClause = sentence.findFirstClauseEndWith(tag_NP, true);
			}
		});
		return sentence;
	}
	public void connectWord(SentenceIDMap sentenceMap) {
		sentenceMap.forEachKey(this::connectWord);	
	}
	
	/**
	 * 長文分割. 
	 * @param sentenceEntry 	キーが文，値がIDタプルのマップエントリ
	 * @return
	 */
	public SentenceIDMap divideSentence(Map.Entry<Sentence, IDTuple> sentenceEntry) {
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
	public void divideSentence(SentenceIDMap sentenceMap) {
		SentenceIDMap clone = new SentenceIDMap(sentenceMap);
		sentenceMap = clone.entrySet().stream()
				.map(this::divideSentence)
				.flatMap(m -> m.entrySet().stream())
				.peek(e -> e.getKey().uniteSubject())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, SentenceIDMap::new));
	}
}